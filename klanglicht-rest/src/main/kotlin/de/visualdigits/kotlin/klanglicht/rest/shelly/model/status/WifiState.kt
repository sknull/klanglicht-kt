package de.visualdigits.kotlin.klanglicht.rest.shelly.model.status


class WifiState(
    val connected: Boolean? = null,
    val ssid: String? = null,
    val ip: String? = null,
    val rssi: Int? = null
)
