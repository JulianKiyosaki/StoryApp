package com.julian.storyapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.julian.storyapp.databinding.ActivityDetailBinding
import com.julian.storyapp.service.StoryDetail
import com.julian.storyapp.service.StoryViewModel
import com.julian.storyapp.service.StoryViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID")

        viewModel = ViewModelProvider(this, StoryViewModelFactory(applicationContext)).get(StoryViewModel::class.java)

        if (storyId != null) {
            viewModel.fetchStoryDetail(storyId)
        } else {
            Toast.makeText(this, "Story ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.storyDetail.observe(this) { story ->
            story?.let { displayStoryDetails(it) }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayStoryDetails(story: StoryDetail) {
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivDetailPhoto)
    }
}
