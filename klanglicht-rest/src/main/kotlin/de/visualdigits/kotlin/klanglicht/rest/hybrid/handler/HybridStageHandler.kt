package de.visualdigits.kotlin.klanglicht.rest.hybrid.handler

import de.visualdigits.kotlin.klanglicht.rest.KlanglichtHandler
import de.visualdigits.kotlin.klanglicht.rest.common.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.hybrid.model.Device
import de.visualdigits.kotlin.klanglicht.rest.hybrid.model.DeviceType
import de.visualdigits.kotlin.klanglicht.rest.shelly.handler.ShellyHandler
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Arrays

@Component
class HybridStageHandler {
    
//    @Autowired
//    val klanglichtHandler: KlanglichtHandler? = null

    @Autowired
    var shellyHandler: ShellyHandler? = null

    @Autowired
    val configHolder: ConfigHolder? = null

    /**
     * Set hex colors.
     *
     * @param ids The comma separated list of ids.
     * @param hexColors The comma separated list of hex colors.
     * @param gains The comma separated list of gains (taken from stage setup if omitted).
     * @param transition The fade duration in milli seconds.
     * @param turnOn Determines if the device should be turned on.
     */
    fun hexColor(
        ids: String,
        hexColors: String,
        gains: String,
        transition: Int,
        turnOn: Boolean
    ) {
        val hybridStage = configHolder?.hybridStage
        val lIds = Arrays.asList(*ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val nd = lIds.size - 1
        var d = 0
        val hasIds = StringUtils.isNotEmpty(ids)
        val lHexColors = Arrays.asList(*hexColors.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray())
        val nh = lHexColors.size - 1
        var h = 0
        val lGains = Arrays.asList(*gains.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val ng = lGains.size - 1
        var g = 0
        val overrideGains = StringUtils.isNotEmpty(gains)
        val dmxIds: MutableList<String> = ArrayList()
        val dmxHexColors: MutableList<String> = ArrayList()
        val dmxGains: MutableList<String> = ArrayList()
        val shellyIds: MutableList<String> = ArrayList()
        val shellyHexColors: MutableList<String> = ArrayList()
        val shellyGains: MutableList<String> = ArrayList()
        if (hasIds) {
            for (id in lIds) {
                val sid = id.trim { it <= ' ' }
                val hexColor = lHexColors[h]
                val gain = lGains[g]
                val device = hybridStage?.getDeviceById(sid)
                if (device != null) {
                    processDevice(
                        overrideGains,
                        dmxIds,
                        dmxHexColors,
                        dmxGains,
                        shellyIds,
                        shellyHexColors,
                        shellyGains,
                        sid,
                        hexColor,
                        gain,
                        device
                    )
                }
                if (++d >= nd) {
                    d = nd
                }
                if (++h >= nh) {
                    h = nh
                }
                if (++g >= ng) {
                    g = ng
                }
            }
        }
        else {
            hybridStage?.devices?.forEach { device ->
                val sid = device.id?.trim()?:""
                val hexColor = lHexColors[h]
                val gain = lGains[g]
                processDevice(
                    overrideGains,
                    dmxIds,
                    dmxHexColors,
                    dmxGains,
                    shellyIds,
                    shellyHexColors,
                    shellyGains,
                    sid,
                    hexColor,
                    gain,
                    device
                )
                if (++d >= nd) {
                    d = nd
                }
                if (++h >= nh) {
                    h = nh
                }
                if (++g >= ng) {
                    g = ng
                }
            }
        }
//        klanglichtHandler?.hexColors(dmxIds, dmxHexColors, dmxGains, transition)
        shellyHandler?.hexColors(shellyIds, shellyHexColors, shellyGains, transition, turnOn)
    }

    private fun processDevice(
        overrideGains: Boolean,
        dmxIds: MutableList<String>,
        dmxHexColors: MutableList<String>,
        dmxGains: MutableList<String>,
        shellyIds: MutableList<String>,
        shellyHexColors: MutableList<String>,
        shellyGains: MutableList<String>,
        sid: String,
        hexColor: String,
        gain: String,
        device: Device
    ) {
        when (device.type) {
            DeviceType.dmx -> {
                dmxIds.add(sid)
                dmxHexColors.add(hexColor)
                var dmxGain = configHolder?.getDmxGain(sid)
                if (overrideGains) {
                    dmxGain = gain.toFloat()
                }
                dmxGains.add(dmxGain.toString())
            }

            DeviceType.shelly -> {
                shellyIds.add(sid)
                shellyHexColors.add(hexColor)
                var shellyGain = configHolder?.getShellyGain(sid)
                if (overrideGains) {
                    shellyGain = (gain.toFloat() * 100.0).toInt()
                }
                shellyGains.add(shellyGain.toString())
            }

            else -> {}
        }
    }
}
