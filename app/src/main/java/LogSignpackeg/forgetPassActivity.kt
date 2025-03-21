package LogSignpackeg

import ApiClasses.RetrofitClient
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practiceapp.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class forgetPassActivity : AppCompatActivity() {

    private lateinit var etEmailforpass: EditText
    private lateinit var btnforgotPass: Button
    private lateinit var btnBackLogin: Button
    private lateinit var btnBackReg: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_pass)

        etEmailforpass = findViewById(R.id.etEmailforpass)
        btnforgotPass = findViewById(R.id.btnforgotPass)
        btnBackLogin = findViewById(R.id.btnBackLogin)
        btnBackReg = findViewById(R.id.btnBackReg)


        btnforgotPass.setOnClickListener {
            val email = etEmailforpass.text.toString().trim()

            if (email.isNotEmpty()) {
                sendForgotPasswordRequest(email)
                //   startActivity(Intent(requireContext(),LoginActivity::class.java))

            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        btnBackLogin.setOnClickListener {
            startActivity(Intent(this@forgetPassActivity, LoginActivity::class.java))
        }

        btnBackReg.setOnClickListener {
            startActivity(Intent(this@forgetPassActivity, RegisterActivity::class.java))
        }


    }


    @SuppressLint("CheckResult")
    private fun sendForgotPasswordRequest(email: String) {
        val emailRequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())  // ✅ Correct way

        RetrofitClient.instance.forgotPassword(emailRequestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                startActivity(Intent(this@forgetPassActivity, LoginActivity::class.java))
                Toast.makeText(this, " ${response.message}", Toast.LENGTH_SHORT).show()  // ✅ requireContext() use kiya
            },
                { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })
    }
}