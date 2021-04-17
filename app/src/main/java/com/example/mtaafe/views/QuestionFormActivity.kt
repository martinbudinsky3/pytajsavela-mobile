package com.example.mtaafe.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.databinding.ActivityLoginBinding
import com.example.mtaafe.network.ApiClient
import com.example.mtaafe.network.ApiInterface
import androidx.lifecycle.Observer
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
    private lateinit var adapter: QuestionAdapter

    //private var selectedImage : Uri? = null
    private var selectedImages = mutableListOf<Uri?>()
    private var imageIndex = 0
    var images = mutableListOf<MultipartBody.Part>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_form)
        rootLayout = findViewById(R.id.questionFormRoot)

        val editTextQuestionTitle: EditText = findViewById(R.id.editTextQuestionTitle)
        val editTextQuestionBody: EditText = findViewById(R.id.editTextQuestionBody)
        val editTextQuestionTags: EditText = findViewById(R.id.editTextQuestionTags)

        val selectImageBtn : Button = findViewById(R.id.selectImageBtn)
        val askButton : Button = findViewById(R.id.askButton)

        selectImageBtn.setOnClickListener {
            imageSelection()
        }

        askButton.setOnClickListener {
            val title = editTextQuestionTitle.text.toString()
            val body = editTextQuestionBody.text.toString()

            val tags = getTags(editTextQuestionTags.text.toString())

            if (selectedImages[0] != null) {
                images = getImages(selectedImages)
            }

            Log.d("message", "Title : " + title)
            Log.d("message", "Body : " + body)

            tags!!.forEachIndexed{index, element -> (Log.d("message", "Tag no."+ index + " : "+ element))}
            images.forEachIndexed{index, element -> (Log.d("message", "Image no."+ index + " : "+ element))}
            // Log.d("message", "Image name : "+ images.get(0))

            viewModel.postQuestion(
                    createPartFromString(title),
                    createPartFromString(body),
                    tags,
                    images
            )

            //viewModel.postQuestion(question)

            viewModel.result.observe(this, Observer {
                when(it) {
                    is ApiResult.Success -> {
                        Log.d("Success", "Question was posted.")
                    }
                    is ApiResult.Error -> handleError(it.error)
                    else -> {}
                }
            })
        }
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
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PCIKER = 100
    }

    private fun getTags(tagsInput : String): List<RequestBody>? {
        val tagsString = tagsInput.split(",").map { it.trim() }
        val tagsRequestBody = mutableListOf<RequestBody>()

        tagsString.forEachIndexed{index, element -> tagsRequestBody.add(index, createPartFromString(element)) }

        return tagsRequestBody
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getImages(imageUris : MutableList<Uri?>) : MutableList<MultipartBody.Part>{
        val images = mutableListOf<MultipartBody.Part>()

        imageUris.forEachIndexed{index, element -> images?.add(prepareFilePart("" + index, element))}

        return images
    }

    private fun createPartFromString(partString : String) : RequestBody{
        return RequestBody.create(MultipartBody.FORM, partString)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun prepareFilePart(partName : String, fileUri : Uri?) : MultipartBody.Part {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(fileUri!!, "r", null)

        val file = File(cacheDir, contentResolver.getFileName(fileUri))
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val requestFile : RequestBody = RequestBody.create(MediaType.parse(contentResolver.getFileName(fileUri)), file)

        return MultipartBody.Part.createFormData(partName, contentResolver.getFileName(fileUri), requestFile)
    }

    private fun ContentResolver.getFileName(uri : Uri) : String{
        var name = ""
        var cursor = query(uri, null, null, null, null)

        cursor?.use {
            it.moveToFirst()
            name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return name
    }

    @RequiresApi(Build.VERSION_CODES.O)
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