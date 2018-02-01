package at.laubi.finalproject.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import at.laubi.finalproject.*
import at.laubi.finalproject.components.LoadingImage
import at.laubi.finalproject.imageUtilities.*


class MainActivity : Activity() {
    private lateinit var layout: GridView
    private val asyncImageLoaderListener: AsyncImageLoaderListener
    private lateinit var adapter: LoadingImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val images = findImages(this)

        adapter = LoadingImageAdapter(this, images.size)

        layout = this.findViewById(R.id.gridView)
        layout.adapter = adapter
        layout.numColumns = (getScreenSize(windowManager).widthPixels / 100) + 1


        images.forEach { image ->
            AsyncImageLoader(image, this, asyncImageLoaderListener).loadAsync()
        }

        layout.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            adapter.getData(pos)?.let {
                Toast.makeText(this, pos.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    init {
        asyncImageLoaderListener = object : AsyncImageLoaderListener {
            override fun finishedLoading(pair: ImagePair) {
                if (pair.bm != null) {
                    adapter.addItem(pair)
                }else{
                    adapter.decreseAmount()
                }

            }
        }
    }
}



private class LoadingImageAdapter(context: Context, initialCount: Int = 0): BaseAdapter(){
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

    fun decreseAmount(){
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