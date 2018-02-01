package at.laubi.finalproject.activities

import android.app.Activity
import android.os.Bundle
import at.laubi.finalproject.R

class ImageDisplayActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        actionBar.setDisplayHomeAsUpEnabled(true)

        val id = intent.extras.getLong("ID")
        val data = intent.extras.getString("DATA")


    }
}
