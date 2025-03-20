package com.example.practiceapp

import AdapterClasses.Adapters.CountryAdapter
import ApiClasses.RetrofitClient
import ModalClasses.Country
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practiceapp.databinding.DialogCountrySelectionBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CountryBottomSheetDialog(
    context: Context,
    private val onCountrySelected: (Country) -> Unit
) : Dialog(context) {

    private var selectedCountryId: String = ""
   // private lateinit var sessionManager: SessionManager
    private lateinit var binding: DialogCountrySelectionBinding
    private val apiService = RetrofitClient.instance
    private val compositeDisposable = CompositeDisposable()

    private val countryAdapter = CountryAdapter { country ->
        onCountrySelected(country)
//        sessionManager = SessionManager(return@CountryAdapter)
//
//        sessionManager.saveCountryId(country.country_id)

        dismiss()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCountrySelectionBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)



        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countryAdapter
        }

        fetchCountries()
    }

    private fun fetchCountries() {
        compositeDisposable.add(
            apiService.getDropDownData("dropdown")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d("API_RESPONSE", response.toString())

                    if (response.status == 200 && response.data != null) {
                        countryAdapter.setData(response.data.countries)
                    } else {
                        Log.e("API_ERROR", "Invalid Response: $response")
                        Toast.makeText(context, "Failed to load countries", Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("API_ERROR", "Error: ${error.message}")
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                })
        )
    }




    override fun dismiss() {
        super.dismiss()
        compositeDisposable.clear()
    }
}
