package at.laubi.finalproject.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import at.laubi.finalproject.*
import nl.dionsegijn.pixelate.Pixelate
import kotlin.math.absoluteValue

class InteractiveImageView : GestureImageView{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var originalBitmap: Bitmap? = null
    private var lastColorFilter: ColorFilter? = null

    override fun setImageBitmap(bm: Bitmap?) {
        originalBitmap = bm

        super.setImageBitmap(bm)
    }

    override fun onFling(initial: MotionEvent?, current: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if(current == null) return false
        if(!current.position.inside(size)) return false

        val posPercent = current.position / size

        val velocity = PointF(velocityX.absoluteValue, velocityY.absoluteValue)


        if(velocity.x > velocity.y){
            // Saturation
            val matrix = ColorMatrix()
            matrix.setSaturation(posPercent.x)

            lastColorFilter = ColorMatrixColorFilter(matrix)

            drawable.colorFilter = lastColorFilter //Color filter is applied async
        }else{
            //Pixelation
            pixelateImage((200 * posPercent.y).toInt()) // This time we have to do it async on our own
        }

        return true
    }



    private fun pixelateImage(blocks: Int){
        originalBitmap?.let {
            Pixelate(it)
                .setDensity(blocks)
                .setListener { bitmap, _ ->
                    super.setImageBitmap(bitmap)
                    lastColorFilter?. let { drawable.colorFilter = it }
                }
                .make()
        }
    }
}