package com.example.mtaafe.views.activities

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
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.mtaafe.R
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.AnswerFormViewModel
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AnswerFormActivity : AppCompatActivity() {
    private lateinit var viewModel: AnswerFormViewModel
    private lateinit var rootLayout: View
    private lateinit var answerFormImageView: ImageView
    private var selectedImages = mutableListOf<Uri?>()
    private var imageIndex = 0
    var images = mutableListOf<MultipartBody.Part>()

    private var questionId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_form)

        questionId = intent.getLongExtra("question_id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        rootLayout = findViewById(R.id.answerFormRoot)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(AnswerFormViewModel::class.java)
        answerFormImageView = findViewById(R.id.answerFormImageView)

        val editTextAnswerBody: EditText = findViewById(R.id.editTextAnswerBody)

        val selectImageBtn : Button = findViewById(R.id.selectImageBtn2)
        val answerBtn : Button = findViewById(R.id.answerBtn)

        selectImageBtn.setOnClickListener {
            imageSelection()
        }

        answerBtn.setOnClickListener {
            val body = editTextAnswerBody.text.toString()

            if (selectedImages.isNotEmpty()) {
                images = getImages(selectedImages)
            }

            Log.d("message", "Body : " + body)
            images.forEachIndexed{index, element -> (Log.d("message", "Image no."+ index + " : "+ element))}

            if (body != ""){
                viewModel.postAnswer(
                        questionId,
                        createPartFromString(body),
                        images
                )

                viewModel.result.observe(this, Observer {
                    when(it) {
                        is ApiResult.Success -> {
                            Log.d("Success", "Answer was posted.")

//                            val intent = Intent(this, QuestionDetailActivity::class.java)
//                            intent.putExtra("question_id", questionId)
//                            startActivity(intent)
                            finish()
                        }
                        is ApiResult.Error -> handleError(it.error)
                        else -> {}
                    }
                })
            }
            else {
                Snackbar.make(rootLayout, "Treba vyplniť všetky povinné údaje!", Snackbar.LENGTH_LONG)
                        .show()
            }

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

                    if(imageIndex == 1) {
                        answerFormImageView.setImageURI(selectedImages[0])
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

        imageUris.forEachIndexed{index, element -> images.add(prepareFilePart("" + index, element))}

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