package com.example.practiceapp

import AdapterClasses.Adapters.SlotAdapter
import ApiClasses.RetrofitClient
import ModalClasses.BookingApiResponse
import ModalClasses.SlotAvailableResponse
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.practiceapp.databinding.ActivityBookingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var sessionManager: SessionManager

    private var selectedDate: String = ""
    private var restaurantId: String? = null
    private lateinit var slotAdapter: SlotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)


        binding.slotRecyclerView.layoutManager = GridLayoutManager(this, 2)


        restaurantId = intent.getStringExtra("restaurant_id")
        if (restaurantId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No Restaurant ID", Toast.LENGTH_SHORT).show()
            finish()
        }


        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val formattedMonth = "%02d".format(month + 1)
                val formattedDay = "%02d".format(dayOfMonth)
                selectedDate = "$year-$formattedMonth-$formattedDay"
                binding.dateTextView.text = "Selected Date: $selectedDate"
                fetchAvailableSlots(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    @SuppressLint("CheckResult")
    private fun fetchAvailableSlots(date: String) {
        Log.d("BookingActivity", "Fetching slots for restaurantId: $restaurantId, date: $date")

        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.instance.getAvailableSlots(
            restaurantId = restaurantId!!,
            bookingDate = date
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                binding.progressBar.visibility = View.GONE
                handleApiResponse(response)
            }, { error ->
                binding.progressBar.visibility = View.GONE
                Log.e("BookingActivity", "API Error: ${error.message}", error)
                Toast.makeText(this, "Failed to load slots!", Toast.LENGTH_LONG).show()
            })
    }

    private fun handleApiResponse(response: SlotAvailableResponse) {
        if (response.status == 200) {
            val slots = mutableListOf<Any>()

            response.available_slots?.Breakfast?.filterNotNull()?.let { slots.addAll(it) }
            response.available_slots?.Lunch?.filterNotNull()?.let { slots.addAll(it) }
            response.available_slots?.Dinner?.filterNotNull()?.let { slots.addAll(it) }

            if (slots.isNotEmpty()) {
                slotAdapter = SlotAdapter(slots, this, restaurantId!!) { slotId ->
                    showBottomSheetDialog(slotId)
                }
                binding.slotRecyclerView.adapter = slotAdapter
            } else {
                Toast.makeText(this, "No slots available!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No Data Found!", Toast.LENGTH_LONG).show()
        }
    }

    private fun showBottomSheetDialog(slotId: String) {
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.dialog_booking, null)
        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setCancelable(true) // Ensure dialog is cancelable

        val etGuests = bottomSheetView.findViewById<EditText>(R.id.etNumberOfGuests)
        val etSpecialDish = bottomSheetView.findViewById<EditText>(R.id.etSpecialRequest)
        val btnConfirmBooking = bottomSheetView.findViewById<Button>(R.id.btnConfirmBooking)

        btnConfirmBooking.setOnClickListener {
            val guests = etGuests.text.toString().trim()
            val specialDish = etSpecialDish.text.toString().trim()

            if (guests.isEmpty()) {
                Toast.makeText(this, "Please enter number of guests!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            bottomSheetDialog.dismiss()
            confirmBooking(slotId, guests, specialDish)
        }

        bottomSheetDialog.show()
    }

    @SuppressLint("CheckResult")
    private fun confirmBooking(slotId: String, guests: String, specialDish: String) {

        val userId = sessionManager.getUserId()
        if (restaurantId.isNullOrEmpty() || slotId.isEmpty() || guests.isEmpty() || selectedDate.isEmpty()) {
            Log.e("BookingActivity", "Error: Missing required parameters")
            Toast.makeText(this, "Error: Missing required parameters!", Toast.LENGTH_LONG).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        if (userId != null) {
            RetrofitClient.instance.bookSlot(
                "booking",
                restaurantId!!,
                slotId,
                userId,
                guests,
                selectedDate,
                specialDish
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    binding.progressBar.visibility = View.GONE
                    handleBookingResponse(response)
                }, { error ->
                    binding.progressBar.visibility = View.GONE
                    Log.e("BookingActivity", "Booking API Error: ${error.message}", error)
                    Toast.makeText(this, "Booking failed!", Toast.LENGTH_LONG).show()
                })
        }
    }

    private fun handleBookingResponse(response: BookingApiResponse) {
        if (response.status == "200") {
            Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@BookingActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Booking Failed: ${response.message}", Toast.LENGTH_LONG).show()
        }
    }
}
