package LogSignpackeg


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practiceapp.databinding.ActivityLoginBinding
import ApiClasses.RetrofitClient
import com.example.practiceapp.MainActivity
import com.example.practiceapp.SessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if (validation()) {
                loginUser()
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, forgetPassActivity::class.java))
        }
    }

    private fun validation(): Boolean {
        val emailVal = binding.etEmail.text.toString().trim()
        val passwordVal = binding.etPassword.text.toString().trim()

        if (!isValidEmail(emailVal)) {
            binding.etEmail.error = "Enter a valid email"
            binding.etEmail.requestFocus()
            return false
        }

        if (passwordVal.length < 8) {
            binding.etPassword.error = "Enter Correct Password"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        )
        return emailPattern.matcher(email).matches()
    }

    @SuppressLint("CheckResult")
    private fun loginUser() {
        val apiService = RetrofitClient.instance

        val emailValue = binding.etEmail.text.toString().trim()
        val passwordValue = binding.etPassword.text.toString().trim()

        if (emailValue.isEmpty() || passwordValue.isEmpty()) {
            Toast.makeText(this, "Email and Password are required!", Toast.LENGTH_LONG).show()
            return
        }

        apiService.loginUser1("login", email = emailValue, password = passwordValue)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.status == "200") {
                    Log.d("LoginActivity", "User ID: ${response.user_id}")

                    sessionManager.saveUserId(response.user_id)

                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
                    sessionManager.setLoginStatus(true)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("LoginActivity", "Response body is null")
                    Toast.makeText(this, "Invalid response from server!", Toast.LENGTH_LONG).show()
                }
            }, { error ->
                Log.e("LoginActivity", "API Error: ${error.message}", error)
                Toast.makeText(this, "Login Failed: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}
