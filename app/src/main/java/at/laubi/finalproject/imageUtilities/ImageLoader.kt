package at.laubi.finalproject.imageUtilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Process
import android.util.Size
import at.laubi.finalproject.cache.DiskBitmapCache


class ImageLoader(private val context: Context, private val id: Long, private val uri: Uri, private val size: Size){
    var callback: (bm: Bitmap?) -> Unit = {}
    private val cacheId
        get() = "$id-${size.width}-${size.height}"

    private val cache = DiskBitmapCache.instance

    fun execute(){
        ImageLoaderExecutor().execute()
    }

    private inner class ImageLoaderExecutor: AsyncTask<Long, Void, Bitmap>(){
        override fun doInBackground(vararg p0: Long?): Bitmap {

            Process.setThreadPriority(-20)

            val cachedBitmap = cache?.loadBitmap(cacheId)
            if(cachedBitmap != null) return cachedBitmap

            var stream = context.contentResolver.openInputStream(uri)

            val options = BitmapFactory.Options()
            options.inScaled = true
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(stream, null, options)
            stream.close()

            stream = context.contentResolver.openInputStream(uri)
            options.inJustDecodeBounds = false
            options.inSampleSize = calculateSampleSize(Size(size.width/4, size.height/4), Size(options.outWidth, options.outHeight))
            val bm =  BitmapFactory.decodeStream(stream, null, options)
            stream.close()

            cache?.storeBitmap(bm, cacheId)

            return bm
        }

        override fun onPostExecute(result: Bitmap?) {
            callback(result)
        }

        private fun calculateSampleSize(target: Size, input: Size): Int{
            var sampleSize = 1

            if(input.height > target.height || input.width > target.width){
                val hh = input.height / 2
                val hw = input.width / 2

                while(
                        (hh / sampleSize) > target.height &&
                        (hw / sampleSize) > target.width){
                    sampleSize *= 2
                }
            }

            return sampleSize
        }
    }



}