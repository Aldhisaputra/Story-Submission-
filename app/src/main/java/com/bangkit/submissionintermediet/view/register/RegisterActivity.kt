package com.bangkit.submissionintermediet.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.ViewModelFactory
import com.bangkit.submissionintermediet.databinding.ActivityRegisterBinding
import com.bangkit.submissionintermediet.view.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            registerButton.isEnabled = false

            // Observasi perubahan pada CustomView
            nameEditText.addTextChangedListener { validateFields() }
            emailEditText.addTextChangedListener { validateFields() }
            passwordEditText.addTextChangedListener { validateFields() }

            registerButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                registerViewModel.register(name, email, password)
                observeRegisterResult()
            }

            loginButton.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateFields() {
        binding.apply {
            val isNameValid = nameEditText.error == null && nameEditText.text.toString().isNotEmpty()
            val isEmailValid = emailEditText.error == null && emailEditText.text.toString().isNotEmpty()
            val isPasswordValid = passwordEditText.error == null && passwordEditText.text.toString().isNotEmpty()

            registerButton.isEnabled = isNameValid && isEmailValid && isPasswordValid
        }
    }

    private fun observeRegisterResult() {
        lifecycleScope.launch {
            registerViewModel.registerResult.collect { result ->
                when (result) {
                    is Results.Loading -> showLoading(true)
                    is Results.Success -> {
                        showLoading(false)
                        showToast("Registrasi berhasil: ${result.data.message}")
                    }
                    is Results.Error -> {
                        showLoading(false)
                        showToast("Terjadi kesalahan: ${result.error}")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            registerButton.isEnabled = !isLoading
        }
    }

    private fun playAnimation() {
        val imageViewAnimator = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        imageViewAnimator.start()

        val animations = listOf(
            ObjectAnimator.ofFloat(binding.nameView, View.ALPHA, 1f).setDuration(200),
            ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(125),
            ObjectAnimator.ofFloat(binding.emailView, View.ALPHA, 1f).setDuration(125),
            ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(125),
            ObjectAnimator.ofFloat(binding.passwordView, View.ALPHA, 1f).setDuration(125),
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(125),
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(125)
        )

        AnimatorSet().apply {
            playSequentially(animations)
            startDelay = 200
            start()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
