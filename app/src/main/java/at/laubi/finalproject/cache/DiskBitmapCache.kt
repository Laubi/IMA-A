package at.laubi.finalproject.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

class DiskBitmapCache(directory: File, createSubDirectory: Boolean = false) {
    private val cacheDirectory: File

    constructor(context: Context): this(context.cacheDir, true)

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

    companion object {
        private var m_instance: DiskBitmapCache? = null

        fun getInstance(context: Context): DiskBitmapCache{
            if (m_instance == null) {
                m_instance = DiskBitmapCache(context)
            }

           return m_instance as DiskBitmapCache
        }
    }
}