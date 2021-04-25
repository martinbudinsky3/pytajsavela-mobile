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
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.config.Constants
import com.example.mtaafe.data.models.*
import com.example.mtaafe.viewmodels.QuestionFormViewModel
import com.example.mtaafe.views.adapters.DeletableTagAdapter
import com.example.mtaafe.views.adapters.ImageFormAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class QuestionFormActivity : AppCompatActivity(), ImageClickListener, TagDeleteClickListener {
    private lateinit var viewModel: QuestionFormViewModel
    private lateinit var imageAdapter: ImageFormAdapter
    private lateinit var selectedTagsAdapter: DeletableTagAdapter
    private lateinit var tagsAdapter: ArrayAdapter<Tag>
    private lateinit var rootLayout: View
    private lateinit var titleErrorMessageText: TextView
    private lateinit var bodyErrorMessageText: TextView
    private lateinit var imagesRecycler: RecyclerView
    private lateinit var tagsRecycler: RecyclerView
    private lateinit var editTextQuestionTitle: EditText
    private lateinit var editTextQuestionBody: EditText
    private lateinit var selectImageBtn : Button
    private lateinit var askButton : Button
    private lateinit var tagsAutoCompleteTextView: AutoCompleteTextView
    private var selectedImages = ArrayList<Uri?>()
    private var selectedTags = ArrayList<Long>()

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
        tagsRecycler = findViewById(R.id.tagsRecyclerView)
        selectImageBtn = findViewById(R.id.selectImageBtn)
        askButton = findViewById(R.id.askButton)
        tagsAutoCompleteTextView = findViewById(R.id.tagsAutoCompleteTextView)

        selectImageBtn.setOnClickListener {
            imageSelection()
        }

        askButton.setOnClickListener {
            ask()
        }

        imageAdapter = ImageFormAdapter(ArrayList())
        imagesRecycler.layoutManager = LinearLayoutManager(this)
        imagesRecycler.adapter = imageAdapter

        selectedTagsAdapter = DeletableTagAdapter(ArrayList())
        tagsRecycler.layoutManager = FlexboxLayoutManager(this)
        tagsRecycler.adapter = selectedTagsAdapter

        tagsAdapter = ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line, ArrayList<Tag>())
        tagsAutoCompleteTextView.setAdapter(tagsAdapter)

        initTagSearch()

        tagsAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            addTag(position)
        }

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
                .create(QuestionFormViewModel::class.java)

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

        viewModel.successfulPost.observe(this, {
            if(it == true) {
                Log.d("Success", "Question was posted.")

                setResult(Constants.QUESTION_CREATED)
                finish()
            }
        })

        viewModel.postError.observe(this, {
             handlePostError(it)
        })

        viewModel.tagsList.observe(this, {
            Log.d("Tags results size", it.tags.size.toString())
            tagsAdapter.clear()
            tagsAdapter.addAll(it.tags)
            tagsAdapter.notifyDataSetChanged()
            tagsAdapter.filter.filter(tagsAutoCompleteTextView.text, tagsAutoCompleteTextView)
        })

        viewModel.errorTagsList.observe(this, {
            handleTagsError(it)
        })
    }

    private fun initTagSearch() {
        tagsAutoCompleteTextView.doAfterTextChanged { text ->
            if(text != null && text.trim().length > 1) {
                Log.d("Tag query", text.toString())
                viewModel.searchQuery = text.toString()
                viewModel.getTagsList()
            } else {
                tagsAdapter.clear()
            }
        }
    }

    private fun addTag(position: Int) {
        val tag = tagsAdapter.getItem(position)
        Log.d("Selected tag", tag.toString())
        selectedTagsAdapter.addItem(tag!!)
        selectedTags.add(tag.id)
        clearTagField()
    }

    private fun clearTagField() {
        tagsAutoCompleteTextView.setText("")
        tagsAdapter.clear()
    }

    private fun ask() {
        val title = editTextQuestionTitle.text.toString()
        val body = editTextQuestionBody.text.toString()
        val images = getImages(selectedImages)

        titleErrorMessageText.visibility = View.GONE
        bodyErrorMessageText.visibility = View.GONE

        viewModel.postQuestion(
            title,
            body,
            selectedTags,
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

    private fun handlePostError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

    private fun handleTagsError(error: ErrorEntity) {
        when(error) {
            is ErrorEntity.Unauthorized -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Snackbar.make(rootLayout, "Nepodarilo sa načítať tagy", Snackbar.LENGTH_LONG)
                    .setAction("Skúsiť znovu") {
                        viewModel.getTagsList()
                    }
                    .show()
            }
        }
    }

    override fun removeImage(position: Int) {
        selectedImages.removeAt(position)
        imageAdapter.removeItem(position)
    }

    override fun removeTag(position: Int) {
        selectedTags.removeAt(position)
        selectedTagsAdapter.removeItem(position)
    }
}