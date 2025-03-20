package AdapterClasses.Adapters


import ModalClasses.City
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceapp.databinding.ItemCityBinding


class CityAdapter(
    private val cities: List<City>,
    private val onItemClick: (City) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount() = cities.size

    inner class CityViewHolder(private val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(city: City) {
            binding.tvCityName.text = city.city_name
            binding.root.setOnClickListener { onItemClick(city) }
        }
    }
}
