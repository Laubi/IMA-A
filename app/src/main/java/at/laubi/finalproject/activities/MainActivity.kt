package at.laubi.finalproject.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import at.laubi.finalproject.*
import at.laubi.finalproject.cache.DiskBitmapCache
import at.laubi.finalproject.adapters.LoadingImageAdapter
import at.laubi.finalproject.imageUtilities.*


class MainActivity : Activity() {
    private lateinit var layout: GridView
    private lateinit var adapter: LoadingImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize DiskBitmapCache
        DiskBitmapCache.initialize(this)

        val images = LoadableImage.all(contentResolver)

        adapter = LoadingImageAdapter(this, images)

        layout = this.findViewById(R.id.gridView)
        layout.adapter = adapter
        layout.numColumns = (screenSize.width / 100) + 1

        layout.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, ID ->
            val intent = Intent(this, ImageDisplayActivity::class.java)
            intent.putExtra("ID", ID)
            startActivity(intent)
        }
    }
}



