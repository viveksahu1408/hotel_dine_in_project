package Restaurents

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import ApiClasses.RetrofitClient
import android.content.Intent
import com.example.practiceapp.BookingActivity
import com.example.practiceapp.R
import com.example.practiceapp.databinding.ActivityRestaurantDetailsBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RestaurantDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityRestaurantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantId = intent.getStringExtra("restaurant_id")

        if (!restaurantId.isNullOrEmpty()) {
            restaurantId.toIntOrNull()?.let { id ->
                fetchRestaurantDetails(id) // API Call
            } ?: run {
                Toast.makeText(this, "Invalid Restaurant ID", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Error: No Restaurant ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.bookBtn.setOnClickListener {
            val intent = Intent(this@RestaurantDetailsActivity, BookingActivity::class.java)
            intent.putExtra("restaurant_id", restaurantId)
            startActivity(intent)
        }
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun fetchRestaurantDetails(restaurantId: Int) {
        RetrofitClient.instance.getRestaurantDetails("single_restaurant", restaurantId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d("API Response", response.toString()) // Debugging ke liye

                if (response.status == 200 && response.data != null) {
                    val restaurant = response.data

                    // Set Data in Views
                    binding.restaurantName.text = restaurant.restaurant_name ?: "No Name"
                    binding.restaurantName2.text = restaurant.restaurant_name ?: "No Name"
                    binding.restaurantPrice.text = "Price: ₹${restaurant.restaurant_price ?: "N/A"}"
                    binding.restaurantTime.text = "Timing: ${restaurant.restaurant_open_time ?: "N/A"} - ${restaurant.restaurant_close_time ?: "N/A"}"
                    binding.restaurantRating.text = "⭐ ${restaurant.avg_rating ?: "0.0"}"
                    binding.restaurantDescription.text = restaurant.restaurant_description ?: "No Description"
                    binding.restaurantCategories.text = "Categories: ${restaurant.food_categories?.joinToString() ?: "N/A"}"

                    if (restaurant.restaurant_images?.isNotEmpty() == true) {
                        Glide.with(this)
                            .load(restaurant.restaurant_images[0])
                            .placeholder(R.drawable.img_2)
                            .into(binding.restaurantImage)
                    }
                } else {
                    Toast.makeText(this, "No Restaurant Found!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }, { error ->
                Log.e("RestaurantDetails", "API Error: ${error.message}", error)
                Toast.makeText(this, "Failed to load details!", Toast.LENGTH_LONG).show()
                finish()
            })
    }
}
