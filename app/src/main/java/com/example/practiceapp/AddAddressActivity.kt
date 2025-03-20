package com.example.practiceapp

import ApiClasses.RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practiceapp.databinding.ActivityUpdatAddressBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class AddAddressActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.instance
    private val compositeDisposable = CompositeDisposable()
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivityUpdatAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityUpdatAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val id = sessionManager.getUserId()

        binding.btnSubmit.setOnClickListener {
            val houseNumber = binding.etHouseNumber.text.toString()
            val flatName = binding.etFlatName.text.toString()
            val societyName = binding.etSocietyName.text.toString()
            val etCountry = binding.etCountry.text.toString()
            val etState = binding.etState.text.toString()
            val etCity = binding.etCity.text.toString()
            val area = binding.etArea.text.toString()
            val street = binding.etStreet.text.toString()
            val pincode = binding.etPincode.text.toString()

            addUserAddress(houseNumber, flatName, societyName, area, street, "", pincode, etCountry, etState, etCity, id)

            startActivity(Intent(this@AddAddressActivity, MainActivity::class.java))
        }


        binding.etCountry.setOnClickListener {
            val dialog = CountryBottomSheetDialog(this) { selectedCountry ->
                binding.etCountry.setText(selectedCountry.country_name)

                val stateDialog = StateBottomSheetDialog(this, selectedCountry.states) { selectedState ->
                    binding.etState.setText(selectedState.state_name)

                    val cityDialog = CityBottomSheetDialog(this, selectedState.cities) { selectedCity ->
                        binding.etCity.setText(selectedCity.city_name)
                    }
                    cityDialog.show()
                }
                stateDialog.show()
            }
            dialog.show()
        }


    }

    private fun addUserAddress(
        houseNumber: String,
        flatName: String,
        societyName: String,
        area: String,
        street: String,
        landmark: String,
        pincode: String,
        countryId: String,
        stateId: String,
        cityId: String,
        userId: String?
    ) {
        val disposable = apiService.addUserAddress(
            "user_address", houseNumber, flatName, societyName, area, street, landmark, pincode,
            countryId.toString(), stateId.toString(), cityId.toString(), userId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: ResponseBody ->
                Toast.makeText(this, "Response: ${response.string()}", Toast.LENGTH_LONG).show()
            }, { throwable: Throwable ->
                Toast.makeText(this, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
            })

        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
