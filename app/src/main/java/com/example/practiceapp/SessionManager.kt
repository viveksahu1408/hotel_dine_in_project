package com.example.practiceapp

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val PREF_NAME = "user_session"
    private val KEY_IS_LOGGED_IN = "is_logged_in"
    private val KEY_USER_ID = "user_id"
    private val KEY_FIRST_NAME = "first_name"
    private val KEY_MIDDLE_NAME = "middle_name"
    private val KEY_LAST_NAME = "last_name"
    private val KEY_EMAIL = "email"
    private val KEY_PHONE = "phone"
    private val KEY_GENDER = "gender"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // ✅ User login status set karega
    fun setLoginStatus(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }


    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }


    fun saveUserData(
        userId: String,
        firstName: String,
        middleName: String,
        lastName: String,
        email: String,
        phone: String,
        gender: String = ""
    ) {
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_FIRST_NAME, firstName)
        editor.putString(KEY_MIDDLE_NAME, middleName)
        editor.putString(KEY_LAST_NAME, lastName)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PHONE, phone)
        editor.putString(KEY_GENDER, gender)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }


    fun getUserData(): Map<String, String?> {
        return mapOf(
            "user_id" to sharedPreferences.getString(KEY_USER_ID, null),
            "first_name" to sharedPreferences.getString(KEY_FIRST_NAME, null),
            "middle_name" to sharedPreferences.getString(KEY_MIDDLE_NAME, null),
            "last_name" to sharedPreferences.getString(KEY_LAST_NAME, null),
            "email" to sharedPreferences.getString(KEY_EMAIL, null),
            "phone" to sharedPreferences.getString(KEY_PHONE, null),
            "gender" to sharedPreferences.getString(KEY_GENDER, null)
        )
    }


    fun logoutUser() {
        editor.clear()
        editor.apply()
    }

    fun loginactivitysave(user_id: String,  email: String ) {

        editor.putString("userEmail",email)  // Save email
//        editor.putString("userFirstName",firstName) // Save first name
//        editor.putString("userLastName", lastName)  // Save last name
        editor.putString("user_id", user_id)  // Save user id
        editor.apply()
    }

    fun saveUserId(user_id: String){
        editor.putString("user_id", user_id).apply()
    }
    fun getUserId() : String? {
        return sharedPreferences.getString("user_id" , "")
    }

    fun saveResturentId(restaurent_id: String){
        editor.putString("restaurent_id", restaurent_id).apply()
    }

    fun saveCountryId(counry_id: String){
        editor.putString("counry_id", counry_id).apply()
    }

    fun saveStateId(state_id: String){
        editor.putString("state_id", state_id).apply()
    }
    fun saveCityId(city_id: String){
        editor.putString("city_id", city_id).apply()
    }
    fun getCountyId() : String? {
        return sharedPreferences.getString("user_id" , "")
    }
    fun getStateId() : String? {
        return sharedPreferences.getString("user_id" , "")
    }
    fun getCityId() : String? {
        return sharedPreferences.getString("user_id" , "")
    }


}
