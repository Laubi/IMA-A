package at.laubi.finalproject.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

class DiskBitmapCache private constructor(context: Context) {
    private val cacheDirectory: File = File(context.cacheDir.absolutePath + File.separator + ".cache")

    init{
        cacheDirectory.mkdirs()
    }

    fun storeBitmap(bitmap: Bitmap, key: String){
        val target = getTargetFile(key)

        if(target.exists()){
            target.delete()
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(target))

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
        var instance: DiskBitmapCache? = null

        fun initialize(context: Context): DiskBitmapCache{
            if (instance == null) {
                instance = DiskBitmapCache(context)
            }

           return instance as DiskBitmapCache
        }
    }
}