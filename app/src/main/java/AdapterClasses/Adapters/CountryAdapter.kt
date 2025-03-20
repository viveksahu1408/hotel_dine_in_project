package AdapterClasses.Adapters

import ModalClasses.Country
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceapp.databinding.ItemCountryBinding

class CountryAdapter(private val onItemClick: (Country) -> Unit) :
    RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private val countryList = mutableListOf<Country>()

    fun setData(countries: List<Country>) {
        countryList.clear()
        countryList.addAll(countries)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(countryList[position])
    }

    override fun getItemCount() = countryList.size

    inner class CountryViewHolder(private val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) {
            binding.tvCountryName.text = country.country_name
            binding.root.setOnClickListener { onItemClick(country) }
        }
    }
}
