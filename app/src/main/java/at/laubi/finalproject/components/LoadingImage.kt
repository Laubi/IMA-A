package at.laubi.finalproject.components

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.widget.ImageView
import at.laubi.finalproject.imageUtilities.ImageLoadTask
import at.laubi.finalproject.imageUtilities.LoadableImage

class LoadingImage: ImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var currentTask: ImageLoadTask? = null
    private var mImage: LoadableImage? = null

    var extraSmallerRation = 1

    var image: LoadableImage?
        get() = mImage
        set(value) {
            if(currentTask != null){
                currentTask?.cancel = true
            }
            setLoadingIcon()
            currentTask = value?.load(Size(
                    layoutParams.width / extraSmallerRation,
                    layoutParams.height / extraSmallerRation
            ), this::setImageBitmap)
            mImage = value
        }

    init{
        setLoadingIcon()
    }

    private fun setLoadingIcon(){
        setImageDrawable(context.getDrawable(android.R.drawable.ic_menu_rotate))
    }
}