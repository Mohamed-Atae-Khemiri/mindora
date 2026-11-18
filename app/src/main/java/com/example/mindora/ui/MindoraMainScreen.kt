package com.example.mindora.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.mindora.ai.viewmodel.LocalAiViewModel
import com.example.mindora.ui.navigation.BottomNavigationBar
import com.example.mindora.ui.navigation.MindoraAppNavHost
import com.example.mindora.ui.navigation.MindoraNavigationRail
import com.example.mindora.ui.theme.AppBackgroundContainer
import com.example.mindora.ui.theme.BackgroundMode
import com.example.mindora.ui.theme.MindoraTheme
import com.example.mindora.ui.theme.getPatternCraftTheme
import com.example.mindora.viewmodel.HomeViewModel
import com.example.mindora.viewmodel.ProgressViewModel
import com.example.mindora.viewmodel.StudentProfileViewModel
import com.example.mindora.viewmodel.SubjectViewModel
import com.example.mindora.viewmodel.TaskViewModel

private const val PREFERENCES_NAME = "mindora_preferences"
private const val NIGHT_MODE_KEY = "night_mode_enabled"
const val BACKGROUND_MODE_KEY = "background_mode"
const val PRESET_THEME_ID_KEY = "preset_theme_id"
const val CUSTOM_PHOTO_URI_KEY = "custom_photo_uri"
const val BACKGROUND_DIM_KEY = "background_dim_level"

@Composable
fun MindoraMainScreen(
    homeViewModel: HomeViewModel,
    subjectViewModel: SubjectViewModel,
    taskViewModel: TaskViewModel,
    progressViewModel: ProgressViewModel,
    studentProfileViewModel: StudentProfileViewModel,
    localAiViewModel: LocalAiViewModel
) {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences(PREFERENCES_NAME, android.content.Context.MODE_PRIVATE)
    }
    var isDarkMode by rememberSaveable {
        mutableStateOf(preferences.getBoolean(NIGHT_MODE_KEY, false))
    }
    var backgroundMode by rememberSaveable {
        val savedModeStr = preferences.getString(BACKGROUND_MODE_KEY, BackgroundMode.PRESET.name)
        mutableStateOf(runCatching { BackgroundMode.valueOf(savedModeStr!!) }.getOrDefault(BackgroundMode.PRESET))
    }
    var selectedPresetId by rememberSaveable {
        mutableStateOf(preferences.getString(PRESET_THEME_ID_KEY, "classic_cream") ?: "classic_cream")
    }
    var customPhotoUri by rememberSaveable {
        mutableStateOf(preferences.getString(CUSTOM_PHOTO_URI_KEY, null))
    }
    var dimLevel by rememberSaveable {
        mutableStateOf(preferences.getFloat(BACKGROUND_DIM_KEY, 0.45f))
    }

    LaunchedEffect(isDarkMode) {
        preferences.edit().putBoolean(NIGHT_MODE_KEY, isDarkMode).apply()
    }
    LaunchedEffect(backgroundMode) {
        preferences.edit().putString(BACKGROUND_MODE_KEY, backgroundMode.name).apply()
    }
    LaunchedEffect(selectedPresetId) {
        preferences.edit().putString(PRESET_THEME_ID_KEY, selectedPresetId).apply()
    }
    LaunchedEffect(customPhotoUri) {
        preferences.edit().putString(CUSTOM_PHOTO_URI_KEY, customPhotoUri).apply()
    }
    LaunchedEffect(dimLevel) {
        preferences.edit().putFloat(BACKGROUND_DIM_KEY, dimLevel).apply()
    }

    val activePreset = remember(selectedPresetId) {
        getPatternCraftTheme(selectedPresetId)
    }
    val effectiveDarkTheme = isDarkMode || (backgroundMode == BackgroundMode.PRESET && activePreset.isDark)

    MindoraTheme(darkTheme = effectiveDarkTheme) {
        AppBackgroundContainer(
            mode = backgroundMode,
            presetId = selectedPresetId,
            customPhotoUri = customPhotoUri,
            dimLevel = dimLevel,
            isDarkMode = effectiveDarkTheme
        ) {
            val navController = rememberNavController()
            BoxWithConstraints(Modifier.fillMaxSize()) {
                if (maxWidth >= 840.dp) {
                    Row(Modifier.fillMaxSize()) {
                        MindoraNavigationRail(navController = navController)
                        Box(Modifier.weight(1f).fillMaxHeight()) {
                            MindoraAppNavHost(
                                navController = navController,
                                homeViewModel = homeViewModel,
                                subjectViewModel = subjectViewModel,
                                taskViewModel = taskViewModel,
                                progressViewModel = progressViewModel,
                                studentProfileViewModel = studentProfileViewModel,
                                localAiViewModel = localAiViewModel,
                                isDarkMode = isDarkMode,
                                onDarkModeChange = { isDarkMode = it },
                                backgroundMode = backgroundMode,
                                onBackgroundModeChange = { backgroundMode = it },
                                selectedPresetId = selectedPresetId,
                                onPresetIdChange = { selectedPresetId = it },
                                customPhotoUri = customPhotoUri,
                                onCustomPhotoUriChange = { customPhotoUri = it },
                                dimLevel = dimLevel,
                                onDimLevelChange = { dimLevel = it },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = { BottomNavigationBar(navController = navController) }
                    ) { paddingValues ->
                        MindoraAppNavHost(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            subjectViewModel = subjectViewModel,
                            taskViewModel = taskViewModel,
                            progressViewModel = progressViewModel,
                            studentProfileViewModel = studentProfileViewModel,
                            localAiViewModel = localAiViewModel,
                            isDarkMode = isDarkMode,
                            onDarkModeChange = { isDarkMode = it },
                            backgroundMode = backgroundMode,
                            onBackgroundModeChange = { backgroundMode = it },
                            selectedPresetId = selectedPresetId,
                            onPresetIdChange = { selectedPresetId = it },
                            customPhotoUri = customPhotoUri,
                            onCustomPhotoUriChange = { customPhotoUri = it },
                            dimLevel = dimLevel,
                            onDimLevelChange = { dimLevel = it },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}
