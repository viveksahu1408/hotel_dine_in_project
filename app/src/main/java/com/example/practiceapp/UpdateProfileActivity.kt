package com.example.practiceapp

import ApiClasses.RetrofitClient
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.practiceapp.databinding.ActivityUpdateProfileBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.regex.Pattern

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding // ViewBinding reference
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var sessionManager: SessionManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)


        val firstName = intent.getStringExtra("first_name") ?: ""
        val lastName = intent.getStringExtra("last_name") ?: ""
        val middleName = intent.getStringExtra("middle_name") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phoneNumber = intent.getStringExtra("phone_number") ?: ""
        //val profileImage = intent.getStringExtra("profile_image") // optional


        binding.etFirstName.setText(firstName)
        binding.etLastName.setText(lastName)
        binding.etMiddleName.setText(middleName)
        binding.etEmail.setText(email)
        binding.etPhoneNumber.setText(phoneNumber)

        binding.ivProfileImage.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnUpdate.setOnClickListener {
            if (validInputs()) {
                updateUserProfile()
                startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.ivProfileImage.setImageURI(imageUri)
        }
    }



    private fun validInputs():Boolean {

        val firstNameValue = binding.etFirstName.text.toString().trim()
        val lastNameValue = binding.etLastName.text.toString().trim()
        val emailValue = binding.etEmail.text.toString().trim()
        val phoneValue = binding.etPhoneNumber.text.toString().trim()


        if (firstNameValue.isEmpty()){
            binding.etFirstName.error = "First name is required"
            binding.etFirstName.requestFocus()
            return false
        }
        if (lastNameValue.isEmpty()){
            binding.etLastName.error = "Last name is required"
            binding.etLastName.requestFocus()
            return false
        }

        if (!isValidEmail(emailValue)) {
            binding.etEmail.error = "Enter a valid email"
            binding.etEmail.requestFocus()
            return false
        }
        if (!isValidPhone(phoneValue)) {
            binding.etPhoneNumber.error = "Enter a valid 10-digit phone number"
            binding.etPhoneNumber.requestFocus()
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
    private fun updateUserProfile() {
        val id = sessionManager.getUserId() ?: ""

        val hotelDineIn = "update".toRequestBody("text/plain".toMediaTypeOrNull())
        val firstNameRequest = binding.etFirstName.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
        val lastNameRequest = binding.etLastName.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
        val middleNameRequest = binding.etMiddleName.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
        val emailRequest = binding.etEmail.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneNumberRequest = binding.etPhoneNumber.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
        val userIdRequest = id.toRequestBody("text/plain".toMediaTypeOrNull())

        val selectedGender = if (binding.rbMale.isChecked) "Male" else "Female"
        val genderRequest = selectedGender.toRequestBody("text/plain".toMediaTypeOrNull())

        // Log for debugging
        Log.d("API_DEBUG", "hotel_dine_in: hotel_dine_in")
        Log.d("API_DEBUG", "first_name: $firstNameRequest")
        Log.d("API_DEBUG", "gender: $selectedGender")

        var profileImagePart: MultipartBody.Part? = null

        // If imageUri is not null, convert the image file to a MultipartBody.Part for upload
        if (imageUri != null) {
            val file = File(getRealPathFromURI(imageUri!!)) // Convert URI to file path
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            profileImagePart = MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
        }

        RetrofitClient.instance.updateUser(
            hotelDineIn,
            firstNameRequest,
            lastNameRequest,
            middleNameRequest,
            emailRequest,
            genderRequest,
            phoneNumberRequest,
            userIdRequest,
            profileImagePart
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            }, { error ->
                Log.e("API_ERROR", error.message ?: "Unknown Error")
                Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
            })
    }

    // Helper function to get real file path from URI
    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex("_data")
        val filePath = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return filePath ?: ""
    }
}
