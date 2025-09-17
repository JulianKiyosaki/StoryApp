package com.julian.storyapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.julian.storyapp.databinding.ActivityAddStoryBinding
import com.julian.storyapp.service.AddStoryViewModel
import com.julian.storyapp.service.StoryViewModelFactory
import com.julian.storyapp.utils.getImageUri
import com.julian.storyapp.utils.reduceFileImage
import com.julian.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<AddStoryViewModel> {
        StoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectImage.setOnClickListener {
            galleryLaunch.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnTakeImage.setOnClickListener {
            currentImageUri = getImageUri(this)
            cameraLaunch.launch(currentImageUri!!)
        }

        binding.buttonAdd.setOnClickListener {
            uploadStory()
        }
    }

    private fun uploadStory() {
        if (!binding.edAddDescription.text.isNullOrBlank() && currentImageUri != null) {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                val desc = binding.edAddDescription.text.toString()

                showLoading(true)

                val requestBody = desc.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

                viewModel.uploadStory(multipartBody, requestBody).observe(this) { response ->
                    when (response) {
                        is Result.Error -> {
                            showToast(getString(R.string.upload_failed))
                            showLoading(false)
                        }
                        Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showToast(getString(R.string.upload_success))
                            showLoading(false)
                            Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(this)
                            }
                        }
                    }
                }
            }
        } else {
            showToast(getString(R.string.upload_not_valid))
        }
    }

    private val galleryLaunch =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                currentImageUri = uri
                imageShow()
            } else {
                Log.d("Photo Picker", "No media selected")
            }
        }

    private val cameraLaunch =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                imageShow()
            } else {
                currentImageUri = null
            }
        }

    private fun imageShow() {
        currentImageUri?.let { uri ->
            binding.ivStoryImage.setImageURI(uri)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
