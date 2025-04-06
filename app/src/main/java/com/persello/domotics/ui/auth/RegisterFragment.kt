package com.persello.domotics.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.persello.domotics.api.Api
import com.persello.domotics.R
import com.persello.domotics.data.auth.AuthData
import com.persello.domotics.databinding.FragmentRegisterBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null;
    private val binding get() = _binding!!;
    private var data: AuthData? = null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false);
        val root: View = binding.root;

        binding.btnTryRegister.setOnClickListener {
            register();
        }

        binding.btnGotoLogin.setOnClickListener {
            goToLogin();
        }

        return root;
    }

    private fun goToLogin() {
        findNavController().navigate(R.id.navigation_login);
    }


    private fun register() {
        val textViewError = binding.textViewRegisterError;
        val login = binding.editTextRegisterLogin.text.toString();
        val password = binding.editTextRegisterPassword.text.toString();

        if (login.isEmpty() || password.isEmpty()) {
            textViewError.text = "Veuillez remplir tous les champs";
            return;
        }

        textViewError.text = "";

        this.data = AuthData(login, password);

        Api().post<AuthData>("https://polyhome.lesmoulinsdudev.com/api/users/register", this.data!!, ::registerSuccess);
    }

    fun registerSuccess(responseCode: Int) {
        val textViewError = binding.textViewRegisterError;

        when (responseCode) {
            200 -> {
                goToLogin();
            }
            400 -> {
                textViewError.text = "Les données fournies sont incorrectes";
            }
            409 -> {
                textViewError.text = "Le login est déjà utilisé par un autre compte";
            }
            500 -> {
                textViewError.text = "Une erreur s’est produite au niveau du serveur";
            }
        }
    }
}