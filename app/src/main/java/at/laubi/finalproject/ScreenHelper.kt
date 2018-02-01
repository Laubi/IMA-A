package at.laubi.finalproject

import android.util.DisplayMetrics
import android.view.WindowManager

fun getScreenSize(wm: WindowManager): DisplayMetrics {
    val metrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(metrics)
    return metrics
}
