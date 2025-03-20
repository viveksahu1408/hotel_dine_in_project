package com.example.practiceapp



import AdapterClasses.Adapters.StateAdapter
import ModalClasses.State
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practiceapp.databinding.DialogStateSelectionBinding


class StateBottomSheetDialog(
    context: Context,
    private val stateList: List<State>,
    private val onStateSelected: (State) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogStateSelectionBinding

    private var selectedStateId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogStateSelectionBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)




        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = StateAdapter(stateList) { selectedState ->
            onStateSelected(selectedState)

            dismiss()
        }
        binding.recyclerView.adapter = adapter
    }
}
