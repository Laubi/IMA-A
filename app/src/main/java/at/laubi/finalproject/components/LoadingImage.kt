package at.laubi.finalproject.components

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class LoadingImage: ImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init{
        setImageDrawable(context.getDrawable(android.R.drawable.ic_menu_rotate))
    }
}