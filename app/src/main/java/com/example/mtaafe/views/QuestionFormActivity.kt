package com.example.mtaafe.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class QuestionFormActivity : AppCompatActivity() {
    private lateinit var viewModel: QuestionFormViewModel
    private lateinit var rootLayout: View
    private lateinit var adapter: QuestionAdapter

    val editTextQuestionTitle: EditText = findViewById(R.id.editTextQuestionTitle)
    val editTextQuestionBody: EditText = findViewById(R.id.editTextQuestionBody)
    val editTextQuestionTags: EditText = findViewById(R.id.editTextQuestionTags)

    val selectImageBtn : Button = findViewById(R.id.selectImageBtn)
    val askButton : Button = findViewById(R.id.askButton)

    //private var selectedImage : Uri? = null
    private lateinit var selectedImages : MutableList<Uri?>
    private var imageIndex = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_form)
        rootLayout = findViewById(R.id.questionFormRoot)

        val title = editTextQuestionTitle.text.toString()
        val body = editTextQuestionBody.text.toString()

        selectImageBtn.setOnClickListener {
            imageSelection()
        }

        var tags = getTags(editTextQuestionTags.text.toString())

        askButton.setOnClickListener {
            if (selectedImages[0] != null) {
                val images = getImages(selectedImages)
            }

            val question = Question(1, title, body, tags, listOf())

            viewModel.postQuestion(question)

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

    private fun getTags(tagsInput : String): List<String> {
        return tagsInput.split(",").map { it.trim() }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getImages(imageUris : MutableList<Uri?>){
        val parcelFileDescriptor = contentResolver.openFileDescriptor(imageUris[0]!!, "r", null) ?: return

        val file = File(cacheDir, contentResolver.getFileName(imageUris[0]!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
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