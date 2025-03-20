package com.example.practiceapp

import AdapterClasses.Adapters.FoodCategoryAdapter
import ApiClasses.RetrofitClient
import AdapterClasses.Adapters.RestaurantAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practiceapp.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var foodCategoryAdapter: FoodCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        sessionManager = SessionManager(this)


        binding.hotelRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fetchRestaurants()


        binding.foodCategoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fetchFoodCategories()


        binding.profileIcon.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        }


        binding.menu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }


        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, UpdateProfileActivity::class.java))
                    Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_bookings -> {
                    startActivity(Intent(this@MainActivity, AddAddressActivity::class.java))
                    Toast.makeText(this, "Add address", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_ratinHistory -> {
                    startActivity(Intent(this, BookingHistoryActivity::class.java))
                    Toast.makeText(this, "Booking History", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logout -> {
                    sessionManager.logoutUser()
                    startActivity(Intent(this, LoginActivity::class.java))
                    Toast.makeText(this, "Logout Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_totalRes -> {
                    startActivity(Intent(this, AllResturentActivity::class.java))
                    Toast.makeText(this, "Total Restaurants", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_change_pass -> {
                    Toast.makeText(this, "Change password", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                    Toast.makeText(this, "Notification", Toast.LENGTH_SHORT).show()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    @SuppressLint("CheckResult")
    private fun fetchRestaurants() {
        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.instance.getRestaurants()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                binding.progressBar.visibility = View.GONE

                if (response.status == 200) {
                    response.data?.let {
                        restaurantAdapter = RestaurantAdapter(this@MainActivity, it.filterNotNull())
                        binding.hotelRecyclerView.adapter = restaurantAdapter
                    }
                } else {
                    Toast.makeText(this, "No Data Found!", Toast.LENGTH_LONG).show()
                }
            }, { error ->
                binding.progressBar.visibility = View.GONE
                Log.e("MainActivity", "API Error: ${error.message}", error)
                Toast.makeText(this, "Failed to load restaurants!", Toast.LENGTH_LONG).show()
            })
    }

    @SuppressLint("CheckResult")
    private fun fetchFoodCategories() {
        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.instance.getFoodCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                binding.progressBar.visibility = View.GONE

                if (response.status == 200) {
                    response.data?.let { foodList ->
                        foodCategoryAdapter = FoodCategoryAdapter(this@MainActivity, foodList.filterNotNull())
                        binding.foodCategoryRecyclerView.adapter = foodCategoryAdapter
                    }
                } else {
                    Toast.makeText(this, "No Food Categories Found!", Toast.LENGTH_LONG).show()
                }
            }, { error ->
                binding.progressBar.visibility = View.GONE
                Log.e("MainActivity", "API Error: ${error.message}", error)
                Toast.makeText(this, "Failed to load food categories!", Toast.LENGTH_LONG).show()
            })
    }
}
