package com.bangkit.submissionintermediet.view.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit.submissionintermediet.R
import com.bangkit.submissionintermediet.dataStore
import com.bangkit.submissionintermediet.preference.UserPreference
import com.bangkit.submissionintermediet.view.home.HomeActivity
import com.bangkit.submissionintermediet.view.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        userPreference = UserPreference.getInstance(dataStore)
        lifecycleScope.launch {
            delay(3000)

            userPreference.getToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
        }
    }
}
