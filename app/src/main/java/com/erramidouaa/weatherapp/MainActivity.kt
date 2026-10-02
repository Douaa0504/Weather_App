/**
 * SkyCast Weather App
 * Développé par Douaa ERRAMI © 2026
 * Tous droits réservés
 */

package com.erramidouaa.weatherapp

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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

// Point d'entrée Android annoté pour l'injection de dépendances avec Hilt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Initialisation des ViewModels via Hilt
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    // Gestionnaire pour la demande de permissions (localisation)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Activation de l'écran de démarrage (Splash Screen)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Configuration de l'affichage bord-à-bord (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Initialisation du lanceur de permissions
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Si la localisation est accordée, on charge les données météo
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                weatherViewModel.loadWeatherInfo()
            }
        }

        // Lancement effectif de la demande de permissions au démarrage
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ))

        setContent {
            // Observation de l'état des paramètres (langue, thème)
            val settingsState by settingsViewModel.state.collectAsState()

            // Gestion dynamique du changement de langue (Locale)
            val context = LocalContext.current
            val locale = Locale(settingsState.language.code)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)

            // Définition de la direction de l'UI (Droite à Gauche pour l'Arabe)
            val layoutDirection = if (settingsState.language == AppLanguage.ARABIC) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            // Application du fournisseur de direction et du thème global
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                WeatherAppTheme(theme = settingsState.theme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Configuration de la navigation Compose
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = Screen.Weather.route) {

                            // Route vers l'écran principal de la météo
                            composable(Screen.Weather.route) {
                                WeatherScreen(
                                    viewModel = weatherViewModel,
                                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                                )
                            }

                            // Route vers l'écran des paramètres
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