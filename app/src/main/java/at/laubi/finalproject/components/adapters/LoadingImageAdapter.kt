package at.laubi.finalproject.components.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import at.laubi.finalproject.components.LoadingImage
import at.laubi.finalproject.imageUtilities.ImagePair

class LoadingImageAdapter(context: Context, initialCount: Int = 0): BaseAdapter(){
    private val views = ArrayList<LoadingImage>(initialCount)
    private val data = ArrayList<ImagePair>(initialCount)
    private var amount = initialCount

    private companion object {
        val layoutParams = AbsListView.LayoutParams(100, 100)
    }

    init{
        for(i in 0.rangeTo(initialCount)){
            val view = LoadingImage(context)
            view.layoutParams = layoutParams
            view.setPadding(0,0,0,0)
            view.scaleType = ImageView.ScaleType.CENTER_CROP
            views.add(view)
        }
    }

    fun decreaseAmount(){
        amount -= 1
        notifyDataSetChanged()
    }

    fun addItem(pair: ImagePair){
        data.add(pair)
        val index = data.indexOf(pair)

        val view = views[index]
        view.setImageBitmap(pair.bm)
        view.invalidate()
    }

    override fun getView(pos: Int, convertView: View?, vg: ViewGroup?): View {
        return views[pos]
    }

    override fun getItem(index: Int): Any {
        return views[index]
    }

    fun getData(index: Int): ImagePair?{
        return if(index < data.size) data[index] else null
    }

    override fun getItemId(index: Int): Long {
        return index + 0L
    }

    override fun getCount(): Int {
        return amount
    }
}