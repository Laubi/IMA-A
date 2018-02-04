package at.laubi.finalproject

import android.app.Activity
import android.graphics.Point
import android.graphics.PointF
import android.util.Size
import android.view.MotionEvent
import android.view.View

fun Size.div(x: Int): Size {
    return Size(
            width / x,
            height / x
    )
}

val Activity.screenSize: Size
    get() {
        val point = android.graphics.Point()
        windowManager.defaultDisplay.getSize(point)
        return android.util.Size(point.x, point.y)
    }

val View.size: Size
    get() = Size( width, height)

val View.position
    get() = PointF(x, y)

val MotionEvent.position
    get() = PointF(x, y)

operator fun PointF.minus(other: PointF): PointF{
    return PointF(
            x - other.x,
            y - other.y
    )
}

fun PointF.inside(size: Size): Boolean{
    return x >= 0 && y >= 0 && x < size.width && y < size.height
}

operator fun PointF.div(size: Size): PointF{
    return PointF(
            x / size.width,
            y / size.height
    )
}