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

        binding.ivProfileImage.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnUpdate.setOnClickListener {
            if (validateInputs()) {
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

    private fun validateInputs(): Boolean {
        if (binding.etFirstName.text.isNullOrEmpty() ||
            binding.etLastName.text.isNullOrEmpty() ||
            binding.etEmail.text.isNullOrEmpty() ||
            binding.etPhoneNumber.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

//
//    @SuppressLint("CheckResult", "SetTextI18n")
//    private fun fetchUserDetails(userId: String) {
//        RetrofitClient.instance.getUserDetails(userId)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ response ->
//                if (response.status == 200) {
//                    val user = response.user
//                  //  binding.tvFullName.text = "Name : ${user?.first_name} ${user?.middle_name} ${user?.last_name}"
//                    binding.etFirstName.setText(user?.first_name) = "first Name : ${user.first_name}"
//                    binding.etFirstName.text = "Email : ${user.first_name}"
//                    binding.etLastName.text = "Email : ${user?.email}"
//                    binding.etMiddleName.text = "Email : ${user?.email}"
//                    binding.etEmail.text = "Email : ${user?.email}"
//                    binding.etPhoneNumber.text = "Mobile : ${user?.phone_number}"
//                   // binding.tvGender.text = "Gender : ${user?.gender}"
//
//                    Glide.with(this).load(user?.profile_image).into(binding.ivProfileImage)
//                } else {
//                    Toast.makeText(this, "Failed to fetch user details", Toast.LENGTH_SHORT).show()
//                }
//            }, { error ->
//                Log.e("API_ERROR", "Error: ${error.message}")
//                Toast.makeText(this, "Error fetching user details", Toast.LENGTH_SHORT).show()
//            })
//    }

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
