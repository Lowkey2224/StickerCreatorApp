package com.stickercreator.app.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stickercreator.app.ui.theme.StickerCreatorTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PermissionDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun permissionDialog_displaysCorrectContent() {
        composeTestRule.setContent {
            StickerCreatorTheme {
                PermissionDialog(
                    title = "Test Title",
                    message = "Test Message",
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Message").assertIsDisplayed()
        composeTestRule.onNodeWithText("Grant Permission").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun permissionDialog_callsCallbacksCorrectly() {
        var confirmCalled = false
        var dismissCalled = false

        composeTestRule.setContent {
            StickerCreatorTheme {
                PermissionDialog(
                    title = "Test Title",
                    message = "Test Message",
                    onConfirm = { confirmCalled = true },
                    onDismiss = { dismissCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Grant Permission").performClick()
        assertTrue(confirmCalled)

        composeTestRule.onNodeWithText("Cancel").performClick()
        assertTrue(dismissCalled)
    }
}
