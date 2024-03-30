package de.visualdigits.kotlin.klanglicht.hardware.shelly.model.status


class WifiState(
    val connected: Boolean? = null,
    val ssid: String? = null,
    val ip: String? = null,
    val rssi: Int? = null
)
