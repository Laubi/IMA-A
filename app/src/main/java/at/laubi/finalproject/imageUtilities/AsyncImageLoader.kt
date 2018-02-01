package at.laubi.finalproject.imageUtilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import at.laubi.finalproject.cache.DiskBitmapCache

/**
 * Created by dlaub on 21.01.2018.
 */

interface AsyncImageLoaderListener{
    fun finishedLoading(pair: ImagePair)
}

class ImagePair(val props: ImageFileProperties, val bm: Bitmap?)

class AsyncImageLoader constructor(val imageFileProperties: ImageFileProperties, val context: Context, var listener: AsyncImageLoaderListener?) {
    private val contentResolver = context.contentResolver
    private val targetSize = 100
    private val cache = DiskBitmapCache(context.cacheDir, true)

    fun loadAsync(){
        LoadImage().execute(imageFileProperties.uri)
    }

    private inner class LoadImage: AsyncTask<Uri, Void, ImagePair>() {
        override fun doInBackground(vararg params: Uri?): ImagePair {
            if (params.isEmpty()) return ImagePair(imageFileProperties, null)
            val param = params.first()
            val cacheId = imageFileProperties.id.toString(16)

            val cachedBitmap = cache.loadBitmap(cacheId)
            if(cachedBitmap != null) return ImagePair(imageFileProperties, cachedBitmap)

            val options = BitmapFactory.Options()
            options.inScaled = true
            options.inSampleSize = calculateSampleSize()

            val bm = BitmapFactory.decodeStream(contentResolver.openInputStream(param), null, options)

            cache.storeBitmap(bm, cacheId)

            return ImagePair(imageFileProperties, bm)
        }

        override fun onPostExecute(result: ImagePair) {
            listener?.finishedLoading(result)
        }
    }

    private fun calculateSampleSize(): Int{
        var sampleSize = 1

        val h = imageFileProperties.height
        val w = imageFileProperties.width

        if(h > targetSize || w > targetSize){
            val hh = h/2
            val hw = w/2

            while((hh / sampleSize) > targetSize && (hw / sampleSize) > targetSize){
                sampleSize *= 2
            }
        }


        return sampleSize
    }
}