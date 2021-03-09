package com.projemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.common.io.Files
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.projemanag.R
import com.projemanag.databinding.ActivityCreateBoardBinding
import com.projemanag.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.projemanag.utils.Constants.READ_STORAGE_PERMISSION_CODE
import java.lang.Exception

class CreateBoardActivity : BaseActivity() {


    private var mBoardImageURL: String = ""
    private var mSelectedImageFileUri: Uri? = null

    private lateinit var binding: ActivityCreateBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        setupActionBar()

        binding.ivBoardImage.setOnClickListener {
            if(ContextCompat
                    .checkSelfPermission(this
                        , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                showImageChooser()
            } else {

                ActivityCompat.requestPermissions(this
                    , arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    , READ_STORAGE_PERMISSION_CODE)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null) {

            mSelectedImageFileUri = data!!.data

            try {

                Glide
                    .with(this)
                    .load(Uri.parse(mSelectedImageFileUri.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivBoardImage)
            } catch (e: Exception) {

                Toast.makeText(this
                    ,"An error has occurred while uploading image. Please try again"
                    ,Toast.LENGTH_LONG)
                    .show()


                Log.e("ON_ACTIVITY_RESULT", e.message.toString())
            }
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun uploadUserImage() {

        showProgressDialog("Please wait...")

        if(mSelectedImageFileUri != null) {

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE${System.currentTimeMillis()}.${getFileExtension(mSelectedImageFileUri)}"
            )

            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapShot ->
                    Log.e(
                        "Firebase Image URL",
                        taskSnapShot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    taskSnapShot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Board image Url", uri.toString())

                            mBoardImageURL = uri.toString()

                        }
                }
                .addOnFailureListener { exception ->

                    hideProgressDialog()

                    Toast.makeText(
                        this
                        ,exception.message
                        ,Toast.LENGTH_LONG
                    ).show()

                    Log.e("FAILURE_BOARD_UPLOAD", exception.message.toString())
                }
        }

    }

    private fun showImageChooser() {

        var galleryIntent = Intent(
            Intent.ACTION_PICK
            , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCreateBoardActivity)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        binding.toolbarCreateBoardActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == READ_STORAGE_PERMISSION_CODE)
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                showImageChooser()
            else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }


    }


}