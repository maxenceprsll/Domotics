package com.persello.domotics.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.persello.domotics.api.Api
import com.persello.domotics.MainActivity
import com.persello.domotics.R
import com.persello.domotics.data.auth.AuthData
import com.persello.domotics.data.auth.TokenData
import com.persello.domotics.storage.auth.TokenStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var tokenStorage: TokenStorage;
    private val mainScope = MainScope();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenStorage = TokenStorage(this);
    }

    fun goToRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent);
    }

    private fun goToMainActivity() {
        finish();
    }

    private suspend fun saveToken(tokenData: TokenData) {
        val token = tokenData.token;
        tokenStorage.write(token);
    }

    fun login(view: View) {
        val mail = findViewById<EditText>(R.id.editTextLoginLogin).text.toString();
        val password = findViewById<EditText>(R.id.editTextLoginPassword).text.toString();

        val data = AuthData(mail, password);

        Api().post<AuthData, TokenData>("https://polyhome.lesmoulinsdudev.com/api/users/auth", data, ::loginSuccess);
    }

    fun loginSuccess(responseCode: Int, token: TokenData?) {
        val textViewError = findViewById<TextView>(R.id.textViewLoginError);

        if (responseCode == 200 && token != null) {
            mainScope.launch {
                saveToken(token);
                goToMainActivity();
            }
        } else if (responseCode == 400) {
            textViewError.text = "Les données fournies sont incorrectes";
        } else if (responseCode == 404) {
            textViewError.text = "Aucun utilisateur ne correspond aux identifiants donnés";
        } else if (responseCode == 500) {
            textViewError.text = "Une erreur s’est produite au niveau du serveur";

        }
    }
}