package Restaurents

import AdapterClasses.Adapters.AllRestaurantAdapter
import ApiClasses.RetrofitClient
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceapp.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class AllResturentActivity : AppCompatActivity() {

   private lateinit var SearchView : SearchView
   private lateinit var restaurantAdapter: AllRestaurantAdapter
    private lateinit var recyclerView: RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_resturent2)

        SearchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.allresrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                restaurantAdapter.filter.filter(query)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                restaurantAdapter.filter.filter(newText)
                return true
            }
        })

        fetchRestaurants()

    }

    @SuppressLint("CheckResult")
    private fun fetchRestaurants() {
        val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "all_restaurants")

        RetrofitClient.instance.getAllRestaurants(requestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.status == 200 && response.restaurants != null) {
                    restaurantAdapter = AllRestaurantAdapter(this,response.restaurants)
                    recyclerView.adapter = restaurantAdapter
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

    }
    }


