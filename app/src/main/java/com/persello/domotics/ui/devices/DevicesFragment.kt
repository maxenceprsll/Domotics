package com.persello.domotics.ui.devices

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.persello.domotics.api.Api
import com.persello.domotics.data.device.DeviceData
import com.persello.domotics.data.device.DevicesData
import com.persello.domotics.databinding.FragmentDevicesBinding
import com.persello.domotics.storage.auth.TokenStorage
import com.persello.domotics.storage.device.DeviceStorage
import com.persello.domotics.storage.home.HomeStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

class DevicesFragment : Fragment() {

    private var _binding: FragmentDevicesBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var adapter: DevicesAdapter;
    private lateinit var tokenStorage: TokenStorage;
    private lateinit var homeStorage: HomeStorage;
    private lateinit var deviceStorage: DeviceStorage;

    private val _devices = MutableLiveData<ArrayList<DeviceData>>();
    private val devices: LiveData<ArrayList<DeviceData>> = _devices;

    private val handler = Handler(Looper.getMainLooper());
    private val fetchDeviceRunnable = object : Runnable {
        override fun run() {
            fetchDevices();
            handler.postDelayed(this, 100);
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val devicesViewModel = ViewModelProvider(this).get(DevicesViewModel::class.java);

        _binding = FragmentDevicesBinding.inflate(inflater, container, false);
        val root: View = binding.root;

        val textView: TextView = binding.textDevices
        devicesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        };

        tokenStorage = TokenStorage(requireContext());
        homeStorage = HomeStorage(requireContext());
        deviceStorage = DeviceStorage(requireContext());

        fetchDevices();

        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        observeViewModel();
        handler.post(fetchDeviceRunnable);
    }

    private fun observeViewModel() {
        devices.observe(viewLifecycleOwner) { devices ->
            updateRecyclerView(devices);
        }
    }

    private fun setupRecyclerView() {
        adapter = DevicesAdapter();
        binding.recyclerViewDevices.adapter = adapter;
        binding.recyclerViewDevices.layoutManager =
            GridLayoutManager(requireContext(), 2);
    }

    private fun updateRecyclerView(devices: List<DeviceData>) {
        adapter.setDevices(devices);
    }

    private fun fetchDevices() {
        MainScope().launch {
            val token = tokenStorage.read();
            val houseId = homeStorage.readSelectedHouseId();
            val path = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices";
            Api().get<DevicesData>(path, ::onDevicesReceived, securityToken = token);
        };
    }

    private fun onDevicesReceived(responseCode: Int, devices: DevicesData?) {
        if (responseCode == 200 && devices != null) {
            MainScope().launch {
                deviceStorage.write(devices.devices);
                _devices.value = deviceStorage.read();
            }
        } else if (responseCode == 400 || responseCode == 500) {
            // Please fetch again
        } else if (responseCode == 403) {
            MainScope().launch {
                tokenStorage.clearToken();
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(fetchDeviceRunnable);
        _binding = null;
    }
}