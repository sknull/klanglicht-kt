package de.visualdigits.klanglicht.hardware.twinkly.model

import de.visualdigits.kotlin.twinkly.model.device.xled.DeviceOrigin
import de.visualdigits.kotlin.twinkly.model.device.xled.XLed
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedArray
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedDevice
import de.visualdigits.kotlin.twinkly.model.device.xled.XledMatrixDevice
import org.slf4j.LoggerFactory

class TwinklyDeviceConfig(
    val type: TwinklyDeviceType? = null,
    val name: String? = null,
    val ipAddress: String? = null,
    val deviceOrigin: String? = null,
    val gain: Double? = null,
    val triggerDeviceName: String? = null,
    val array: List<List<TwinklyDevice>> = listOf()
) {

    private val log = LoggerFactory.getLogger(javaClass)

    var xledArray: XLedArray = XLedArray.instance()
    val xledDeviceMap: MutableMap<String, XLed> = mutableMapOf()
    var triggerDeviceIpAddress: String? = null

    fun initialize(discoveredDevices: List<String>) {
        val xLedDevices = mutableListOf<MutableList<XLed>>()
        array.forEach { column ->
            val filter = column.filter { config -> discoveredDevices.contains(config.ipAddress) }
            val xledColumn = mutableListOf<XLed>()
            filter
                .forEach { twinklyDeviceNode ->
                    try {
                        val device = when (type) {
                            TwinklyDeviceType.xled -> XLedDevice.instance(
                                ipAddress = twinklyDeviceNode.ipAddress ?: error("No ip address"),
                                width = twinklyDeviceNode.width ?: error("No width"),
                                height = twinklyDeviceNode.height ?: error("No height")
                            )

                            TwinklyDeviceType.xledmatrix -> XledMatrixDevice.instance(
                                ipAddress = twinklyDeviceNode.ipAddress ?: error("No ip address"),
                                name = twinklyDeviceNode.name ?: error("No device name"),
                                width = twinklyDeviceNode.width ?: error("No width"),
                                height = twinklyDeviceNode.height ?: error("No height")
                            )

                            else -> error("Unsupported type")
                        }
                        xledColumn.add(device)
                        xledDeviceMap[twinklyDeviceNode.name ?: error("No name")] = device
                    } catch (e: Exception) {
                        log.warn("Could not initialize twinkly device '${twinklyDeviceNode.ipAddress}'")
                    }
                }
            xLedDevices.add(xledColumn)
        }
        xledArray = XLedArray.instance(
            xLedDevices = xLedDevices,
            deviceOrigin = deviceOrigin?.let { d -> DeviceOrigin.valueOf(d) }?: DeviceOrigin.TOP_LEFT
        )
    }
}
