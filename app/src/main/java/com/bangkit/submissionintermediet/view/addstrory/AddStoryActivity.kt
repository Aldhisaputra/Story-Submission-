package com.bangkit.submissionintermediet.view.addstrory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.ViewModelFactory
import com.bangkit.submissionintermediet.databinding.ActivityAddStoryBinding
import com.bangkit.submissionintermediet.response.StoryUploadResponse
import com.bangkit.submissionintermediet.utils.reduceFileImage
import com.bangkit.submissionintermediet.utils.uriToFile
import com.bangkit.submissionintermediet.view.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<AddStoryViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.imageUri.observe(this) { uri ->
            uri?.let {
                currentImageUri = it
                binding.ImageView.setImageURI(it)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.uploadResult.observe(this) { result ->
            handleUploadResult(result)
        }

        binding.cameraBtn.setOnClickListener {
            if (allPermissionsGranted()) launcherCamera.launch(null)
            else requestPermissions()
        }

        binding.galleryBtn.setOnClickListener {
            val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            launcherGallery.launch(request)
        }

        binding.uploadBtn.setOnClickListener { uploadImage() }
    }

    @Suppress("DEPRECATION")
    private val launcherCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, it, null, null))
            viewModel.imageUri.value = uri
        }
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.imageUri.value = it }
    }

    private fun uploadImage() {
        val description = binding.descriptionEdt.text.toString()
        if (description.isBlank()) {
            Snackbar.make(binding.root, "Deskripsi tidak boleh kosong", Snackbar.LENGTH_SHORT).show()
            return
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)
            val compressedImage = imageFile.reduceFileImage()
            val photoMultipart = MultipartBody.Part.createFormData("photo", compressedImage.name, compressedImage.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            val descriptionReqBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            viewModel.uploadStory(descriptionReqBody, photoMultipart, null, null)
        } ?: Snackbar.make(binding.root, "Gambar belum dipilih", Snackbar.LENGTH_SHORT).show()
    }

    private fun handleUploadResult(result: Results<StoryUploadResponse>) {
        when (result) {
            is Results.Loading -> binding.progressIndicator.visibility = View.VISIBLE
            is Results.Success -> {
                Snackbar.make(binding.root, result.data.message ?: "Upload Successful", Snackbar.LENGTH_SHORT).show()
                navigateToHome()
            }
            is Results.Error -> {
                Snackbar.make(binding.root, result.error, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
}
