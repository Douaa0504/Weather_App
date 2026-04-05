package com.erramidouaa.weatherapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.erramidouaa.weatherapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Theme Selection
            Column {
                Text(
                    text = stringResource(R.string.theme),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppTheme.values().forEach { theme ->
                        FilterChip(
                            selected = state.theme == theme,
                            onClick = { viewModel.onThemeChange(theme) },
                            label = {
                                Text(
                                    when (theme) {
                                        AppTheme.LIGHT -> stringResource(R.string.light)
                                        AppTheme.DARK -> stringResource(R.string.dark)
                                        AppTheme.AUTO -> stringResource(R.string.system_default)
                                    }
                                )
                            }
                        )
                    }
                }
            }

            // Language Selection
            Column {
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLanguage.values().forEach { language ->
                        FilterChip(
                            selected = state.language == language,
                            onClick = { viewModel.onLanguageChange(language) },
                            label = {
                                Text(
                                    when (language) {
                                        AppLanguage.ENGLISH -> stringResource(R.string.english)
                                        AppLanguage.ARABIC -> stringResource(R.string.arabic)
                                        AppLanguage.FRENCH -> stringResource(R.string.french)
                                    }
                                )
                            }
                        )
                    }
                }
            }

            // Unit Selection
            Column {
                Text(
                    text = stringResource(R.string.units),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = state.isMetric,
                        onClick = { viewModel.onUnitChange(true) },
                        label = { Text(stringResource(R.string.metric)) }
                    )
                    FilterChip(
                        selected = !state.isMetric,
                        onClick = { viewModel.onUnitChange(false) },
                        label = { Text(stringResource(R.string.imperial)) }
                    )
                }
            }
        }
    }
}
