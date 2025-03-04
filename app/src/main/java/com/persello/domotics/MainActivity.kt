package com.persello.domotics

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.persello.domotics.databinding.ActivityMainBinding
import com.persello.domotics.storage.auth.TokenStorage
import com.persello.domotics.ui.auth.LoginActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    private lateinit var tokenStorage: TokenStorage;
    private val mainScope = MainScope();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val navView: BottomNavigationView = binding.navView;

        val navController = findNavController(R.id.nav_host_fragment_activity_main);

        navController.addOnDestinationChangedListener { _, destination, _ ->
            checkTokenValidity();
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_favorites, R.id.navigation_devices, R.id.navigation_settings
            )
        );
        setupActionBarWithNavController(navController, appBarConfiguration);
        navView.setupWithNavController(navController);

        tokenStorage = TokenStorage(this);

        checkTokenValidity();
    }

    override fun onResume() {
        super.onResume();
        checkTokenValidity();
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent);
    }

    private fun checkTokenValidity() {
        mainScope.launch {
            if(tokenStorage.read().isEmpty()) {
                goToLogin();
            }
        }
    }

}