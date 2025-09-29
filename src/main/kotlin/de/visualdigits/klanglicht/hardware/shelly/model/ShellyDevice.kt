package de.visualdigits.klanglicht.hardware.shelly.model


class ShellyDevice(
    val name: String = "",
    val model: String = "",
    val command: String = "",
    val ipAddress: String = "",
    val gain: Double = 0.0
) {

    override fun toString(): String {
        return "Shelly $name [$ipAddress] $model"
    }
}
