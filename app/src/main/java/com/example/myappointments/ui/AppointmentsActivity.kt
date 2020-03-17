package com.example.myappointments.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappointments.R
import com.example.myappointments.model.Appointment
import kotlinx.android.synthetic.main.activity_appointments.*

class AppointmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        val appointments = ArrayList<Appointment>()

        appointments.add(
            Appointment(1, "Doctor Test 1", "10/03/20", "3:00 AM")
        )
        appointments.add(
            Appointment(2, "Doctor Test 2", "20/03/20", "5:00 AM")
        )
        appointments.add(
            Appointment(2, "Doctor Test 3", "22/03/20", "8:00 PM")
        )


        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter =
            AppointmentAdapter(appointments)
    }
}
