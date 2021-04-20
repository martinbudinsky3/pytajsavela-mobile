package com.example.mtaafe.views

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionFormViewModel
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class QuestionFormActivity : AppCompatActivity() {
    private lateinit var viewModel: QuestionFormViewModel
    private lateinit var rootLayout: View
    private lateinit var titleErrorMessageText: TextView
    private lateinit var bodyErrorMessageText: TextView
    private lateinit var questionFormImageView: ImageView
    private var selectedImages = mutableListOf<Uri?>()
    private var imageIndex = 0
    var images = mutableListOf<MultipartBody.Part>()
    private var tagList = mutableListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_form)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        rootLayout = findViewById(R.id.questionFormRoot)
        titleErrorMessageText = findViewById(R.id.titleErrorMessageText)
        bodyErrorMessageText = findViewById(R.id.bodyErrorMessageText)
        questionFormImageView = findViewById(R.id.questionFormImageView)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionFormViewModel::class.java)

        val editTextQuestionTitle: EditText = findViewById(R.id.editTextQuestionTitle)
        val editTextQuestionBody: EditText = findViewById(R.id.editTextQuestionBody)

        val selectImageBtn : Button = findViewById(R.id.selectImageBtn)
        val askButton : Button = findViewById(R.id.askButton)

        selectImageBtn.setOnClickListener {
            imageSelection()
        }

        askButton.setOnClickListener {
            val title = editTextQuestionTitle.text.toString()
            val body = editTextQuestionBody.text.toString()

            if (selectedImages.isNotEmpty()) {
                images = getImages(selectedImages)
            }

            images.forEachIndexed{ index, element -> (Log.d(
                "message",
                "Image no." + index + " : " + element
            ))}

            titleErrorMessageText.visibility = View.GONE
            bodyErrorMessageText.visibility = View.GONE

            viewModel.postQuestion(
                title,
                body,
                tagList,
                images
            )
        }

        viewModel.titleErrorMessage.observe(this, {
            titleErrorMessageText.visibility = View.VISIBLE
            titleErrorMessageText.text = it
        })

        viewModel.bodyErrorMessage.observe(this, {
            bodyErrorMessageText.visibility = View.VISIBLE
            bodyErrorMessageText.text = it
        })

        viewModel.result.observe(this, {
            when (it) {
                is ApiResult.Success -> {
                    Log.d("Success", "Question was posted.")

                    val intent = Intent(this, QuestionsListActivity::class.java)
                    startActivity(intent)
                }
                is ApiResult.Error -> handleError(it.error)
            }
        })
    }

    private fun imageSelection(){
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")

            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PCIKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // user selected an image
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode){
                REQUEST_CODE_IMAGE_PCIKER -> {
                    //selectedImage = data?.data
                    selectedImages.add(imageIndex, data?.data)
                    imageIndex += 1

                    if(imageIndex == 1) {
                        questionFormImageView.setImageURI(selectedImages[0])
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PCIKER = 100
    }


    private fun getImages(imageUris : MutableList<Uri?>) : MutableList<MultipartBody.Part>{
        val images = mutableListOf<MultipartBody.Part>()

        imageUris.forEachIndexed{ index, element -> images.add(prepareFilePart("" + index, element))}

        return images
    }


    private fun prepareFilePart(partName : String, fileUri : Uri?) : MultipartBody.Part {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(fileUri!!, "r", null)

        val file = File(cacheDir, contentResolver.getFileName(fileUri))
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val requestFile : RequestBody = RequestBody.create(
            MediaType.parse(
                contentResolver.getFileName(
                    fileUri
                )
            ), file
        )

        return MultipartBody.Part.createFormData(
            partName,
            contentResolver.getFileName(fileUri),
            requestFile
        )
    }

    private fun ContentResolver.getFileName(uri: Uri) : String{
        var name = ""
        var cursor = query(uri, null, null, null, null)

        cursor?.use {
            it.moveToFirst()
            name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return name
    }

    private fun handleError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(rootLayout, "Oops, something went wrong.", Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }
}