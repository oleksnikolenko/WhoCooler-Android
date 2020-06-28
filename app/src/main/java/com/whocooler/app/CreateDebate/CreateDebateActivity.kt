package com.whocooler.app.CreateDebate

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.R
import java.io.File
import java.io.IOException


class CreateDebateActivity : AppCompatActivity(), CreateDebateContracts.PresenterViewContract {

    companion object {
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }

    var interactor: CreateDebateContracts.ViewInteractorContract? = null
    var router: CreateDebateContracts.RouterInterface? = null

    private lateinit var leftImage: ImageView
    private lateinit var rightImage: ImageView
    private lateinit var createButton: Button
    private lateinit var leftName: EditText
    private lateinit var rightName: EditText
    private lateinit var picker: NumberPicker

    private var fileLeftImage: File? = null
    private var fileRightImage: File? = null

    private lateinit var categories: ArrayList<Category>

    var isLeftImageSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_debate)
        setupModule()

        interactor?.getCategoryList()

        assignViewsById()
        setupOnClickListeners()
    }

    private fun setupModule() {
        var activity = this
        val interactor = CreateDebateInteractor()
        val presenter = CreateDebatePresenter()
        val router = CreateDebateRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.presenter = presenter
        presenter.activity = activity
        router.activity = activity
    }

    override fun displayCategories(categories: ArrayList<Category>) {
        this.categories = categories
        picker.minValue = 0
        picker.maxValue = categories.count() - 1
        picker.displayedValues = categories.map {it.name}.toTypedArray()
    }

    private fun assignViewsById() {
        leftImage = findViewById(R.id.create_image_left)
        rightImage = findViewById(R.id.create_image_right)
        createButton = findViewById(R.id.create_create_button)
        leftName = findViewById(R.id.create_left_name_edit_text)
        rightName = findViewById(R.id.create_right_name_edit_text)
        picker = findViewById(R.id.create_picker)
    }

    private fun setupOnClickListeners() {
        leftImage.setOnClickListener {
            isLeftImageSelected = true
            tryToGetImageFromUser()
        }

        rightImage.setOnClickListener {
            isLeftImageSelected = false
            tryToGetImageFromUser()
        }

        createButton.setOnClickListener {
            if (App.prefs.isTokenEmpty()) {
                router?.navigateToAuth()
            } else if (fileLeftImage != null && fileRightImage != null && leftName.text.isNotEmpty()
                && rightName.text.isNotEmpty()) {
                interactor?.createDebate(
                    leftName.text.toString(),
                    rightName.text.toString(),
                    fileLeftImage!!,
                    fileRightImage!!,
                    categories.get(picker.value).id
                )
            } else {
                showAlertNotEnoughData()
            }
        }
    }

    private fun showAlertNotEnoughData() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Not enough data, please fill all the fields")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->  })
        builder.create().show()
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

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    @Throws(IOException::class)
    private fun updateUserImage(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            val imageStream = contentResolver.openInputStream(uri)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            if (isLeftImageSelected) {
                fileLeftImage = File(getRealPathFromURI(uri))
                leftImage.setImageBitmap(selectedImage)
                leftImage.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(0, 0, 0, 0)
                    weight = 0.5f
                }
            } else {
                fileRightImage = File(getRealPathFromURI(uri))
                rightImage.setImageBitmap(selectedImage)
                rightImage.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(0, 0, 0, 0)
                    weight = 0.5f
                }
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        startActivityForResult(intent, CreateDebateActivity.IMAGE_PICK_CODE)
    }

    override fun navigateToDebate(debate: Debate) {
        router?.navigateToDebate(debate)
    }

    override fun showError(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }
}
