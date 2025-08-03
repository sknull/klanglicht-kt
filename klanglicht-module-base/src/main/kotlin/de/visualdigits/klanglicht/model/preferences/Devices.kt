package de.visualdigits.klanglicht.model.preferences

import de.visualdigits.klanglicht.hardware.shelly.model.ShellyDevice
import de.visualdigits.klanglicht.hardware.twinkly.model.TwinklyConfiguration
import de.visualdigits.klanglicht.model.dmx.model.Dmx
import de.visualdigits.klanglicht.model.hybrid.HybridDevice
import de.visualdigits.klanglicht.model.hybrid.HybridDeviceType
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedDevice
import de.visualdigits.kotlin.twinkly.model.device.xled.XledArray

class Devices(
    val dmx: Dmx? = null,
    val shelly: List<ShellyDevice> = listOf(),
    val twinkly: List<TwinklyConfiguration> = listOf(),
    val stage: List<HybridDevice> = listOf(),
    val colorWheels: List<ColorWheel> = listOf()
) {

    val shellyMap: Map<String, ShellyDevice> = shelly.associateBy { it.name }

    val twinklyMap: Map<String, TwinklyConfiguration> = twinkly.associate { tc -> Pair(tc.name?:error("No device name"), tc) }
    val xledArrays: Map<String, XledArray>
    val xledDevices: Map<String, XLedDevice>

    val stageMap: Map<String, HybridDevice>

    val colorWheelMap: Map<String, ColorWheel> = colorWheels.associate { cw -> Pair(cw.id, cw) }

    init {
        val discoveredDevices = XLedDevice.discoverTwinklyDevices()
        if (discoveredDevices.isNotEmpty()) {
            twinkly.forEach { td -> td.initialize(discoveredDevices) }
        }
        stageMap = stage
            .filter { it.type != HybridDeviceType.twinkly || twinklyMap[it.id]?.xledArray?.isLoggedIn() == true }
            .associateBy { it.id }
        xledArrays = twinklyMap
            .map { (name, config) ->
                Pair(name, config.xledArray)
            }.toMap()
        xledDevices = twinklyMap.values
            .flatMap { tc ->
                tc.xledDeviceMap.toList()
            }.toMap()
    }
}
