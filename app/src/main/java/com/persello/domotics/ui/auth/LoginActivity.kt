package com.persello.domotics.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.persello.domotics.api.Api
import com.persello.domotics.MainActivity
import com.persello.domotics.R
import com.persello.domotics.data.auth.AuthData

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    fun goToRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent);
    }

    fun login(view: View) {
        val mail = findViewById<EditText>(R.id.editTextLoginLogin).text.toString();
        val password = findViewById<EditText>(R.id.editTextLoginPassword).text.toString();

        val data = AuthData(mail, password);

        Api().post<AuthData, String>("https://polyhome.lesmoulinsdudev.com/api/users/auth", data, ::loginSuccess);
    }

    fun loginSuccess(responseCode: Int, token: String?) {
        if (responseCode == 200)
        {
            val intent = Intent(this, MainActivity::class.java);
            intent.putExtra("token", token);
            startActivity(intent);
            finish();
        }
    }
}