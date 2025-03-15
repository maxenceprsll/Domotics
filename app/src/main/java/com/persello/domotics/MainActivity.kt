package com.persello.domotics

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.persello.domotics.api.Api
import com.persello.domotics.data.device.CommandData
import com.persello.domotics.data.device.DeviceData
import com.persello.domotics.databinding.ActivityMainBinding
import com.persello.domotics.storage.DataStoreSingleton.dataStore
import com.persello.domotics.storage.auth.TokenStorage
import com.persello.domotics.storage.device.DeviceStorage
import com.persello.domotics.storage.home.HomeStorage
import com.persello.domotics.ui.auth.LoginActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    private lateinit var tokenStorage: TokenStorage;
    private lateinit var homeStorage: HomeStorage;
    private lateinit var deviceStorage: DeviceStorage;
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
        homeStorage = HomeStorage(this);
        deviceStorage = DeviceStorage(this);

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

    fun sendCommand(view: View) {
        MainScope().launch {
            val token = tokenStorage.read();
            val houseId = homeStorage.readSelectedHouseId();

            val device = view.tag as DeviceData;

            val deviceId = device.id;

            var commandData = CommandData("");

            if (device.type == "rolling shutter" || device.type == "sliding shutter" || device.type == "garage door") {
                if (device.opening == 1.0 && device.openingMode != 2) {
                    commandData = CommandData("CLOSE");
                } else if (device.opening == 0.0 && device.openingMode != 2) {
                    commandData = CommandData("OPEN");
                } else if (device.opening > 0 && device.opening < 1 && device.openingMode != 2) {
                    commandData = CommandData("STOP");
                } else if (device.openingMode == 2) {
                    if (device.opening >= 0.5) {
                        commandData = CommandData("OPEN");
                    } else {
                        commandData = CommandData("CLOSE");
                    }
                }
            } else if (device.type == "light") {
                if (device.power == 1) {
                    commandData = CommandData("TURN OFF");
                } else {
                    commandData = CommandData("TURN ON");
                }
            } else {
                commandData = CommandData("UNKNOWN");
            }
            Api().post<CommandData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", commandData, ::onCommandSent, securityToken = token);
        }
    }

    private fun onCommandSent(responseCode: Int) {
        when (responseCode) {
            200 -> {
                // Good fetch data
                System.out.println("Command sent");
            }
            400, 500 -> {
                // Please try again
            }
            403 -> {
                MainScope().launch {
                    tokenStorage.clearToken();
                }
            }
        }
    }

}