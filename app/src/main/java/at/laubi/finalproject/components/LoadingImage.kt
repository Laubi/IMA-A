package at.laubi.finalproject.components

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView

class LoadingImage: ImageView {
    private val animation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init{
        setImageDrawable(context.getDrawable(android.R.drawable.ic_menu_rotate))

        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.duration = 2000

        startAnimation(animation)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        clearAnimation()
        super.setImageBitmap(bm)
    }
}