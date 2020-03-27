package com.example.myappointments.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.io.response.LoginResponse
import com.example.myappointments.util.PreferenceHelper
import com.example.myappointments.util.PreferenceHelper.get
import com.example.myappointments.util.PreferenceHelper.set
import com.example.myappointments.util.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val snackBar by lazy {
        Snackbar.make(mainLayout,
            R.string.press_back_again, Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences =
            PreferenceHelper.defaultPrefs(this)

        if (preferences["jwt", ""].contains("."))
            goToMenuActivity()

        tvGoToRegister.setOnClickListener {
            Toast.makeText(this,getString(R.string.please_fill_you_register_data), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        if(email.trim().isEmpty() || password.trim().isEmpty()){
            toast(getString(R.string.error_empty_credentials))
            return
        }

        val call = apiService.postLogin(email, password)
        call.enqueue(object: Callback<LoginResponse>{
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val loginResponse = response.body()
                    if(loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if(loginResponse.success){
                        createSessionPreference(loginResponse.jwt)
                        toast("Bienvenido " + loginResponse.user.name)
                        goToMenuActivity(true)
                    } else {
                        toast(getString(R.string.error_invalid_credentials))
                    }
                }
            }

        })
    }

    private fun goToMenuActivity(isUserInput: Boolean = false){
        val intent = Intent(this, MenuActivity::class.java)

        if(isUserInput){
            intent.putExtra("store_token", true)
        }
        startActivity(intent)
        finish()
    }

    private fun createSessionPreference(jwt: String){
        val preferences =
            PreferenceHelper.defaultPrefs(this)
        preferences["jwt"] = jwt
    }

    override fun onBackPressed() {
        if(snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }
}
