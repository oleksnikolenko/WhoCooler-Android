package com.whocooler.app.UserProfile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.whocooler.app.Common.Models.User
import com.whocooler.app.R
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URI

class UserProfileActivity: AppCompatActivity(), UserProfileContracts.PresenterViewContract {

    lateinit var interactor: UserProfileContracts.ViewInteractorContract
    lateinit var router: UserProfileContracts.RouterInterface
    private var user: User? = null

    companion object {
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }

    private fun setupModule() {
        var activity = this
        var interactor = UserProfileInteractor()
        var presenter = UserProfilePresenter()
        var router = UserProfileRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.presenter = presenter
        presenter.output = activity
        router.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupModule()
        setContentView(R.layout.activity_user_profile)

        setupOnClickListeners()
        interactor.getProfile()
    }

    private fun setupOnClickListeners() {
        val changeName = findViewById<TextView>(R.id.profile_edit_name)
        changeName.setOnClickListener {
            showEditNameAlert()
        }

        val changeAvatar = findViewById<TextView>(R.id.profile_change_avatar)
        changeAvatar.setOnClickListener {
            tryToGetImageFromUser()
        }
    }

    private fun tryToGetImageFromUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        } else {
            //system OS is < Marshmallow
            pickImageFromGallery();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                updateUserImage(imageUri)
            }
        }
    }

    @Throws(IOException::class)
    private fun updateUserImage(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            interactor.updateUserAvatar(it.readBytes())
        }
    }

    private fun showEditNameAlert() {
        var text = ""
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit name")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(user?.name)
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            interactor.updateUserName(input.text.toString())
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        val dialog = builder.create()

        dialog.show()
    }

    override fun displayProfile(user: User) {
        this.user = user

        val userAvatarView = findViewById<ImageView>(R.id.rounded_user_image)
        val namePlaceholder = findViewById<TextView>(R.id.profile_name_placeholder)
        val userName = findViewById<TextView>(R.id.profile_name_txt)
        val logoutBtn = findViewById<Button>(R.id.user_profile_logout_btn)
        val privacyPolicyTextView = findViewById<TextView>(R.id.profile_privacy_policy)

        Picasso.get().load(user.avatar).into(userAvatarView)
        namePlaceholder.text = "Name"
        userName.text = user.name
        logoutBtn.text = "Logout"
        privacyPolicyTextView.text = "Privacy policy"

        logoutBtn.setOnClickListener {
            interactor.logout()
        }

        privacyPolicyTextView.setOnClickListener {
            openUrl("https://www.iubenda.com/privacy-policy/66454455", this)
        }
    }

    private fun openUrl(url: String, context: Context) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun navigateToAuth() {
        router?.navigateToAuth()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

}