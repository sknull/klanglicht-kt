package de.visualdigits.klanglicht.hardware.twinkly.model

import de.visualdigits.kotlin.twinkly.model.device.xled.DeviceOrigin
import de.visualdigits.kotlin.twinkly.model.device.xled.XLed
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedArray
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedDevice
import de.visualdigits.kotlin.twinkly.model.device.xled.XledMatrixDevice

class TwinklyConfiguration(
    val type: TwinklyDeviceType? = null,
    val name: String? = null,
    val deviceOrigin: String? = null,
    val gain: Double? = null,
    val xMusicDevice: XledDeviceConfiguration? = null,
    val array: List<List<XledDeviceConfiguration>> = listOf()
) {

    var xledArray: XLedArray = XLedArray.instance()
    val xledDeviceMap: MutableMap<String, XLed> = mutableMapOf()

    fun initialize(discoveredDevices: List<String>) {
        val xLedDevices = mutableListOf<MutableList<XLed>>()
        array.forEach { column ->
            val filter = column.filter { config -> discoveredDevices.contains(config.ipAddress) }
            val xledColumn = mutableListOf<XLed>()
            filter
                .forEach { config ->
                    val device = when (type) {
                        TwinklyDeviceType.xled -> XLedDevice.instance(
                            ipAddress = config.ipAddress ?: error("No ip address"),
                            width = config.width ?: error("No width"),
                            height = config.height ?: error("No height")
                        )

                        TwinklyDeviceType.xledmatrix -> XledMatrixDevice.instance(
                            ipAddress = config.ipAddress ?: error("No ip address"),
                            width = config.width ?: error("No width"),
                            height = config.height ?: error("No height")
                        )

                        else -> error("Unsupported type")
                    }
                    xledColumn.add(device)
                    xledDeviceMap[config.name ?: error("No name")] = device
                    device
                }
            xLedDevices.add(xledColumn)
        }
        xledArray = XLedArray.instance(
            xLedDevices = xLedDevices,
            deviceOrigin = deviceOrigin?.let { d -> DeviceOrigin.valueOf(d) }?: DeviceOrigin.TOP_LEFT
        )
    }
}
