package at.laubi.finalproject

import android.app.Activity
import android.os.Bundle

class ImageDisplayActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        actionBar.setDisplayHomeAsUpEnabled(true)

        val id = intent.extras.getLong("ID")
        val data = intent.extras.getString("DATA")


    }
}
