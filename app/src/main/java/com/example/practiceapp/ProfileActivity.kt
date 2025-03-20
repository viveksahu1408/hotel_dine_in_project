package com.example.practiceapp

import ApiClasses.RetrofitClient
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.practiceapp.databinding.ActivityProfileBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.instance
    private val compositeDisposable = CompositeDisposable()
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Set up logout button
        binding.logoutBtn.setOnClickListener {
            sessionManager.logoutUser()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Get user ID and fetch details
        val userId = sessionManager.getUserId().toString()
        fetchUserDetails(userId)
        fetchUserAddress(userId)

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, UpdateProfileActivity::class.java))
        }
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun fetchUserDetails(userId: String) {
        RetrofitClient.instance.getUserDetails(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.status == 200) {
                    val user = response.user
                    binding.tvFullName.text = "Name : ${user?.first_name} ${user?.middle_name} ${user?.last_name}"
                    binding.tvEmail.text = "Email : ${user?.email}"
                    binding.tvPhoneNumber.text = "Mobile : ${user?.phone_number}"
                    binding.tvGender.text = "Gender : ${user?.gender}"

                    Glide.with(this).load(user?.profile_image).into(binding.ivProfileImage)
                } else {
                    Toast.makeText(this, "Failed to fetch user details", Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                Log.e("API_ERROR", "Error: ${error.message}")
                Toast.makeText(this, "Error fetching user details", Toast.LENGTH_SHORT).show()
            })
    }

    private fun fetchUserAddress(userId: String) {
        val disposable = apiService.getUserAddress("user_details", userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d("API_RESPONSE", "Full Response: $response")

                if (response.status == 200 && response.userAddress != null) {
                    val address = response.userAddress
                    val fullAddress = " Address : ${address.houseNumber}, ${address.flatName}\n" +
                            "${address.society}, ${address.area}\n" +
                            "${address.street}, ${address.landmark}\n" +
                            "Pincode: ${address.pincode}\n" +
                            "${address.city}, ${address.state}, ${address.country}"

                    binding.tvAddress.text = fullAddress
                    Log.d("API_RESPONSE", "Address: $fullAddress")
                } else {
                    Log.e("API_ERROR", response.message)
                    binding.tvAddress.text = "Address not found"
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                Log.e("API_ERROR", error.message ?: "Unknown Error")
                binding.tvAddress.text = "Error fetching address"
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear() // Clear any remaining disposables to avoid memory leaks
    }
}
