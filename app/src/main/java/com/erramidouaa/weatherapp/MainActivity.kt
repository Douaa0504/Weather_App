/**
 * SkyCast Weather App
 * Developed by Douaa ERRAMI © 2026
 * All Rights Reserved
 */

package com.erramidouaa.weatherapp

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erramidouaa.weatherapp.presentation.WeatherScreen
import com.erramidouaa.weatherapp.presentation.WeatherViewModel
import com.erramidouaa.weatherapp.presentation.navigation.Screen
import com.erramidouaa.weatherapp.presentation.settings.AppLanguage
import com.erramidouaa.weatherapp.presentation.settings.SettingsScreen
import com.erramidouaa.weatherapp.presentation.settings.SettingsViewModel
import com.erramidouaa.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                weatherViewModel.loadWeatherInfo()
            }
        }
        
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ))

        setContent {
            val settingsState by settingsViewModel.state.collectAsState()
            
            // Handle Locale change
            val context = LocalContext.current
            val locale = Locale(settingsState.language.code)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)

            val layoutDirection = if (settingsState.language == AppLanguage.ARABIC) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                WeatherAppTheme(theme = settingsState.theme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = Screen.Weather.route) {
                            composable(Screen.Weather.route) {
                                WeatherScreen(
                                    viewModel = weatherViewModel,
                                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                                )
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    onBackClick = { navController.popBackStack() },
                                    viewModel = settingsViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
