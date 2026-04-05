package com.erramidouaa.weatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erramidouaa.weatherapp.domain.location.LocationTracker
import com.erramidouaa.weatherapp.domain.use_case.GetWeatherByCityUseCase
import com.erramidouaa.weatherapp.domain.use_case.GetWeatherUseCase
import com.erramidouaa.weatherapp.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getWeatherByCityUseCase: GetWeatherByCityUseCase,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private var currentUnits: String = "metric"
    private var lastCity: String? = null
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    init {
        loadWeatherByCity("New York")
    }

    fun updateUnits(isMetric: Boolean) {
        val newUnits = if (isMetric) "metric" else "imperial"
        if (currentUnits != newUnits) {
            currentUnits = newUnits
            refreshWeather()
        }
    }

    private fun refreshWeather() {
        lastCity?.let { loadWeatherByCity(it) } ?: run {
            if (lastLat != null && lastLon != null) {
                loadWeatherInfo()
            }
        }
    }

    fun loadWeatherInfo() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                lastLat = location.latitude
                lastLon = location.longitude
                lastCity = null
                
                when (val result = getWeatherUseCase(location.latitude, location.longitude, currentUnits)) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            weatherInfo = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            weatherInfo = null,
                            isLoading = false,
                            error = result.message
                        )
                    }
                    else -> Unit
                }
            } else {
                if (_state.value.weatherInfo == null) {
                    loadWeatherByCity("New York")
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }

    fun loadWeatherByCity(city: String) {
        lastCity = city
        lastLat = null
        lastLon = null
        
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )
            when (val result = getWeatherByCityUseCase(city, currentUnits)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        weatherInfo = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        weatherInfo = null,
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> Unit
            }
        }
    }
}
