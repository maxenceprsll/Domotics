package com.persello.domotics.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.persello.domotics.api.Api
import com.persello.domotics.R
import com.persello.domotics.data.auth.AuthData
import com.persello.domotics.data.auth.TokenData
import com.persello.domotics.databinding.FragmentLoginBinding
import com.persello.domotics.storage.auth.TokenStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var tokenStorage: TokenStorage;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false);
        val root: View = binding.root;

        tokenStorage = TokenStorage(requireContext());

        binding.btnTryLogin.setOnClickListener {
            login();
        }

        binding.btnGotoRegister.setOnClickListener {
            goToRegister();
        }

        return root;
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    private fun goToRegister() {
        findNavController().navigate(R.id.navigation_register);
    }

    private fun goToMainActivity() {
        findNavController().navigate(R.id.navigation_favorites);
    }

    private fun login() {
        val mail = binding.editTextLoginLogin.text.toString();
        val password = binding.editTextLoginPassword.text.toString();

        val data = AuthData(mail, password);

        Api().post<AuthData, TokenData>("https://polyhome.lesmoulinsdudev.com/api/users/auth", data, ::loginSuccess);
    }

    fun loginSuccess(responseCode: Int, token: TokenData?) {
        val textViewError = view?.findViewById<TextView>(R.id.textViewLoginError);

        if (responseCode == 200 && token != null) {
            MainScope().launch {
                tokenStorage.write(token.token);
                goToMainActivity();
            }
        } else if (responseCode == 400) {
            textViewError?.text = "Les données fournies sont incorrectes";
        } else if (responseCode == 404) {
            textViewError?.text = "Aucun utilisateur ne correspond aux identifiants donnés";
        } else if (responseCode == 500) {
            textViewError?.text = "Une erreur s’est produite au niveau du serveur";
        }
    }
}