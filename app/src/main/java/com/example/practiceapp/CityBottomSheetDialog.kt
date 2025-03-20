package com.example.practiceapp


import AdapterClasses.Adapters.CityAdapter
import ModalClasses.City
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practiceapp.databinding.DialogCitySelectionBinding


class CityBottomSheetDialog(
    context: Context,
    private val cityList: List<City>,
    private val onCitySelected: (City) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogCitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogCitySelectionBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = CityAdapter(cityList) { selectedCity ->
            onCitySelected(selectedCity)
            dismiss()
        }
        binding.recyclerView.adapter = adapter
    }
}
