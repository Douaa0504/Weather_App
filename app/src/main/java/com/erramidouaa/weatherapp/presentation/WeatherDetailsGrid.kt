package com.erramidouaa.weatherapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.erramidouaa.weatherapp.domain.model.WeatherData
import com.erramidouaa.weatherapp.presentation.components.GlassBox

@Composable
fun WeatherDetailsGrid(
    data: WeatherData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailItem(
                label = "Humidity",
                value = "${data.humidity}%",
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            )
            DetailItem(
                label = "Wind",
                value = "${data.windSpeed} km/h",
                icon = Icons.Default.Air,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailItem(
                label = "Visibility",
                value = "${(data.visibility ?: 0) / 1000} km",
                icon = Icons.Default.Visibility,
                modifier = Modifier.weight(1f)
            )
            DetailItem(
                label = "Pressure",
                value = "${data.pressure ?: 0} hPa",
                icon = Icons.Default.Compress,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassBox(
        modifier = modifier,
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}
