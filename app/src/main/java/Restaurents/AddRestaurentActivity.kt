package Restaurents

import ApiClasses.RetrofitClient
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practiceapp.R
import com.example.practiceapp.databinding.ActivityAddRestaurentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AddRestaurentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRestaurentBinding
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var licenceEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var foodTypeEditText: EditText
    private lateinit var openTimeEditText: EditText
    private lateinit var closeTimeEditText: EditText
    private lateinit var addButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddRestaurentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameEditText = findViewById(R.id.editTextName)
        emailEditText = findViewById(R.id.editTextEmail)
        phoneEditText = findViewById(R.id.editTextPhone)
        licenceEditText = findViewById(R.id.editTextLicence)
        priceEditText = findViewById(R.id.editTextPrice)
        descriptionEditText = findViewById(R.id.editTextDescription)
        foodTypeEditText = findViewById(R.id.editTextFoodType)
        openTimeEditText = findViewById(R.id.editTextOpenTime)
        closeTimeEditText = findViewById(R.id.editTextCloseTime)
        addButton = findViewById(R.id.buttonAddRestaurant)

        addButton.setOnClickListener {
            addRestaurant()
        }
    }

    @SuppressLint("CheckResult")
    private fun addRestaurant() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val licence = licenceEditText.text.toString()
        val price = priceEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val foodType = foodTypeEditText.text.toString()
        val openTime = openTimeEditText.text.toString()
        val closeTime = closeTimeEditText.text.toString()

        RetrofitClient.instance.addRestaurant(
            restaurantName = name,
            restaurantEmail = email,
            restaurantPhoneNumber = phone,
            restaurantLicenceNo = licence,
            restaurantPrice = price,
            restaurantDescription = description,
            restaurantFoodType = foodType,
            restaurantOpenTime = openTime,
            restaurantCloseTime = closeTime
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
            }, { error ->

                Toast.makeText(this, "Failed: ${error.message}", Toast.LENGTH_LONG).show()
            })


    }


}
