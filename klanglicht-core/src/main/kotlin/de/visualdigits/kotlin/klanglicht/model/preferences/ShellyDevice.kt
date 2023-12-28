package de.visualdigits.kotlin.klanglicht.model.preferences


data class ShellyDevice(
    val name: String = "",
    val model: String = "",
    val command: String = "",
    val ipAddress: String = "",
    val gain: Float = 0.0f
)
