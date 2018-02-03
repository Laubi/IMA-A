package at.laubi.finalproject.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import at.laubi.finalproject.*
import at.laubi.finalproject.components.adapters.LoadingImageAdapter
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
        layout.numColumns = (getScreenSize(windowManager).width / 100) + 1


        images.forEach { image ->
            AsyncImageLoader(image, this, asyncImageLoaderListener).loadAsync()
        }

        layout.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            adapter.getData(pos)?.let {
                callImageDisplayActivity(it.props)
            }
        }
    }

    init {
        asyncImageLoaderListener = object : AsyncImageLoaderListener {
            override fun finishedLoading(pair: ImagePair) {
                if (pair.bm != null) {
                    adapter.addItem(pair)
                }else{
                    adapter.decreaseAmount()
                }

            }
        }
    }

    private fun callImageDisplayActivity(props: ImageFileProperties){
        val intent = Intent(this, ImageDisplayActivity::class.java)
        intent.putExtras(ImageDisplayActivity.buildBundle(props.id, props.data))
        startActivity(intent)
    }
}



