package com.example.mtaafe.views.activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionFormViewModel
import com.example.mtaafe.views.adapters.ImageFormAdapter
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class QuestionFormActivity : AppCompatActivity(), ImageClickListener {
    private lateinit var viewModel: QuestionFormViewModel
    private lateinit var imageAdapter: ImageFormAdapter
    private lateinit var rootLayout: View
    private lateinit var titleErrorMessageText: TextView
    private lateinit var bodyErrorMessageText: TextView
    private lateinit var imagesRecycler: RecyclerView
    private lateinit var editTextQuestionTitle: EditText
    private lateinit var editTextQuestionBody: EditText
    private var selectedImages = ArrayList<Uri?>()
    var images = ArrayList<MultipartBody.Part>()
    private var tagList = ArrayList<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_form)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Nová otázka"

        rootLayout = findViewById(R.id.questionDetailRoot)
        editTextQuestionTitle = findViewById(R.id.editTextQuestionTitle)
        editTextQuestionBody = findViewById(R.id.editTextQuestionBody)
        titleErrorMessageText = findViewById(R.id.titleErrorMessageText)
        bodyErrorMessageText = findViewById(R.id.bodyErrorMessageText)
        imagesRecycler = findViewById(R.id.imagesRecyclerView)

        imageAdapter = ImageFormAdapter(ArrayList())
        imagesRecycler.layoutManager = LinearLayoutManager(this)
        imagesRecycler.adapter = imageAdapter

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionFormViewModel::class.java)


        val selectImageBtn : Button = findViewById(R.id.selectImageBtn)
        val askButton : Button = findViewById(R.id.askButton)

        selectImageBtn.setOnClickListener {
            imageSelection()
        }

        askButton.setOnClickListener {
            ask()
        }

        viewModel.titleErrorMessage.observe(this, {
            titleErrorMessageText.visibility = View.VISIBLE
            titleErrorMessageText.text = it
        })

        viewModel.bodyErrorMessage.observe(this, {
            bodyErrorMessageText.visibility = View.VISIBLE
            bodyErrorMessageText.text = it
        })

        viewModel.validationError.observe(this, {
            if(it == true) {
                Snackbar.make(rootLayout, "Nevyplnili ste správne všetky polia", Snackbar.LENGTH_LONG)
                    .show()
            }
        })

        viewModel.result.observe(this, {
            when (it) {
                is ApiResult.Success -> {
                    Log.d("Success", "Question was posted.")

                    setResult(1)
                    finish()
                }
                is ApiResult.Error -> handleError(it.error)
            }
        })
    }

    private fun ask() {
        val title = editTextQuestionTitle.text.toString()
        val body = editTextQuestionBody.text.toString()

        //if (selectedImages.isNotEmpty()) {
            val images = getImages(selectedImages)
        //}

        titleErrorMessageText.visibility = View.GONE
        bodyErrorMessageText.visibility = View.GONE

        viewModel.postQuestion(
            title,
            body,
            tagList,
            images
        )
    }

    private fun imageSelection(){
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")

            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // user selected an image
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode){
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImages.add(data?.data)
                    //imageAdapter.updateData(selectedImages)
                    imageAdapter.addItem(data?.data)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }


    private fun getImages(imageUris : ArrayList<Uri?>) : ArrayList<MultipartBody.Part>{
        val images = ArrayList<MultipartBody.Part>()

        imageUris.forEachIndexed{ index, element -> images.add(prepareFilePart("images[$index]", element))}

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
                Snackbar.make(rootLayout, "Nepodarilo sa pridať otázku", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Skúsiť znovu") {
                            ask()
                        }
                        .show()
            }
        }
    }

    override fun removeImage(position: Int) {
        selectedImages.removeAt(position)
        imageAdapter.removeItem(position)
    }
}