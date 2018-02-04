package at.laubi.finalproject.activities

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Size
import android.widget.ImageView
import at.laubi.finalproject.R
import at.laubi.finalproject.imageUtilities.LoadableImage
import at.laubi.finalproject.screenSize

class ImageDisplayActivity : Activity() {
    private lateinit var imageView: ImageView
    private lateinit var targetBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)
        actionBar.setDisplayHomeAsUpEnabled(true)

        imageView = findViewById(R.id.imageView)

        val image = LoadableImage.byId(contentResolver, intent.extras.getLong("ID"))

        val targetSize = screenSize

        image?.load(Size(targetSize.width / 2, targetSize.height / 2), { bm ->
            bm?.let{
                targetBitmap = it
                imageView.setImageBitmap(it)
            }
        })
    }
}
