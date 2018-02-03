package at.laubi.finalproject.imageUtilities

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore.Images.Media.*
import android.util.Size
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

class LoadableImage internal constructor(
        val id: Long,
        val width: Int,
        val height: Int,
        val data: String,
        private val contentResolver: ContentResolver
){
    val uri: Uri get() = Uri.fromFile(File(data))
    val cacheId: String get() = "$id-$width-$height"

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
