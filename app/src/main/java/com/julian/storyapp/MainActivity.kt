package com.julian.storyapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.julian.storyapp.databinding.ActivityMainBinding
import com.julian.storyapp.login.LoginActivity
import com.julian.storyapp.maps.MapsActivity
import com.julian.storyapp.service.LoadingStateAdapter
import com.julian.storyapp.service.StoryAdapter
import com.julian.storyapp.service.StoryViewModel
import com.julian.storyapp.service.StoryViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        lifecycleScope.launch {
            UserPreference.getInstance(applicationContext.dataStore).getUserToken().collect { token ->
                if (token.isNullOrEmpty()) {
                    navigateToLogin()
                } else {
                    setupUI()
                }
            }
        }
    }

    private fun setupUI() {
        val storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("STORY_ID", story.id)
            }
            startActivity(intent)
        }

        val loadingStateAdapter = LoadingStateAdapter { storyAdapter.retry() }

        binding.rvMain.apply {
            adapter = storyAdapter.withLoadStateFooter(
                footer = loadingStateAdapter)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        storyAdapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility = if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                binding.textError.visibility = View.VISIBLE
                binding.textError.text = it.error.localizedMessage
            } ?: run {
                binding.textError.visibility = View.GONE
            }
        }

        storyViewModel.stories.observe(this) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
        }

        storyViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        storyViewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                binding.textError.visibility = View.VISIBLE
                binding.textError.text = error
            } else {
                binding.textError.visibility = View.GONE
            }
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            storyAdapter.refresh()
            storyAdapter.addLoadStateListener { loadState ->
                if (loadState.source.refresh is LoadState.NotLoading) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                lifecycleScope.launch {
                    UserPreference.getInstance(applicationContext.dataStore).clearSession()
                    navigateToLogin()
                }
                return true
            }
            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

