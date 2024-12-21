package com.bangkit.submissionintermediet.view.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.submissionintermediet.ViewModelFactory
import com.bangkit.submissionintermediet.adapter.StoryAdapter
import com.bangkit.submissionintermediet.dataStore
import com.bangkit.submissionintermediet.databinding.ActivityHomeBinding
import com.bangkit.submissionintermediet.view.map.MapsActivity
import com.bangkit.submissionintermediet.preference.UserPreference
import com.bangkit.submissionintermediet.view.addstory.AddStoryActivity
import com.bangkit.submissionintermediet.view.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.paging.PagingData
import com.bangkit.submissionintermediet.pagging.StoryEntity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel> { ViewModelFactory.getInstance(this) }
    private val storyAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeStory()

        binding.btnMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        binding.add.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

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

    private fun observeStory() {
        viewModel.getAllStory.observe(this) { pagingData: PagingData<StoryEntity> ->
            lifecycleScope.launch {
                storyAdapter.submitData(pagingData)
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
}
