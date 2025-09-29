package de.visualdigits.klanglicht.model.preferences

import de.visualdigits.klanglicht.hardware.shelly.model.ShellyDevice
import de.visualdigits.klanglicht.hardware.twinkly.model.TwinklyConfiguration
import de.visualdigits.klanglicht.model.dmx.model.Dmx
import de.visualdigits.klanglicht.model.hybrid.HybridDevice
import de.visualdigits.klanglicht.model.hybrid.HybridDeviceType
import de.visualdigits.kotlin.twinkly.model.device.xled.XLed
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedArray
import de.visualdigits.kotlin.twinkly.model.device.xmusic.XMusic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Devices(
    val dmx: Dmx? = null,
    val shelly: List<ShellyDevice> = listOf(),
    val twinkly: List<TwinklyConfiguration> = listOf(),
    val stage: List<HybridDevice> = listOf(),
    val colorWheels: List<ColorWheel> = listOf()
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    val shellyMap: Map<String, ShellyDevice> = shelly.associateBy { it.name }

    val twinklyMap: Map<String, TwinklyConfiguration> = twinkly.associate { tc -> Pair(tc.name?:error("No device name"), tc) }
    var xMusicDevice: XMusic? = twinkly.find { t -> t.xMusicDevice != null }?.xMusicDevice?.let { xm -> xm.ipAddress?.let { ip -> XMusic.instance(ip) } }
    var xledArrays: Map<String, XLedArray> = mapOf()
    var xledDevices: Map<String, XLed> = mapOf()
    var stageMap: Map<String, HybridDevice> = mapOf()

    val colorWheelMap: Map<String, ColorWheel> = colorWheels.associate { cw -> Pair(cw.id, cw) }

    var discoveredDevices: List<String> = listOf()

    init {
        refreshTwinklyDevices()
    }

    fun refreshTwinklyDevices(timeoutMillis: Int = 2000) {
        discoveredDevices = XLed.discoverTwinklyDevices(timeoutMillis)
        log.info("Discovered twinkly devices: $discoveredDevices")
        if (discoveredDevices.isNotEmpty()) {
            twinkly.forEach { td -> td.initialize(discoveredDevices) }
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
}
