package at.laubi.finalproject.imageUtilities

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore.Images.Media.*
import android.util.Size
import at.laubi.finalproject.cache.DiskBitmapCache
import java.io.File
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


private val projection = arrayOf(_ID, DATA, HEIGHT, WIDTH)
private const val sortOrder = DEFAULT_SORT_ORDER

private fun resolveImages(contentResolver: ContentResolver, selection: String?, args: Array<String>?): List<LoadableImage>{

    val imageCursor = contentResolver.query(
            EXTERNAL_CONTENT_URI,
            projection,
            selection,
            args,
            sortOrder
    )

    val results = ArrayList<LoadableImage>(imageCursor.count)

    val index = object {
        val id = imageCursor.getColumnIndex(_ID)
        val data = imageCursor.getColumnIndex(DATA)
        val width = imageCursor.getColumnIndex(WIDTH)
        val height = imageCursor.getColumnIndex(HEIGHT)
    }

    while(imageCursor.moveToNext()){

        results += LoadableImage(
                imageCursor.getLong(index.id),
                imageCursor.getInt(index.width),
                imageCursor.getInt(index.height),
                imageCursor.getString(index.data),
                contentResolver
        )
    }

    imageCursor.close()

    return results
}

class ImageLoadTask internal constructor(
        val contentResolver: ContentResolver,
        val image: LoadableImage,
        val targetSize: Size,
        val listener: (bm: Bitmap?) -> Unit
){
    var cancel = false

    fun load(){
        Executor().executeOnExecutor(executor)
    }

    val cacheId = "${image.id}-${targetSize.width}-${targetSize.height}"

    // We create an inner class, so the AsyncTask will not leak outside
    private inner class Executor: AsyncTask<Void, Void, Bitmap?>() {
        override fun onPostExecute(result: Bitmap?) {
            if(!cancel)
                listener(result)
        }

        override fun doInBackground(vararg p0: Void?): Bitmap? {
            if(cancel) return null
            val cachedBitmap = tryLoadBitmapFromCache(cacheId)

            return if(cachedBitmap != null){
                cachedBitmap
            }else{
                val options = BitmapFactory.Options()
                options.inScaled = true
                options.inSampleSize = calculateSampleSize(targetSize)

                val stream = contentResolver.openInputStream(image.uri)
                if(cancel) return null
                val bm = BitmapFactory.decodeStream(stream, null, options)
                stream.close()

                tryStoreBitmapInCache(cacheId, bm)


                bm
            }
        }
    }

    private fun tryLoadBitmapFromCache(key: String): Bitmap?{
        return DiskBitmapCache.instance?.loadBitmap(key)
    }

    private fun tryStoreBitmapInCache(key: String, bitmap: Bitmap){
        DiskBitmapCache.instance?.storeBitmap(bitmap, key)
    }

    private fun calculateSampleSize(target: Size): Int{
        var sampleSize = 1

        if(image.height > target.height || image.width > target.width){
            val hh = image.height / 2
            val hw = image.width / 2

            while(
                    (hh / sampleSize) > target.height &&
                    (hw / sampleSize) > target.width){
                sampleSize *= 2
            }
        }

        return sampleSize
    }

    private companion object {
        val executor = ThreadPoolExecutor(0xf, 0xff, 10, TimeUnit.SECONDS, LinkedBlockingDeque<Runnable>());
    }
}



class LoadableImage internal constructor(
        val id: Long,
        val width: Int,
        val height: Int,
        val data: String,
        private val contentResolver: ContentResolver
){
    val uri: Uri get() = Uri.fromFile(File(data))

    fun load(size: Size, listener: (bm: Bitmap?) -> Unit): ImageLoadTask {
        val loader = ImageLoadTask(contentResolver, this, size, listener)
        loader.load()
        return loader
    }

    companion object Factory{

        fun all(contentResolver: ContentResolver): List<LoadableImage>{
            return resolveImages(contentResolver, null, null)
        }

        fun byId(contentResolver: ContentResolver, id: Long): LoadableImage?{
            return resolveImages(
                    contentResolver,
                    "_id = ?",
                    arrayOf(id.toString())
            ).firstOrNull()
        }

    }
}
