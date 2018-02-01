package at.laubi.finalproject.imageUtilities

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images.Media.*
import android.provider.MediaStore
import java.io.File

class ImageFileProperties(val id: Long, val data: String, val width: Int, val height: Int){
    val uri = Uri.fromFile(File(data))
}

private val projection = arrayOf(_ID, DATA, HEIGHT, WIDTH)

fun findImages(context: Context): ArrayList<ImageFileProperties>{
    val imageIds = ArrayList<ImageFileProperties>(0)

    val images = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
        null,
        null,
        MediaStore.Images.Media.DEFAULT_SORT_ORDER
    )

    imageIds.ensureCapacity(images.count)

    val indexId = images.getColumnIndex(_ID)
    val dataId = images.getColumnIndex(DATA)
    val heightId = images.getColumnIndex(HEIGHT)
    val widthId = images.getColumnIndex(WIDTH)

    while(images.moveToNext()){
        val id = images.getLong(indexId)
        val data = images.getString(dataId)
        val height = images.getInt(heightId)
        val width = images.getInt(widthId)
        imageIds.add(ImageFileProperties(id, data, width, height))
    }

    images.close()

    return imageIds
}