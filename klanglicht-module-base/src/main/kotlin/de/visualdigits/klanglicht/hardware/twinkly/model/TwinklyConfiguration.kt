package de.visualdigits.klanglicht.hardware.twinkly.model

import de.visualdigits.kotlin.twinkly.model.device.xled.DeviceOrigin
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedDevice
import de.visualdigits.kotlin.twinkly.model.device.xled.XledArray
import de.visualdigits.kotlin.twinkly.model.device.xled.XledMatrixDevice
import de.visualdigits.kotlin.twinkly.model.device.xmusic.XMusic

class TwinklyConfiguration(
    val type: TwinklyDeviceType? = null,
    val name: String? = null,
    val deviceOrigin: String? = null,
    val gain: Double? = null,
    val array: Array<Array<XledDeviceConfiguration>> = arrayOf()
) {

    var xledArray: XledArray = XledArray()
    val xledDeviceMap: MutableMap<String, XLedDevice> = mutableMapOf()

    fun initialize(discoveredDevices: List<String>) {
        xledArray = XledArray(
            deviceOrigin = deviceOrigin?.let { d -> DeviceOrigin.valueOf(d) }?:error("No device origin"),
            xLedDevices = array.map { column ->
                column
                    .filter { config ->
                        discoveredDevices.contains(config.ipAddress) || type == TwinklyDeviceType.xmusic
                    }
                    .map { config ->
                        val device = when (type) {
                            TwinklyDeviceType.xled -> XLedDevice(
                                ipAddress = config.ipAddress ?: error("No ip address"),
                                width = config.width ?: error("No width"),
                                height = config.height ?: error("No height")
                            )

                            TwinklyDeviceType.xmusic -> XMusic(
                                ipAddress = config.ipAddress ?: error("No ip address"),
                            )

                            TwinklyDeviceType.xledmatrix -> XledMatrixDevice(
                                ipAddress = config.ipAddress ?: error("No ip address"),
                                width = config.width ?: error("No width"),
                                height = config.height ?: error("No height")
                            )

                            else -> error("Unsupported type")
                        }
                        xledDeviceMap[config.name ?: error("No name")] = device
                        device
                    }.toTypedArray()
            }.toTypedArray()
        )
    }
}
