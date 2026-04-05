package com.erramidouaa.weatherapp.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.erramidouaa.weatherapp.R
import com.erramidouaa.weatherapp.domain.model.WeatherData
import com.erramidouaa.weatherapp.presentation.components.GlassBox
import com.erramidouaa.weatherapp.presentation.components.ShimmerLoading
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val (bgGradient, contentColor) = when (state.weatherInfo?.current?.icon?.take(2)) {
        "01", "02" -> listOf(Color(0xFFFF9800), Color(0xFFFF5722)) to Color.White // Sunny
        "03", "04" -> listOf(Color(0xFF90A4AE), Color(0xFF607D8B)) to Color.White // Cloudy
        "09", "10", "11" -> listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)) to Color.White // Rainy
        "13" -> listOf(Color(0xFFE1F5FE), Color(0xFF81D4FA)) to Color(0xFF0277BD) // Snow
        else -> listOf(Color(0xFF1B3B5A), Color(0xFF102840)) to Color.White
    }

    PullToRefreshBox(
        isRefreshing = state.isLoading && state.weatherInfo != null,
        onRefresh = {
            if (searchQuery.isNotBlank()) {
                viewModel.loadWeatherByCity(searchQuery)
            } else {
                viewModel.loadWeatherInfo()
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(bgGradient))
        ) {
            if (state.isLoading && state.weatherInfo == null) {
                ShimmerLoading()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { 
                                if (searchQuery.isNotBlank()) {
                                    viewModel.loadWeatherByCity(searchQuery)
                                }
                            },
                            contentColor = contentColor,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = contentColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = state.weatherInfo != null,
                        enter = fadeIn() + expandVertically()
                    ) {
                        state.weatherInfo?.let { info ->
                            Column {
                                MainWeatherInfo(info.current, info.city, contentColor)
                                
                                Text(
                                    text = stringResource(R.string.forecast_5_days),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                                HourlyForecastRow(info.hourlyForecast, contentColor)

                                WeatherDetailsGrid(info.current, Modifier.padding(top = 16.dp))
                                
                                ForecastList(info.dailyForecast, Modifier.padding(top = 8.dp))
                            }
                        }
                    }

                    state.error?.let { error ->
                        ErrorMessage(error, onRetry = {
                            if (searchQuery.isNotBlank()) viewModel.loadWeatherByCity(searchQuery) else viewModel.loadWeatherInfo()
                        })
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_city), color = contentColor.copy(alpha = 0.7f)) },
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = contentColor)
            }
        },
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = contentColor,
            focusedTextColor = contentColor,
            unfocusedTextColor = contentColor
        )
    )
}

@Composable
fun MainWeatherInfo(data: WeatherData, city: String, contentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = city, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = contentColor)
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${data.icon}@4x.png",
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Text(text = "${data.temperature.roundToInt()}°", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black, color = contentColor)
        Text(
            text = stringResource(R.string.feels_like, data.feelsLike?.roundToInt() ?: 0) + " • " + data.description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            },
            style = MaterialTheme.typography.titleLarge,
            color = contentColor.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun HourlyForecastRow(forecast: List<WeatherData>, contentColor: Color) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(forecast) { data ->
            val time = remember(data.time) {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(data.time * 1000))
            }
            GlassBox(
                modifier = Modifier.width(100.dp),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = time, style = MaterialTheme.typography.labelMedium, color = contentColor)
                    AsyncImage(model = "https://openweathermap.org/img/wn/${data.icon}@2x.png", contentDescription = null, modifier = Modifier.size(40.dp))
                    Text(text = "${data.temperature.roundToInt()}°", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = Color.White, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text(stringResource(R.string.retry)) }
    }
}
