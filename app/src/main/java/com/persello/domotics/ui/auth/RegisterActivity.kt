package com.persello.domotics.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
        val textViewError = findViewById<TextView>(R.id.textViewRegisterError);
        val login = findViewById<EditText>(R.id.editTextRegisterLogin).text.toString();
        val password = findViewById<EditText>(R.id.editTextRegisterPassword).text.toString();

        if (login.isEmpty() || password.isEmpty()) {
            textViewError.text = "Veuillez remplir tous les champs";
            return;
        }

        textViewError.text = "";

        this.data = AuthData(login, password);

        Api().post<AuthData>("https://polyhome.lesmoulinsdudev.com/api/users/register", this.data!!, ::registerSuccess);
    }

    fun registerSuccess(responseCode: Int) {
        val textViewError = findViewById<TextView>(R.id.textViewRegisterError);

        if (responseCode == 200) {
            goToLogin(findViewById(R.id.editTextRegisterLogin));
        } else if (responseCode == 400) {
            textViewError.text = "Les données fournies sont incorrectes";
        } else if (responseCode == 409) {
            textViewError.text = "Le login est déjà utilisé par un autre compte";
        } else if (responseCode == 500) {
            textViewError.text = "Une erreur s’est produite au niveau du serveur";
        }
    }
}