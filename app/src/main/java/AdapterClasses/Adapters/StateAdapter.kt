package AdapterClasses.Adapters

import ModalClasses.State


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceapp.databinding.ItemStateBinding


class StateAdapter(
    private val states: List<State>,
    private val onItemClick: (State) -> Unit
) : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val binding = ItemStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        holder.bind(states[position])
    }

    override fun getItemCount() = states.size

    inner class StateViewHolder(private val binding: ItemStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(state: State) {
            binding.tvStateName.text = state.state_name
            binding.root.setOnClickListener { onItemClick(state) }
        }
    }
}
