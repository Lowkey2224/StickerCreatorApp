package com.stickercreator.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stickercreator.app.ui.components.ErrorHandlerProvider
import com.stickercreator.app.ui.components.ErrorSeverity
import com.stickercreator.app.ui.screens.crop.CropScreen
import com.stickercreator.app.ui.screens.debug.DeveloperSettingsScreen
import com.stickercreator.app.ui.screens.home.HomeScreen
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun StickerCreatorNavigation(
    navController: NavHostController = rememberNavController()
) {
    ErrorHandlerProvider { errorHandler ->
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(
                    onImageSelected = { imageUri ->
                        try {
                            val encodedUri = URLEncoder.encode(imageUri, StandardCharsets.UTF_8.toString())
                            navController.navigate("crop/$encodedUri")
                        } catch (e: Exception) {
                            errorHandler.showErrorPopup(
                                message = "Navigation failed",
                                details = "Could not navigate to crop screen with URI: $imageUri",
                                severity = ErrorSeverity.ERROR,
                                exception = e
                            )
                        }
                    },
                    onNavigateToDevSettings = {
                        navController.navigate("dev-settings")
                    }
                )
            }

            composable("crop/{imageUri}") { backStackEntry ->
                val encodedImageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
                val imageUri = try {
                    URLDecoder.decode(encodedImageUri, StandardCharsets.UTF_8.toString())
                } catch (e: Exception) {
                    android.util.Log.w("Navigation", "Failed to decode URI: ${e.message}")
                    errorHandler.showErrorPopup(
                        message = "Failed to decode image URI",
                        details = "The image URI from navigation could not be decoded: $encodedImageUri",
                        severity = ErrorSeverity.ERROR,
                        exception = e
                    )
                    ""
                }
                CropScreen(
                    imageUri = imageUri,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            
            composable("dev-settings") {
                DeveloperSettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
