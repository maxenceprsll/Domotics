package com.persello.domotics.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.persello.domotics.R
import com.persello.domotics.api.Api
import com.persello.domotics.data.user.UserData
import com.persello.domotics.databinding.FragmentSettingsBinding
import com.persello.domotics.storage.auth.TokenStorage
import com.persello.domotics.storage.device.DeviceStorage
import com.persello.domotics.storage.home.HomeStorage
import com.persello.domotics.storage.user.UserStorage
import com.persello.domotics.ui.auth.LoginActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var tokenStorage: TokenStorage;
    private lateinit var homeStorage: HomeStorage;
    private lateinit var deviceStorage: DeviceStorage;
    private lateinit var userStorage: UserStorage;

    private val _users = MutableLiveData<List<UserData>>();
    val users: LiveData<List<UserData>> = _users;

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
        homeStorage = HomeStorage(requireContext());
        deviceStorage = DeviceStorage(requireContext());
        userStorage = UserStorage(requireContext());

        binding.btnSettingsLogout.setOnClickListener {
            logoutUser()
        }

        fetchUsers();

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        setupList();
        observeViewModel();
    }

    private fun observeViewModel() {
        users.observe(viewLifecycleOwner) { users ->
            updateList(users);
        };
    }

    private fun setupList() {
        binding.listViewSettingsUsers.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, mutableListOf());
    }

    private fun updateList(users: List<UserData>) {
        val adapter = SettingsAdapter(requireContext(), users);
        binding.listViewSettingsUsers.adapter = adapter;
        adapter.notifyDataSetChanged();
    }

    private fun fetchUsers() {
        MainScope().launch {
            val token = tokenStorage.read();
            val houseId = homeStorage.readSelectedHouseId();

            Api().get<ArrayList<UserData>>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", ::onUsersReceived, securityToken = token);
        }
    }

    private fun onUsersReceived(responseCode: Int, users: ArrayList<UserData>?) {
        if (responseCode == 200 && users != null) {
            MainScope().launch {
                userStorage.write(users)
                _users.value = userStorage.read();
            }
        } else if (responseCode == 403 || responseCode == 500) {
            MainScope().launch {
                tokenStorage.clearToken();
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logoutUser() {
        MainScope().launch {
            tokenStorage.clearToken();
            homeStorage.clear();
            deviceStorage.clear();
            userStorage.clear();

            requireActivity().run {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume();
        fetchUsers();
    }
}