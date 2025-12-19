package de.visualdigits.klanglicht.configuration.model

import de.visualdigits.klanglicht.hardware.dmx.model.Dmx
import de.visualdigits.klanglicht.hardware.hybrid.model.HybridDevice
import de.visualdigits.klanglicht.hardware.hybrid.model.HybridDeviceType
import de.visualdigits.klanglicht.hardware.shelly.model.ShellyDevice
import de.visualdigits.klanglicht.hardware.shelly.service.ShellyService
import de.visualdigits.klanglicht.hardware.twinkly.model.TwinklyDeviceConfig
import de.visualdigits.klanglicht.hardware.twinkly.model.TwinklyDeviceType
import de.visualdigits.kotlin.twinkly.model.device.xled.XLed
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedArray
import de.visualdigits.kotlin.twinkly.model.device.xmusic.XMusic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Devices(
    val dmx: Dmx? = null,
    val shelly: List<ShellyDevice> = listOf(),
    val twinkly: List<TwinklyDeviceConfig> = listOf(),
    val stage: List<HybridDevice> = listOf(),
    val colorWheels: List<ColorWheel> = listOf()
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    val shellyMap: Map<String, ShellyDevice> = shelly.associateBy { it.name }

    val twinklyMap: Map<String, TwinklyDeviceConfig> = twinkly.associateBy { tc -> tc.name?:error("No device name") }
    var xMusicDevice: XMusic? = null
    var xledArrays: Map<String, XLedArray> = mapOf()
    var xledDevices: Map<String, XLed> = mapOf()
    var stageMap: Map<String, HybridDevice> = mapOf()

    val colorWheelMap: Map<String, ColorWheel> = colorWheels.associateBy { cw -> cw.id }

    var discoveredDevices: List<String> = listOf()

    init {
        log.info("Initialize stage devices")
        stageMap = stage
            .filter { it.type != HybridDeviceType.twinkly || twinklyMap[it.id]?.xledArray?.isLoggedIn() == true }
            .associateBy { it.id }
        twinkly.forEach { td -> td.triggerDeviceName?.also { tdn -> td.triggerDeviceIpAddress = shellyMap[tdn]?.ipAddress } }
    }

    fun refreshTwinklyDevices(shellyService: ShellyService) {
        val onlineTriggerDevices = twinkly
            .mapNotNull { t -> t.triggerDeviceIpAddress }
            .toSet()
            .filter { tdip -> shellyService.isOn(tdip) }
        val discoveredDevices = twinkly
            .filter { td -> onlineTriggerDevices.contains(td.triggerDeviceIpAddress) }
            .flatMap { td -> td.array.flatten().mapNotNull { tdn -> tdn.ipAddress } }
            .toMutableList()
        xMusicDevice = discoveredDevices.find { tid -> twinklyMap[tid]?.type == TwinklyDeviceType.xmusic }?.let { tid ->
            twinklyMap[tid]?.ipAddress?.let { ip ->
                XMusic.instance(
                    ipAddress = ip
                )
            }
        }
        xMusicDevice?.also { xmd -> discoveredDevices.remove(xmd.getIpAddress()) }
        this.discoveredDevices = discoveredDevices

        log.info("Discovered twinkly devices: $discoveredDevices")
        if (discoveredDevices.isNotEmpty()) {
            twinkly.forEach { td -> td.initialize(discoveredDevices) }
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
