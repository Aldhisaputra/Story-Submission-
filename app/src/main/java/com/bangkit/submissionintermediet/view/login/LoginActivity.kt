package com.bangkit.submissionintermediet.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bangkit.submissionintermediet.ViewModelFactory
import com.bangkit.submissionintermediet.databinding.ActivityLoginBinding
import com.bangkit.submissionintermediet.view.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupLoginButton()
        setupTextChangedListeners()
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.login(email, password,
                    onSuccess = {
                        binding.progressBar.visibility = View.GONE
                        navigateToHome()
                    },
                    onError = { message ->
                        binding.progressBar.visibility = View.GONE
                        showToast(message)
                    }
                )
            } else {
                showToast("Email atau password tidak boleh kosong")
            }
        }
    }

    private fun setupTextChangedListeners() {
        binding.apply {
            emailEditText.addTextChangedListener { validateFields() }
            passwordEditText.addTextChangedListener { validateFields() }
        }
    }

    private fun validateFields() {
        binding.apply {
            loginButton.isEnabled = !emailEditText.text.isNullOrEmpty() && !passwordEditText.text.isNullOrEmpty()
        }
    }

    private fun playAnimation() {
        // Animasi untuk elemen UI
        val imageViewAnimator = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        imageViewAnimator.start()

        AnimatorSet().apply {
            playSequentially(
                binding.emailTextView.createFadeInAnimator(),
                binding.emailEditText.createFadeInAnimator(),
                binding.passwordTextView.createFadeInAnimator(),
                binding.passwordEditText.createFadeInAnimator(),
                binding.loginButton.createFadeInAnimator()
            )
            startDelay = 200
        }.start()
    }

    private fun View.createFadeInAnimator(duration: Long = 250): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, View.ALPHA, 1f).setDuration(duration)
    }

    private fun navigateToHome() {
        Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
