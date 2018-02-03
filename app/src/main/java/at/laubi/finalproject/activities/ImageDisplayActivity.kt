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

        val bundle = getBundle(this)
        val uri = Uri.fromFile(File(bundle.data))

        val loader = ImageLoader(this, bundle.id, uri, getScreenSize(windowManager))
        loader.callback = { bitmap ->
            bitmap?.let {
                targetBitmap = bitmap
                imageView.setImageBitmap(bitmap)
            }
        }
        loader.execute()
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


    class ActivityBundle(val id: Long, val data: String)

    companion object {

        fun buildBundle(id: Long, data: String): Bundle{
            val bundle = Bundle()
            bundle.putLong("ID", id)
            bundle.putString("DATA", data)

            return bundle
        }

        private fun getBundle(activity: ImageDisplayActivity): ActivityBundle{
            val bundle = activity.intent.extras

            return ActivityBundle(
                    bundle.getLong("ID"),
                    bundle.getString("DATA")
            )
        }


    }
}
