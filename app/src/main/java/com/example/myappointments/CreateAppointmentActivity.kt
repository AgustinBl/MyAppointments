package com.example.myappointments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.create_view_step_one.*
import kotlinx.android.synthetic.main.create_view_step_three.*
import kotlinx.android.synthetic.main.create_view_step_two.*
import java.util.*

class CreateAppointmentActivity : AppCompatActivity() {

    private val selectedCalendar = Calendar.getInstance()
    private var selectedRadioButton: RadioButton? = null

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
            showAppointmentDataToConfirm()
            cvStep2.visibility = View.GONE
            cvStep3.visibility = View.VISIBLE
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "La cita se ha registrado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        val specialtyOptions = arrayOf("Specialty A", "Specialty B", "Specialty C")
        spinnerSpecialties.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, specialtyOptions)

        val doctorOptions = arrayOf("Doctor A", "Doctor B", "Doctor C")
        spinnerDoctors.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, doctorOptions)
    }

    private fun showAppointmentDataToConfirm(){
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()

        val selectedRadioBtnID = radioGroupType.checkedRadioButtonId
        val selectedRadioType = radioGroupType.findViewById<RadioButton>(selectedRadioBtnID)

        tvConfirmType.text = selectedRadioType.text.toString()

        tvConfirmDoctorName.text = spinnerDoctors.selectedItem.toString()
        tvConfirmDate.text = etScheduledDate.text.toString()
        tvConfirmTime.text = selectedRadioButton?.text.toString()
    }

    fun onClickScheduledDate(v: View?){

        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
           // Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_SHORT).show()
            selectedCalendar.set(y,m,d)
            etScheduledDate.setText(resources.getString(R.string.date_format, y, m.twoDigits(), d.twoDigits()))
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
        selectedRadioButton = null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        val hours = arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener { view ->
                selectedRadioButton?.isChecked = false
                selectedRadioButton = view as RadioButton?
                selectedRadioButton?.isChecked = true
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
                val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme)
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
