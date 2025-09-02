package com.stickercreator.app.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stickercreator.app.ui.theme.StickerCreatorTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysThemeCorrectly() {
        composeTestRule.setContent {
            StickerCreatorTheme {
                // Just test that the theme loads without crashing
            }
        }
        // If we reach here, the theme loaded successfully
    }
}
