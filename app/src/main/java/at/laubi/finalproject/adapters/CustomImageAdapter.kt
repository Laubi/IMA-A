package at.laubi.finalproject.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import at.laubi.finalproject.components.LoadingImage
import at.laubi.finalproject.imageUtilities.LoadableImage

class CustomImageAdapter(
        private val context: Context,
        private val images: List<LoadableImage>
): BaseAdapter(){

    override fun getView(index: Int, loadingImage: View?, vg: ViewGroup?): View {
        val target = images[index]

        val view = if(loadingImage != null){
            loadingImage as LoadingImage
            loadingImage
        }else{
            val newView = LoadingImage(context)
            newView.layoutParams = ViewGroup.LayoutParams(100, 100)
            newView.scaleType = ImageView.ScaleType.CENTER_CROP

            newView.extraSmallerRation = 2 // We load the images much smaller than we would need => Speed

            newView
        }

        view.image = target

        return view
    }

    override fun getItem(index: Int): Any {
        return images[index]
    }

    override fun getItemId(index: Int): Long {
        return images[index].id
    }

    override fun getCount(): Int {
        return images.size
    }
}