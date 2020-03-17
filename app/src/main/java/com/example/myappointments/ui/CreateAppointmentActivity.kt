package com.example.myappointments.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.model.Specialty
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.create_view_step_one.*
import kotlinx.android.synthetic.main.create_view_step_three.*
import kotlinx.android.synthetic.main.create_view_step_two.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val selectedCalendar = Calendar.getInstance()
    private var selectedTimeRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {
            if(etDescription.text.toString().length < 3){
                etDescription.error = getString(R.string.validate_appointment_description)
            } else {
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }

        btnNext2.setOnClickListener {
            when {
                etScheduledDate.text.toString().isEmpty() -> {
                    etScheduledDate.error = getString(R.string.validate_appointment_date)
                }
                selectedTimeRadioButton == null -> {
                    Snackbar.make(
                        createAppointmentLinearLayout,
                        R.string.validate_appointment_time,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    showAppointmentDataToConfirm()
                    cvStep2.visibility = View.GONE
                    cvStep3.visibility = View.VISIBLE
                }
            }
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "La cita se ha registrado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        loadSpecialties()

        val doctorOptions = arrayOf("Doctor A", "Doctor B", "Doctor C")
        spinnerDoctors.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, doctorOptions)
    }

    private fun loadSpecialties(){
       val call = apiService.getSpecialties()
        call.enqueue(object: Callback<ArrayList<Specialty>> {
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity,getString(R.string.label_error_loading_specialties), Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(
                call: Call<ArrayList<Specialty>>,
                response: Response<ArrayList<Specialty>>
            ) {
                if(response.isSuccessful){
                    val specialties = response.body()
                    val specialtyOptions = ArrayList<String>()

                    specialties?.forEach {
                        specialtyOptions.add(it.name)
                    }
                    spinnerSpecialties.adapter = ArrayAdapter(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, specialtyOptions)
                }
            }

        })
    }


    private fun showAppointmentDataToConfirm(){
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()

        val selectedRadioBtnID = radioGroupType.checkedRadioButtonId
        val selectedRadioType = radioGroupType.findViewById<RadioButton>(selectedRadioBtnID)

        tvConfirmType.text = selectedRadioType.text.toString()

        tvConfirmDoctorName.text = spinnerDoctors.selectedItem.toString()
        tvConfirmDate.text = etScheduledDate.text.toString()
        tvConfirmTime.text = selectedTimeRadioButton?.text.toString()
    }

    fun onClickScheduledDate(v: View?){

        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
           // Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_SHORT).show()
            selectedCalendar.set(y,m,d)
            etScheduledDate.setText(resources.getString(R.string.date_format, y, m.twoDigits(), d.twoDigits()))
            etScheduledDate.error = null
            displayRadioButtons()
        }

        // new dialog
        val datePickerDialog = DatePickerDialog(this, listener, year, month, dayOfMonth)

        //set limits
        val datePicker = datePickerDialog.datePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePicker.minDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 29)
        datePicker.maxDate = calendar.timeInMillis

        //show dialog
        datePickerDialog.show()
    }

    private fun displayRadioButtons(){
      //  radioGroup.clearCheck()
      //  radioGroup.removeAllViews()
      //  radioGroup.checkedRadioButtonId
        selectedTimeRadioButton = null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        val hours = arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener { view ->
                selectedTimeRadioButton?.isChecked = false
                selectedTimeRadioButton = view as RadioButton?
                selectedTimeRadioButton?.isChecked = true
            }

            if(goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)
            goToLeft = !goToLeft
        }

    }

    private fun Int.twoDigits() = if(this>=10) this.toString() else "0$this"

    override fun onBackPressed() {

        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
            cvStep2.visibility == View.VISIBLE -> {
                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE
            }
            cvStep1.visibility == View.VISIBLE -> {
                val builder = AlertDialog.Builder(this,
                    R.style.CustomDialogTheme
                )
                builder.setTitle(getString(R.string.dialog_exit_create_appointment))
                builder.setMessage(getString(R.string.dialog_exit_create_appointment_message))
                builder.setPositiveButton(getString(R.string.dialog_exit_create_appointment_positive)) { _, _ ->
                    finish()
                }
                builder.setNegativeButton(getString(R.string.dialog_exit_create_appointment_negative)) { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }

    }
}