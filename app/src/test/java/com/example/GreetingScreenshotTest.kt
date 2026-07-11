package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TourPackage
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun tour_card_screenshot() {
    val samplePackage = TourPackage(
      id = "morning_boat",
      name = "Subah-e-Banaras Morning Tour",
      price = 500,
      duration = "3 Hours (5:00 AM - 8:00 AM)",
      description = "Experience the magical dawn in Varanasi with sunrise boat cruise and morning rituals on ghats.",
      highlights = listOf("Vedic Chanting", "Sunrise Boat Cruise"),
      iconName = "wb_sunny"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        TourPackageCard(pack = samplePackage, onBookClick = {})
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
