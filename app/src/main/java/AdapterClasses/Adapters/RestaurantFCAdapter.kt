package AdapterClasses.Adapters

import ModalClasses.RestaurantFC
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practiceapp.R
import Restaurents.RestaurantDetailsActivity
import java.util.Locale

class RestaurantFCAdapter(
    private val context: Context,
    private var restaurantFCList: List<RestaurantFC> // Original list
) : RecyclerView.Adapter<RestaurantFCAdapter.RestaurantFCViewHolder>(), Filterable {

    private var filteredList: List<RestaurantFC> = restaurantFCList // Filtered list

    class RestaurantFCViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.tvRestaurantNamefc)
        val restaurantPrice: TextView = itemView.findViewById(R.id.tvRestaurantPricefc)
        val restaurantTime: TextView = itemView.findViewById(R.id.tvRestaurantTimefc)
        val restaurantImage: ImageView = itemView.findViewById(R.id.ivRestaurantImagefc)
        val foodcategory: TextView = itemView.findViewById(R.id.foodcategoryfc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantFCViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reslist_foodcategory, parent, false)
        return RestaurantFCViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantFCViewHolder, position: Int) {
        val restaurant = filteredList[position]

        holder.restaurantName.text = restaurant.name
        holder.restaurantPrice.text = "Price: ${restaurant.price}"
        holder.restaurantTime.text = "Timing: ${restaurant.openTime ?: "N/A"} - ${restaurant.closeTime ?: "N/A"}"
        holder.foodcategory.text = restaurant.foodType

        if (restaurant.images?.isNotEmpty() == true) {
            Glide.with(holder.itemView.context)
                .load(restaurant.images?.get(0))
                .placeholder(R.drawable.img_2)
                .into(holder.restaurantImage)
        }

        holder.restaurantImage.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            if (!restaurant.restaurantId.isNullOrEmpty()) {
                intent.putExtra("restaurant_id", restaurant.restaurantId)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Error: Restaurant ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.getDefault())?.trim()

                filteredList = if (query.isNullOrEmpty()) {
                    restaurantFCList
                } else {
                    restaurantFCList.filter {
                        it.name.lowercase(Locale.getDefault()).contains(query) ||
                                it.foodType.lowercase(Locale.getDefault()).contains(query)
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<RestaurantFC>
                notifyDataSetChanged()
            }
        }
    }
}
