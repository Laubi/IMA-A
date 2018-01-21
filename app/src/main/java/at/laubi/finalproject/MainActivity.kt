package at.laubi.finalproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*


class MainActivity : Activity() {
    private var layout: GridView? = null
    private val asyncImageLoaderListener: AsyncImageLoaderListener
    private var adapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val images = findImages(this)

        adapter = ImageAdapter(this, images.size)

        layout = this.findViewById(R.id.gridView)
        layout?.adapter = adapter
        layout?.numColumns = (getScreenSize(windowManager).widthPixels / 100) + 1


        images.forEach { image ->
            AsyncImageLoader(image, this, asyncImageLoaderListener).loadAsync()
        }
    }

    init {
        asyncImageLoaderListener = object : AsyncImageLoaderListener {
            override fun finishedLoading(pair: ImagePair) {
                if (pair.bm != null) {
                    adapter?.addItem(pair)
                    adapter?.notifyDataSetChanged()
                }

            }
        }
    }

    fun clickedImage(props: ImageFileProperties){
        val intent = Intent(this, ImageDisplayActivity::class.java)
        intent.putExtra("ID", props.id)
        intent.putExtra("DATA", props.data)
        startActivity(intent)
    }
}

fun getScreenSize(wm: WindowManager): DisplayMetrics{
    val metrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(metrics)
    return metrics
}

private class ImageAdapter(val parent: MainActivity, initialCount: Int = 0): BaseAdapter(){
    private val data = ArrayList<ImagePair>(initialCount)

    fun addItem(pair: ImagePair){
        data.add(pair)
    }

    override fun getView(pos: Int, convertView: View?, vg: ViewGroup?): View {
        val view: ImageView?

        if(convertView == null){
            view = ImageView(this.parent)
            view.layoutParams = AbsListView.LayoutParams(100, 100)
            view.setPadding(0,0,0,0)
            view.scaleType = ImageView.ScaleType.CENTER_CROP
        }else{
            view = convertView as ImageView
        }

        view.setImageBitmap(data[pos].bm)
        view.setOnClickListener { parent.clickedImage(data[pos].props) }

        return view
    }

    override fun getItem(index: Int): Any {
        return data.get(index)
    }

    override fun getItemId(index: Int): Long {
        return index +0L
    }

    override fun getCount(): Int {
        return data.size
    }
}