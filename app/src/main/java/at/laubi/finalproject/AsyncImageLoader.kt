package at.laubi.finalproject

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import java.io.File

/**
 * Created by dlaub on 21.01.2018.
 */

private val MODE_READ = "r"

interface AsyncImageLoaderListener{
    fun finishedLoading(pair: ImagePair)
}

class ImagePair(val props: ImageFileProperties, val bm: Bitmap?)

class AsyncImageLoader constructor(val imageFileProperties: ImageFileProperties, context: Context, var listener: AsyncImageLoaderListener) {
    private val contentResolver = context.contentResolver
    private val targetSize = 100

    fun loadAsync(){
        LoadImage().execute(imageFileProperties.uri)
    }

    private inner class LoadImage: AsyncTask<Uri, Void, ImagePair>() {
        override fun doInBackground(vararg params: Uri?): ImagePair {
            if (params.isEmpty()) return ImagePair(imageFileProperties, null)

            val param = params.first()
            val assetFileDescriptor = contentResolver.openFileDescriptor(param, MODE_READ)

            val options = BitmapFactory.Options()
            options.inScaled = true
            options.inSampleSize = calculateSampleSize()

            val bm = BitmapFactory.decodeStream(contentResolver.openInputStream(param), null, options)


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