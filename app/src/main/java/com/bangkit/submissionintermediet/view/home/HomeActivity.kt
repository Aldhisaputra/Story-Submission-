package com.bangkit.submissionintermediet.view.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.submissionintermediet.R
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.ViewModelFactory
import com.bangkit.submissionintermediet.adapter.StoryAdapter
import com.bangkit.submissionintermediet.dataStore
import com.bangkit.submissionintermediet.databinding.ActivityHomeBinding
import com.bangkit.submissionintermediet.preference.UserPreference
import com.bangkit.submissionintermediet.view.addstrory.AddStoryActivity
import com.bangkit.submissionintermediet.view.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel> { ViewModelFactory.getInstance(this) }
    private val storyAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeStories()

        binding.add.setOnClickListener { startActivity(Intent(this, AddStoryActivity::class.java)) }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun setupRecyclerView() {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyAdapter
        }
    }

    private fun observeStories() {
        viewModel.getAllStories.observe(this) { result ->
            when (result) {
                is Results.Success -> storyAdapter.submitList(result.data)
                is Results.Error -> showSnackbar("Error: ${result.error}")
                is Results.Loading -> showSnackbar("Loading...")
            }
        }
    }

    private fun logoutUser() {
        lifecycleScope.launch {
            UserPreference.getInstance(dataStore).clearToken()
            showSnackbar("Logout berhasil")
            Intent(this@HomeActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(this)
            }
            finish()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        observeStories()
    }
}