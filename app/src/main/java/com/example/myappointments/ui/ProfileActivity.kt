package com.example.myappointments.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.model.User
import com.example.myappointments.util.PreferenceHelper
import com.example.myappointments.util.PreferenceHelper.get
import com.example.myappointments.util.toast
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.item_appointment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val jwt = preferences["jwt", ""]
        val authHeader = "Bearer $jwt"

        val call = apiService.getUser(authHeader)
        call.enqueue(object: Callback<User>{
            override fun onFailure(call: Call<User>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    val user = response.body()
                    if(user != null)
                        displayProfileData(user)
                }
            }

        })

      //  Handler().postDelayed({
      //      displayProfileData()
      //  }, 3000)
    }

    private fun displayProfileData(user: User){
        etName.setText(user.name)
        etPhone.setText(user.phone)
        etAddress.setText(user.address)

        progressBarProfile.visibility = View.GONE
        LinearLayoutProfile.visibility = View.VISIBLE

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile(){
        val name = etName.text.toString()
        val phone = etPhone.text.toString()
        val address = etAddress.text.toString()

        if(name.length < 4){
            InputLayoutName.error = getString(R.string.error_profile_name)
            return
        }

        val jwt = preferences["jwt", ""]
        val authHeader = "Bearer $jwt"

        val call = apiService.postUser(authHeader, name, phone, address)
        call.enqueue(object: Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    toast(getString(R.string.profile_success_save))
                    finish()
                }
            }

        })
    }
}
