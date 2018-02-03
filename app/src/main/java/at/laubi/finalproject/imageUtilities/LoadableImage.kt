package at.laubi.finalproject.imageUtilities

import android.content.AsyncTaskLoader
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore.Images.Media.*
import android.util.Size
import at.laubi.finalproject.cache.DiskBitmapCache
import java.io.File


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

internal class LoadableImageLoader constructor(
        val contentResolver: ContentResolver,
        val image: LoadableImage,
        val targetSize: Size,
        val listener: (bm: Bitmap?) -> Unit
){
    fun load(){
        Executor().execute()
    }

    // We create an inner class, so the AsyncTask will not leak outside
    private inner class Executor: AsyncTask<Void, Void, Bitmap>() {
        override fun onPostExecute(result: Bitmap?) {
            listener(result)
        }

        override fun doInBackground(vararg p0: Void?): Bitmap {
            val cachedBitmap = tryLoadBitmapFromCache(image.cacheId)

            return if(cachedBitmap != null){
                cachedBitmap
            }else{
                val options = BitmapFactory.Options()
                options.inScaled = true
                options.inSampleSize = calculateSampleSize(targetSize)

                val stream = contentResolver.openInputStream(image.uri)
                val bm = BitmapFactory.decodeStream(stream, null, options)
                stream.close()

                tryStoreBitmapInCache(image.cacheId, bm)

                bm
            }
        }
    }

    private fun tryLoadBitmapFromCache(key: String): Bitmap?{
        val cache = DiskBitmapCache.getInstance()
        return cache?.loadBitmap(key)
    }

    private fun tryStoreBitmapInCache(key: String, bitmap: Bitmap){
        val cache = DiskBitmapCache.getInstance()
        cache?.storeBitmap(bitmap, key)
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
}



class LoadableImage internal constructor(
        val id: Long,
        val width: Int,
        val height: Int,
        val data: String,
        private val contentResolver: ContentResolver
){
    val uri: Uri get() = Uri.fromFile(File(data))
    val cacheId: String get() = "$id-$width-$height"

    fun load(size: Size, listener: (bm: Bitmap?) -> Unit){
        LoadableImageLoader(contentResolver, this, size, listener)
                .load()
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
