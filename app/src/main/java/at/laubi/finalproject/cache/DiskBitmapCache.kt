package at.laubi.finalproject.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

/**
 * Created by dlaubreiter on 30.01.18.
 */
class DiskBitmapCache(directory: File, createSubDirectory: Boolean = false) {
    private val cacheDirectory: File

    init{
        if(!directory.exists()){
            directory.mkdir()
        }else if (!directory.isDirectory){
            throw IllegalArgumentException("Cache-directory is not a directory: '$directory'") as Throwable
        }

        cacheDirectory = if(createSubDirectory){
            File(directory.absolutePath + File.separator + ".cache")
        }else directory

        cacheDirectory.mkdir()
    }

    fun storeBitmap(bitmap: Bitmap, key: String){
        val target = getTargetFile(key)

        if(target.exists()){
            target.delete()
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(target))

    }

    fun loadBitmap(key: String): Bitmap?{
        val target = getTargetFile(key)

        if(!target.exists()) return null

        return BitmapFactory.decodeFile(target.absolutePath)
    }

    private fun getTargetFile(key: String): File{
        return File(cacheDirectory.absolutePath + File.separator + key)
    }
}