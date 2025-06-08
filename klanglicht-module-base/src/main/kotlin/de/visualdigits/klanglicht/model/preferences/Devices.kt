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

    val twinklyMap: Map<String, TwinklyConfiguration> = twinkly.associate { Pair(it.name, it) }

    val stageMap: Map<String, HybridDevice> =  stage
        .filter { it.type != HybridDeviceType.twinkly || twinklyMap[it.id]?.xledArray?.isLoggedIn() == true }
        .associateBy { it.id }

    val xledArrays: Map<String, XledArray> = twinkly.associate { config -> Pair(config.name, config.xledArray) }
    val xledDevices = twinkly.flatMap { tc -> tc.array.flatMap { a -> a.toList() }.toList() }.associate { tc -> Pair(tc.name,
        XLedDevice(tc.ipAddress, tc.width, tc.height)) }

    val colorWheelMap: Map<String, ColorWheel> = colorWheels.associate { cw -> Pair(cw.id, cw) }
}
