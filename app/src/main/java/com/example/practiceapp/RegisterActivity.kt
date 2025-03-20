package com.example.practiceapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ApiClasses.RetrofitClient
import android.content.Intent
import android.widget.RadioButton
import android.widget.RadioGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var firstName: EditText
    private lateinit var middleName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var gender: EditText
    private lateinit var adress: EditText
    private lateinit var password: EditText
    private lateinit var registerBtn: Button
    private lateinit var btngotoRegister: Button

    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sessionManager = SessionManager(this)
        firstName = findViewById(R.id.etFirstName)
        middleName = findViewById(R.id.etMiddleName)
        lastName = findViewById(R.id.etLastName)
        email = findViewById(R.id.etEmail)
        phone = findViewById(R.id.etPhone)
        adress = findViewById(R.id.etadress)
      //  gender = findViewById(R.id.etGender)
        password = findViewById(R.id.etPassword)
        registerBtn = findViewById(R.id.btnRegister)
        btngotoRegister = findViewById(R.id.btngotoRegister)

        genderRadioGroup = findViewById(R.id.rgGenderReg)
        radioMale = findViewById(R.id.rbMaleReg)
        radioFemale = findViewById(R.id.rbFemaleReg)

        registerBtn.setOnClickListener {

            if (validInputs()){
                registerUser()
            }
        }

        btngotoRegister.setOnClickListener {
            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
        }
    }


    private fun validInputs():Boolean {

        val firstNameValue = firstName.text.toString().trim()
        val lastNameValue = lastName.text.toString().trim()
        val emailValue = email.text.toString().trim()
        val phoneValue = phone.text.toString().trim()
        val passwordValue = password.text.toString().trim()
        val selectedGenderValue = if (radioMale.isChecked)"Male" else if (radioFemale.isChecked)"Female" else ""

        if (firstNameValue.isEmpty()){
            firstName.error = "First name is required"
            firstName.requestFocus()
            return false
        }
        if (lastNameValue.isEmpty()){
            lastName.error = "Last name is required"
            lastName.requestFocus()
            return false
        }

        if (!isValidEmail(emailValue)) {
            email.error = "Enter a valid email"
            email.requestFocus()
            return false
        }
        if (!isValidPhone(phoneValue)) {
            phone.error = "Enter a valid 10-digit phone number"
            phone.requestFocus()
            return false
        }
        if (passwordValue.length < 8) {
            password.error = "Password must be at least 8 characters"
            password.requestFocus()
            return false
        }

        if (selectedGenderValue.isEmpty()) {
            Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show()
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

    private fun isValidPhone(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
    }




    @SuppressLint("CheckResult")
    private fun registerUser() {
        val apiService = RetrofitClient.instance

        val hotelDineIn = "register".toRequestBody("text/plain".toMediaTypeOrNull())
        val firstNameValue = firstName.text.toString().trim()
        val middleNameValue = middleName.text.toString().trim()
        val lastNameValue = lastName.text.toString().trim()
        val emailValue = email.text.toString().trim()
        val phoneNumberValue = phone.text.toString().trim()
        val addressValue = adress.text.toString().trim()
      //  val genderValue = gender.text.toString().trim()
        val passwordValue = password.text.toString().trim()

        val selectedGender = when {
            radioMale.isChecked -> "Male"
            radioFemale.isChecked -> "Female"
            else -> ""
        }

        if (firstNameValue.isEmpty() || lastNameValue.isEmpty() || emailValue.isEmpty() ||
            phoneNumberValue.isEmpty()|| selectedGender.isEmpty() || passwordValue.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_LONG).show()
            return
        }


        val firstName = firstNameValue.toRequestBody("text/plain".toMediaTypeOrNull())
        val middleName = middleNameValue.toRequestBody("text/plain".toMediaTypeOrNull())
        val lastName = lastNameValue.toRequestBody("text/plain".toMediaTypeOrNull())
        val email = emailValue.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneNumber = phoneNumberValue.toRequestBody("text/plain".toMediaTypeOrNull())
        val address = addressValue.toRequestBody("text/plain".toMediaTypeOrNull())
        val gender = selectedGender.toRequestBody("text/plain".toMediaTypeOrNull())
        val password = passwordValue.toRequestBody("text/plain".toMediaTypeOrNull())

        apiService.registerUser(hotelDineIn, firstName, middleName, lastName, email, phoneNumber,address, gender, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                val jsonObject = JSONObject(response.string())

                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()


                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    val errorMessage = jsonObject.optString("message", "Registration failed")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }, { error ->
                Log.e("RegisterActivity", "API Error: ${error.message}", error)
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}





