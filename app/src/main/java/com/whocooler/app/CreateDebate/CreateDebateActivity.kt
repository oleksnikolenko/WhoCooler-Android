package com.whocooler.app.CreateDebate

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Helpers.FileUtils
import com.whocooler.app.Common.Helpers.PendingDownloadContent
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.EXTRA_PICK_CATEGORY
import com.whocooler.app.Common.Utilities.RESULT_PICK_CATEGORY
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.R
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class CreateDebateActivity : AppCompatActivity(), CreateDebateContracts.PresenterViewContract {

    companion object {
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1002;
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
    private lateinit var debateTypeTextView: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var leftImageContainer: ConstraintLayout
    private lateinit var leftImageText: TextView
    private lateinit var rightImageContainer: ConstraintLayout
    private lateinit var rightImageText: TextView

    private var isRequestInProgress = false

    private var fileLeftImage: ByteArray? = null
    private var fileRightImage: ByteArray? = null

    private var selectedCategory: Category? = null
    private var debateType: String = "sides"

    var isLeftImageSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

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
        debateTypeTextView = findViewById(R.id.create_debate_type)
        leftImageContainer = findViewById(R.id.create_image_left_container)
        rightImageContainer = findViewById(R.id.create_image_right_container)
        leftImageText = findViewById(R.id.create_left_image_text)
        rightImageText = findViewById(R.id.create_right_image_text)
        progressBar = findViewById(R.id.create_progress_bar)
        debateTypeTextView.text = getString(R.string.create_debate_type_sides_type)
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

        debateTypeTextView.setOnClickListener {
            showDebateTypeAlert()
        }

        createButton.setOnClickListener {
            if (debateType == "sides") {
                handleSidesTap()
            } else if (debateType == "statement") {
                handleStatementTap()
            }
        }
    }

    private fun showAlertNotEnoughData(missingElement: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.create_not_enough_data) + missingElement)
        builder.setPositiveButton(
            getString(R.string.ok),
            DialogInterface.OnClickListener { _, _ -> })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                updateUserImage(data)
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

    @Throws(IOException::class)
    private fun updateUserImage(data: Intent?) {
        val clipData = data?.clipData
        val path: Any? = if (clipData != null) {
            val paths = ArrayList<Any>()
            for (i in 0 until clipData.itemCount) {
                val path = FileUtils.getPath(this, clipData.getItemAt(i).uri)
                if (path != null) {
                    paths.add(path)
                }
            }
            paths.firstOrNull()
        } else {
            FileUtils.getPath(this, data?.data)
        }

        val file = when (path) {
            is PendingDownloadContent -> {
                val fileName = UUID.randomUUID().toString() + path.fileName
                FileUtils.copyUriToFile(this, path.uri, fileName)
            }
            is String -> {
                File(path)
            }
            else -> null
        } ?: return

        val uri = Uri.fromFile(file)
        val imageStream = contentResolver.openInputStream(uri)
        var selectedImage = BitmapFactory.decodeStream(imageStream)

        selectedImage = modifyOrientation(selectedImage, file.absolutePath)

        if (isLeftImageSelected) {
            fileLeftImage = bitmapToByteArray(selectedImage)

            leftImage.setImageBitmap(selectedImage)
            leftImage.setBackgroundColor(0x00000000)

            leftImageText.visibility = View.GONE
        } else {
            fileRightImage = bitmapToByteArray(selectedImage)

            rightImage.setImageBitmap(selectedImage)
            rightImage.setBackgroundColor(0x00000000)

            rightImageText.visibility = View.GONE
        }
    }

    private fun tryToGetImageFromUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                        .show()
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
        progressBar.visibility = View.GONE
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

    private fun showDebateTypeAlert() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.create_choose_type))

        val debateTypes = arrayOf(
            getString(R.string.create_debate_type_sides),
            getString(R.string.create_debate_type_statement)
        )

        val debateTypesForBackend = arrayOf("sides", "statement")

        builder.setItems(debateTypes) { _, which ->
            debateType = debateTypesForBackend[which]
            setupDebateTypeLayout(debateType)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun setupDebateTypeLayout(type: String) {
        if (type == "sides") {
            debateName.hint = getString(R.string.create_debate_name_optional)
            leftName.hint = getString(R.string.create_left_side_name)
            rightName.hint = getString(R.string.create_right_side_name)
            rightImageContainer.visibility = View.VISIBLE
            debateTypeTextView.text = getString(R.string.create_debate_type_sides_type)

            leftImageContainer.layoutParams = LinearLayout.LayoutParams(
                0,
                dip(150)
            ).apply {
                weight = 0.5f
                setMargins(0, dip(12), dip(1), 0)
            }

            rightImageContainer.layoutParams = LinearLayout.LayoutParams(
                0,
                dip(150)
            ).apply {
                weight = 0.5f
                setMargins(dip(1), dip(12), 0, 0)
            }
        } else {
            rightImageContainer.visibility = View.GONE
            debateName.hint = getString(R.string.create_debate_name_required)
            leftName.hint = getString(R.string.create_left_side_name_short)
            rightName.hint = getString(R.string.create_right_side_name_short)
            debateTypeTextView.text = getString(R.string.create_debate_type_statement_type)

            leftImageContainer.layoutParams = LinearLayout.LayoutParams(
                0,
                dip(225)
            ).apply {
                weight = 1f
                setMargins(0, dip(12), 0, 0)
            }
        }
    }

    private fun handleSidesTap() {
        if (isRequestInProgress) {
            return
        } else if (App.prefs.isTokenEmpty()) {
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
            interactor?.createDebateSides(
                leftName.text.toString(),
                rightName.text.toString(),
                fileLeftImage!!,
                fileRightImage!!,
                selectedCategory!!.id,
                if (debateName.text.toString().isEmpty()) null else debateName.text.toString()
            )

            isRequestInProgress = true

            progressBar.visibility = View.VISIBLE
        }
    }

    private fun handleStatementTap() {
        if (isRequestInProgress) {
            return
        } else if (App.prefs.isTokenEmpty()) {
            router?.navigateToAuth()
        } else if (fileLeftImage == null) {
            showAlertNotEnoughData(getString(R.string.create_debate_image))
        } else if (debateName.text.isEmpty()) {
            showAlertNotEnoughData(getString(R.string.create_debate_name_required))
        } else if (leftName.text.isEmpty()) {
            showAlertNotEnoughData(getString(R.string.create_left_side_name))
        } else if (rightName.text.isEmpty()) {
            showAlertNotEnoughData(getString(R.string.create_right_side_name))
        } else if (selectedCategory == null) {
            showAlertNotEnoughData(getString(R.string.create_category))
        } else {
            interactor?.createDebateStatement(
                leftName.text.toString(),
                rightName.text.toString(),
                fileLeftImage!!,
                selectedCategory!!.id,
                debateName.text.toString()
            )

            isRequestInProgress = true

            progressBar.visibility = View.VISIBLE
        }
    }

    private fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {
        val ei = ExifInterface(image_absolute_path)
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(bitmap, true, false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipImage(bitmap, false, true)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flipImage(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()

        var bitmapWidth = bitmap.width
        var bitmapHeight = bitmap.height
        var compression = 100

        if (byteSizeOf(bitmap) == 48000000) {
            bitmapWidth /= 3
            bitmapHeight /= 3
            compression = 75
        } else if (byteSizeOf(bitmap) > 10000000) {
            bitmapWidth /= 2
            bitmapHeight /= 2
            compression = 90
        } else if (byteSizeOf(bitmap) > 3000000) {
            bitmapWidth /= 1.5.toInt()
            bitmapHeight /= 1.5.toInt()
            compression = 95
        }

        val b = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false)

        b.compress(Bitmap.CompressFormat.JPEG, compression, stream)

        return stream.toByteArray()
    }

    private fun byteSizeOf(bitmap: Bitmap): Int {
        return bitmap.allocationByteCount
    }

}