package com.persello.domotics.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.persello.domotics.api.Api
import com.persello.domotics.data.home.HomeData
import com.persello.domotics.databinding.FragmentFavoritesBinding
import com.persello.domotics.storage.auth.TokenStorage
import com.persello.domotics.storage.home.HomeStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var tokenStorage: TokenStorage;
    private lateinit var homeStorage: HomeStorage;

    private val _homes = MutableLiveData<List<HomeData>>();
    val homes: LiveData<List<HomeData>> = _homes;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val favoritesViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java);

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        val root: View = binding.root;

        val textView: TextView = binding.textFavorites;
        favoritesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it;
        };

        tokenStorage = TokenStorage(requireContext());
        homeStorage = HomeStorage(requireContext());

        fetchHomes();

        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        setupSpinner();
        observeViewModel();
        loadHomes();
    }

    private fun observeViewModel() {
        homes.observe(viewLifecycleOwner) { homes ->
            updateSpinner(homes);
        };
    }

    private fun setupSpinner() {
        binding.spinnerFavoritesHomes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (parent?.getItemAtPosition(position) as? HomeData)?.let {
                    MainScope().launch {
                        saveSelectedHouse(it);
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSpinner(homes: List<HomeData>) {
        val adapter = object : ArrayAdapter<HomeData>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            homes
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView;
                view.text = homes[position].toString();
                return view;
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView;
                view.text = homes[position].toString();
                return view;
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFavoritesHomes.adapter = adapter;
    }

    private fun saveSelectedHouse(house: HomeData) {
        MainScope().launch {
            homeStorage.saveSelectedHouseId(house.houseId.toString())
        }
    }

    private fun loadHomes() {
        MainScope().launch {
            _homes.value = homeStorage.read();
        }
    }

    private fun fetchHomes() {
        MainScope().launch {
            val token = tokenStorage.read();
            Api().get<ArrayList<HomeData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::onHomesReceived, securityToken = token);
        };
    }

    private fun onHomesReceived(responseCode: Int, homes: ArrayList<HomeData>?) {
        if (responseCode == 200 && homes != null) {
            MainScope().launch {
                homeStorage.write(homes);
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
}