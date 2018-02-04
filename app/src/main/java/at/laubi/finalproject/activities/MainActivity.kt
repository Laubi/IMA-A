package at.laubi.finalproject.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.*
import at.laubi.finalproject.*
import at.laubi.finalproject.cache.DiskBitmapCache
import at.laubi.finalproject.adapters.CustomImageAdapter
import at.laubi.finalproject.imageUtilities.*

private const val PERMISSION_REQUEST_CODE = 0x6482
private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(checkPermissions()){
            showImageContents()
        }else{
            showNeedPermissions()
        }
    }

    private fun showNeedPermissions(){
        setContentView(R.layout.activity_main_no_permission)

        val button = findViewById<Button>(R.id.requestPermissionsButton)
        button.setOnClickListener {
            checkPermissions()
        }
    }

    private fun showImageContents(){
        setContentView(R.layout.activity_main)

        // Initialize DiskBitmapCache
        DiskBitmapCache.initialize(this)

        val images = LoadableImage.all(contentResolver)

        val adapter = CustomImageAdapter(this, images)

        val layout = this.findViewById<GridView>(R.id.gridView)
        layout.adapter = adapter
        layout.numColumns = (screenSize.width / 100) + 1

        layout.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, ID ->
            val intent = Intent(this, ImageDisplayActivity::class.java)
            intent.putExtra("ID", ID)
            startActivity(intent)
        }
    }

    private fun checkPermissions(): Boolean{
        val hasAllRequiredPermissions =
                REQUIRED_PERMISSIONS
                        .map(this::checkSelfPermission).none { result -> result ==  PERMISSION_DENIED }

        if(!hasAllRequiredPermissions){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }

        return hasAllRequiredPermissions
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_REQUEST_CODE -> {
                if( grantResults.isNotEmpty() && grantResults.first() == PERMISSION_GRANTED){
                    showImageContents()
                }else{
                    showNeedPermissions()
                }
            }
        }

    }
}



