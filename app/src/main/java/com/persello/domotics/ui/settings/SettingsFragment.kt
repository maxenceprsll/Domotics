package com.persello.domotics.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.persello.domotics.MainActivity
import com.persello.domotics.R
import com.persello.domotics.api.Api
import com.persello.domotics.data.home.HomeData
import com.persello.domotics.data.user.UserData
import com.persello.domotics.data.user.UserLoginData
import com.persello.domotics.databinding.FragmentSettingsBinding
import com.persello.domotics.storage.auth.TokenStorage
import com.persello.domotics.storage.device.DeviceStorage
import com.persello.domotics.storage.home.HomeStorage
import com.persello.domotics.storage.user.UserStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var tokenStorage: TokenStorage;
    private lateinit var homeStorage: HomeStorage;
    private lateinit var deviceStorage: DeviceStorage;
    private lateinit var userStorage: UserStorage;

    private var currentHome: HomeData? = null;

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
        };

        tokenStorage = TokenStorage(requireContext());
        homeStorage = HomeStorage(requireContext());
        deviceStorage = DeviceStorage(requireContext());
        userStorage = UserStorage(requireContext());

        binding.btnSettingsLogout.setOnClickListener {
            logoutUser();
            val intent = Intent(requireContext(), MainActivity::class.java);
            startActivity(intent);
        };

        binding.btnSettingsAddUser.setOnClickListener {
            val userLogin = binding.editTextSettingsUserLogin.text.toString()
            val userLoginData = UserLoginData(userLogin);
            addUser(userLoginData);
        };

        MainScope().launch {
            currentHome = homeStorage.read(homeStorage.readSelectedHouseId());
        };

        fetchUsers();

        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        setupList();
        setupForm();
        observeViewModel();
    }

    private fun observeViewModel() {
        users.observe(viewLifecycleOwner) { users ->
            updateList(users);
        };
    }

    private fun setupForm() {
        MainScope().launch {
            val home = homeStorage.read(homeStorage.readSelectedHouseId());
            if (home != null && !home.owner) {
                binding.editTextSettingsUserLogin.visibility = View.GONE;
                binding.btnSettingsAddUser.visibility = View.GONE;
            }
        }
    }

    private fun setupList() {
        binding.listViewSettingsUsers.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, mutableListOf());
    }

    private fun updateList(users: List<UserData>) {
        if (currentHome == null) {
            return;
        }
        val adapter = SettingsAdapter(requireContext(), users, currentHome!!.owner);
        binding.listViewSettingsUsers.adapter = adapter;
        adapter.notifyDataSetChanged();

        binding.listViewSettingsUsers.post {
            for (i in 0 until binding.listViewSettingsUsers.childCount) {
                val child = binding.listViewSettingsUsers.getChildAt(i)
                val btn = child.findViewById<Button>(R.id.btnUsersRemoveUser)
                btn.setOnClickListener {
                    removeUser(it);
                };
            }
        }
    }

    private fun logoutUser() {
        MainScope().launch {
            tokenStorage.clearToken();
        }
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
        } else if (responseCode == 500) {
            // Please try again
        } else if (responseCode == 403) {
            logoutUser();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addUser(userLoginData: UserLoginData) {
        MainScope().launch {
            val token = tokenStorage.read();
            val houseId = homeStorage.readSelectedHouseId();
            Api().post<UserLoginData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", userLoginData, ::onUserAdded, securityToken = token);
        }
    }

    private fun onUserAdded(responseCode: Int) {
        when (responseCode) {
            200 -> {
                // Good fetch data
                System.out.println("User added");
                fetchUsers();
                binding.editTextSettingsUserLogin.text.clear();
            }
            400, 500 -> {
                // Please try again
            }
            409 -> {
                // User already exists
                System.out.println("User already exists");
            }
            403 -> {
                logoutUser();
            }
        }
    }

    private fun removeUser(view: View) {
        MainScope().launch {
            val token = tokenStorage.read();
            val houseId = homeStorage.readSelectedHouseId();
            val userData = view.tag as UserData;
            val deleteData = UserLoginData(userData.userLogin);
            System.out.println(userData.userLogin);
            Api().delete<UserLoginData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", deleteData, ::onUserRemoved, securityToken = token);
        }
    }

    private fun onUserRemoved(responseCode: Int) {
        when (responseCode) {
            200 -> {
                // Good fetch data
                System.out.println("User removed");
                fetchUsers();
            }
            400, 500 -> {
                // Please try again
            }
            403 -> {
                logoutUser();
            }
        }
    }

    override fun onResume() {
        super.onResume();
        fetchUsers();
    }
}