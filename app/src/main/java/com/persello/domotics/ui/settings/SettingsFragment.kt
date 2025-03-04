package com.persello.domotics.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.persello.domotics.databinding.FragmentSettingsBinding
import com.persello.domotics.storage.auth.TokenStorage
import com.persello.domotics.ui.auth.LoginActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var tokenStorage: TokenStorage;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java);

        _binding = FragmentSettingsBinding.inflate(inflater, container, false);
        val root: View = binding.root;

        val textView: TextView = binding.textSettings;
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it;
        }

        tokenStorage = TokenStorage(requireContext());

        binding.btnSettingsLogout.setOnClickListener {
            logoutUser()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logoutUser() {
        MainScope().launch {
            tokenStorage.clearToken();

            requireActivity().run {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}