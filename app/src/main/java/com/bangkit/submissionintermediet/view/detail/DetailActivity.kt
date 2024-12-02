package com.bangkit.submissionintermediet.view.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.submissionintermediet.ViewModelFactory
import com.bangkit.submissionintermediet.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID) ?: run {
            showError()
            return
        }

        val storyName = intent.getStringExtra(EXTRA_STORY_NAME)
        val storyDescription = intent.getStringExtra(EXTRA_STORY_DESCRIPTION)
        val storyImageUrl = intent.getStringExtra(EXTRA_STORY_IMAGE_URL)

        observeViewModel(storyId, storyName, storyDescription, storyImageUrl)
    }

    private fun observeViewModel(storyId: String, storyName: String?, storyDescription: String?, storyImageUrl: String?) {
        viewModel.getDetailStory(storyId).observe(this) { storyDetail ->
            if (storyDetail != null) {
                binding.tvName.text = storyName
                binding.tvDescription.text = storyDescription
                Glide.with(this)
                    .load(storyImageUrl)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.holo_red_dark)
                    .into(binding.ivDetailPhoto)
                binding.ivDetailPhoto.transitionName = "sharedImage"
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "Terjadi kesalahan, silakan coba lagi", Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_STORY_NAME = "extra_story_name"
        const val EXTRA_STORY_DESCRIPTION = "extra_story_description"
        const val EXTRA_STORY_IMAGE_URL = "extra_story_image_url"
    }
}
