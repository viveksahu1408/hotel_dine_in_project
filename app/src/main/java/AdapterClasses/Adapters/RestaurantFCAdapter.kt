package AdapterClasses.Adapters


import ModalClasses.RestaurantFC
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practiceapp.R
import com.example.practiceapp.ResListFCActivity
import com.example.practiceapp.RestaurantDetailsActivity

class RestaurantFCAdapter  (private val context: Context,
                            private val restaurantFCList: List<RestaurantFC>)
    : RecyclerView.Adapter<RestaurantFCAdapter.RestaurantFCViewHolder>()
{


    class RestaurantFCViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

        val restaurantName: TextView = itemView.findViewById(R.id.tvRestaurantNamefc)
        val restaurantPrice: TextView = itemView.findViewById(R.id.tvRestaurantPricefc)
        val restaurantTime: TextView = itemView.findViewById(R.id.tvRestaurantTimefc)
       // val restaurantRating: TextView = itemView.findViewById(R.id.tvRestaurantRatingfc)
        val restaurantImage: ImageView = itemView.findViewById(R.id.ivRestaurantImagefc)
        val foodcategory: TextView = itemView.findViewById(R.id.foodcategoryfc)

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantFCAdapter.RestaurantFCViewHolder {

 val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reslist_foodcategory,parent,false)
        return RestaurantFCViewHolder(view)
    }


    override fun onBindViewHolder(holder: RestaurantFCAdapter.RestaurantFCViewHolder, position: Int) {
        val restaurantFCList = restaurantFCList[position]

        holder.restaurantName.text = restaurantFCList.name
        holder.restaurantPrice.text = "Price: ${restaurantFCList.price}"
        holder.restaurantTime.text = "Timing: ${restaurantFCList.openTime ?: "N/A"} - ${restaurantFCList.closeTime ?: "N/A"}"
        holder.foodcategory.text = restaurantFCList.foodType



        if (restaurantFCList.images?.isNotEmpty() == true) {
            Glide.with(holder.itemView.context)
                .load(restaurantFCList.images?.get(0))
                .placeholder(R.drawable.img_2)
                .into(holder.restaurantImage)
        }

        holder.restaurantImage.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            if (!restaurantFCList.restaurantId.isNullOrEmpty()) {
                intent.putExtra("restaurant_id", restaurantFCList.restaurantId)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Error: Restaurant ID is missing", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun getItemCount(): Int {
        return restaurantFCList.size
    }

}