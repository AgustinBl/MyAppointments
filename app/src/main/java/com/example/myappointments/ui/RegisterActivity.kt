package com.example.myappointments.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myappointments.R
import com.example.myappointments.io.ApiService
import com.example.myappointments.io.response.LoginResponse
import com.example.myappointments.util.PreferenceHelper
import com.example.myappointments.util.PreferenceHelper.set
import com.example.myappointments.util.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnConfirmRegister.setOnClickListener {
            performRegister()
        }
    }

    private fun performRegister() {
        val name = etRegisterName.text.toString().trim()
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString()
        val passwordConfirmation = etRegisterPasswordConfirm.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            toast(getString(R.string.error_register_empty_fields))
            return
        }

        if (password != passwordConfirmation) {
            toast(getString(R.string.error_register_password_match))
            return
        }

        val call = apiService.postRegister(name, email, password, passwordConfirmation)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse == null) {
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if (loginResponse.success) {
                        createSessionPreference(loginResponse.jwt)
                        toast("Bienvenido " + loginResponse.user.name)
                        goToMenuActivity(true)
                    } else {
                        toast(getString(R.string.error_invalid_credentials))
                    }
                } else {
                    toast(getString(R.string.error_register_validation))
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
}

