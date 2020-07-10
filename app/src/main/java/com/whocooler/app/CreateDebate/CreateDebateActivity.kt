package com.whocooler.app.CreateDebate

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.EXTRA_PICK_CATEGORY
import com.whocooler.app.Common.Utilities.RESULT_PICK_CATEGORY
import com.whocooler.app.R
import kotlinx.android.synthetic.main.activity_create_debate.*
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
    private lateinit var debateName: EditText
    private lateinit var categoryTextView: TextView

    private var fileLeftImage: File? = null
    private var fileRightImage: File? = null

    private var selectedCategory: Category? = null

    var isLeftImageSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_debate)
        setupModule()

        assignViewsById()
        setupOnClickListeners()
        setupActionBar()
    }

    private fun setupModule() {
        val activity = this
        val interactor = CreateDebateInteractor()
        val presenter = CreateDebatePresenter()
        val router = CreateDebateRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.presenter = presenter
        presenter.activity = activity
        router.activity = activity
    }

    private fun setupActionBar() {
        setSupportActionBar(findViewById(R.id.create_debate_toolbar))
        supportActionBar?.setTitle("")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun assignViewsById() {
        leftImage = findViewById(R.id.create_image_left)
        rightImage = findViewById(R.id.create_image_right)
        createButton = findViewById(R.id.create_create_button)
        leftName = findViewById(R.id.create_left_name_edit_text)
        rightName = findViewById(R.id.create_right_name_edit_text)
        debateName = findViewById(R.id.create_debate_name_edit_text)
        categoryTextView = findViewById(R.id.create_category)
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

        categoryTextView.setOnClickListener {
            router?.navigateToPickCategory()
        }

        createButton.setOnClickListener {
            if (App.prefs.isTokenEmpty()) {
                router?.navigateToAuth()
            } else if (fileLeftImage == null) {
                showAlertNotEnoughData(getString(R.string.create_left_image))
            } else if (fileRightImage == null) {
                showAlertNotEnoughData(getString(R.string.create_right_image))
            } else if (leftName.text.isEmpty()) {
                showAlertNotEnoughData(getString(R.string.create_left_side_name))
            } else if (rightName.text.isEmpty()) {
                showAlertNotEnoughData(getString(R.string.create_right_side_name))
            } else if (selectedCategory == null) {
                showAlertNotEnoughData(getString(R.string.create_category))
            } else {
                interactor?.createDebate(
                    leftName.text.toString(),
                    rightName.text.toString(),
                    fileLeftImage!!,
                    fileRightImage!!,
                    selectedCategory!!.id,
                    if (debateName.text.toString().isEmpty()) null else debateName.text.toString()
                )
            }
        }
    }

    private fun showAlertNotEnoughData(missingElement: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.create_not_enough_data) + missingElement)
        builder.setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener { _, _ ->  })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                updateUserImage(imageUri)
            }
        } else if (resultCode == RESULT_PICK_CATEGORY) {
            val category: Category? = data?.extras?.getParcelable(EXTRA_PICK_CATEGORY)
            if (category != null) {
                selectedCategory = category
                categoryTextView.text = category.name
                categoryTextView.setTextColor(Color.BLACK)
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
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
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

    // Handles navigation back
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
