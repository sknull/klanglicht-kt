package de.visualdigits.kotlin.klanglicht.rest.hybrid.handler

import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridDevice
import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridDeviceType
import de.visualdigits.kotlin.klanglicht.rest.dmx.handler.DmxHandler
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.shelly.handler.ShellyHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HybridStageHandler {
    
    @Autowired
    val dmxHandler: DmxHandler? = null

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
     * @param transitionDuration The fade duration in milli seconds.
     * @param turnOn Determines if the device should be turned on.
     */
    fun hexColor(
        ids: String,
        hexColors: String,
        gains: String,
        transitionDuration: Long,
        turnOn: Boolean
    ) {
        val lIds = ids
            .split(",")
            .filter { it.isNotEmpty() }
            .map { it.trim() }
        val nd = lIds.size - 1
        var d = 0

        val lHexColors = hexColors
            .split(",")
            .filter { it.isNotEmpty() }
        val nh = lHexColors.size - 1
        var h = 0

        val lGains = gains
            .split(",")
            .filter { it.isNotEmpty() }
            .map { it.toFloat() }
        val ng = lGains.size - 1
        var g = 0

        val dmxIds: MutableList<String> = ArrayList()
        val dmxHexColors: MutableList<String> = ArrayList()
        val dmxGains: MutableList<Float> = ArrayList()
        val shellyIds: MutableList<String> = ArrayList()
        val shellyHexColors: MutableList<String> = ArrayList()
        val shellyGains: MutableList<Float> = ArrayList()
        if (lIds.isNotEmpty()) {
            for (id in lIds) {
                val hexColor = lHexColors[h]
                val gain = lGains[g]
                val device = configHolder!!.preferences?.stageMap?.get(id)
                if (device != null) {
                    processDevice(
                        lGains.isNotEmpty(),
                        dmxIds,
                        dmxHexColors,
                        dmxGains,
                        shellyIds,
                        shellyHexColors,
                        shellyGains,
                        id,
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
        } else {
            configHolder!!.preferences?.stage?.forEach { device ->
                val sid = device.id.trim() ?: ""
                val hexColor = lHexColors[h]
                val gain = lGains[g]
                processDevice(
                    lGains.isNotEmpty(),
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
        dmxHandler?.hexColors(
            baseChannels = dmxIds,
            hexColors = dmxHexColors,
            gains = dmxGains,
            transitionDuration = transitionDuration,
            store = true
        )
        shellyHandler?.hexColors(
            ids = shellyIds,
            hexColors = shellyHexColors,
            gains = shellyGains,
            transitionDuration = transitionDuration,
            turnOn = turnOn
        )
    }

    private fun processDevice(
        overrideGains: Boolean,
        dmxIds: MutableList<String>,
        dmxHexColors: MutableList<String>,
        dmxGains: MutableList<Float>,
        shellyIds: MutableList<String>,
        shellyHexColors: MutableList<String>,
        shellyGains: MutableList<Float>,
        sid: String,
        hexColor: String,
        gain: Float,
        hybridDevice: HybridDevice
    ) {
        when (hybridDevice.type) {
            HybridDeviceType.dmx -> {
                dmxIds.add(sid)
                dmxHexColors.add(hexColor)
                var dmxGain = configHolder!!.getDmxGain(sid)
                if (overrideGains) {
                    dmxGain = gain
                }
                dmxGains.add(dmxGain)
            }

            HybridDeviceType.shelly -> {
                shellyIds.add(sid)
                shellyHexColors.add(hexColor)
                var shellyGain = configHolder!!.getShellyGain(sid)
                if (overrideGains) {
                    shellyGain = gain
                }
                shellyGains.add(shellyGain)
            }

            else -> {}
        }
    }
}
