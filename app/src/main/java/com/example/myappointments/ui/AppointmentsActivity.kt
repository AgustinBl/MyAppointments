package com.example.myappointments.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.model.Appointment
import com.example.myappointments.util.PreferenceHelper
import com.example.myappointments.util.PreferenceHelper.get
import com.example.myappointments.util.toast
import kotlinx.android.synthetic.main.activity_appointments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentsActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy{
        ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    private val appointmentAdapter = AppointmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        loadAppointments()



        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = appointmentAdapter
    }

    private fun loadAppointments(){
        val jwt = preferences["jwt", ""]
        val call = apiService.getAppointments("Bearer $jwt")
        call.enqueue(object: Callback<ArrayList<Appointment>> {
            override fun onFailure(call: Call<ArrayList<Appointment>>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<ArrayList<Appointment>>,
                response: Response<ArrayList<Appointment>>
            ) {
                if(response.isSuccessful){
                    response.body()?.let {
                        appointmentAdapter.appointments = it
                        appointmentAdapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }
}
