package at.laubi.finalproject

import android.graphics.Point
import android.util.Size
import android.view.WindowManager

fun getScreenSize(wm: WindowManager): Size {
    val metrics = Point()
    wm.defaultDisplay.getSize(metrics)
    return Size(metrics.x, metrics.y)
}

fun Size.div(x: Int): Size{
    return Size(
            width / x,
            height / x
    )
}

