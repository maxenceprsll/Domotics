package com.persello.domotics.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.persello.domotics.api.Api
import com.persello.domotics.R
import com.persello.domotics.data.auth.AuthData

class RegisterActivity : AppCompatActivity() {

    private var data: AuthData? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun goToLogin(view: View) {
        finish();
    }


    fun register(view: View) {
        val login = findViewById<EditText>(R.id.editTextRegisterLogin).text.toString();
        val password = findViewById<EditText>(R.id.editTextRegisterPassword).text.toString();

        this.data = AuthData(login, password);

        Api().post<AuthData>("https://polyhome.lesmoulinsdudev.com/api/users/register", this.data!!, ::registerSuccess);
    }

    fun registerSuccess(responseCode: Int) {
        if (responseCode == 200)
        {
            goToLogin(findViewById(R.id.editTextRegisterLogin));
        }
    }
}