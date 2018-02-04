package at.laubi.finalproject.activities

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import at.laubi.finalproject.R
import at.laubi.finalproject.getScreenSize
import at.laubi.finalproject.imageUtilities.ImageLoader
import at.laubi.finalproject.imageUtilities.LoadableImage
import nl.dionsegijn.pixelate.Pixelate
import java.io.File

class ImageDisplayActivity : Activity() {
    private lateinit var imageView: ImageView
    private lateinit var targetBitmap: Bitmap

    private lateinit var gestureDetector: GestureDetector
    private val gestureListener: GestureDetector.OnGestureListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)
        actionBar.setDisplayHomeAsUpEnabled(true)

        imageView = findViewById(R.id.imageView)
        gestureDetector = GestureDetector(this, gestureListener)


        val image = LoadableImage.byId(contentResolver, intent.extras.getLong("ID"))

        image?.load(getScreenSize(windowManager), imageView::setImageBitmap)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    init{
        gestureListener = object: GestureDetector.SimpleOnGestureListener(){
            override fun onScroll(initial: MotionEvent?, current: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {

                if(distanceX > distanceY){
                    current?.let{
                        val x_move = it.x.div(getScreenSize(windowManager).width)

                        pixelateImage((targetBitmap.width * x_move).toInt())
                    }
                }

                return true
            }
        }
    }

    private fun pixelateImage(blocks: Int){
        Pixelate(targetBitmap)
                .setDensity(100)
                .setListener { bitmap, density ->
                    targetBitmap = bitmap
                    imageView.setImageBitmap(bitmap)
                }
                .make()
    }
}
