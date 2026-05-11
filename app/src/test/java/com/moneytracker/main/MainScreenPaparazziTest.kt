package com.moneytracker.main

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class MainScreenPaparazziTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:style/Theme.Material.Light.NoActionBar"
    )

    @Test
    fun mainScreenLoadedPreview() {
        paparazzi.snapshot {
            MainScreenLoadedPreview()
        }
    }
}
