package com.stickercreator.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stickercreator.app.ui.screens.crop.CropScreen
import com.stickercreator.app.ui.screens.home.HomeScreen

@Composable
fun StickerCreatorNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onImageSelected = { imageUri ->
                    navController.navigate("crop/$imageUri")
                }
            )
        }
        
        composable("crop/{imageUri}") { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
            CropScreen(
                imageUri = imageUri,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
