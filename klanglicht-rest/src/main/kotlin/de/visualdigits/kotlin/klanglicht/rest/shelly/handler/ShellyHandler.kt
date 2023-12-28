package de.visualdigits.kotlin.klanglicht.rest.shelly.handler

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.preferences.ColorState
import de.visualdigits.kotlin.klanglicht.model.preferences.ShellyDevice
import de.visualdigits.kotlin.klanglicht.rest.common.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.feign.LightmanagerClient
import de.visualdigits.kotlin.klanglicht.rest.shelly.client.ShellyClient
import de.visualdigits.kotlin.klanglicht.rest.shelly.model.status.Light
import de.visualdigits.kotlin.klanglicht.rest.shelly.model.status.Status
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Arrays

@Component
class ShellyHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    var client: LightmanagerClient? = null

    @Autowired
    val configHolder: ConfigHolder? = null

    /**
     * Sets the given scene or index on the connected lightmanager air.
     *
     * @param sceneId
     * @param index
     */
    fun control(
        sceneId: Int,
        index: Int
    ) {
        if (sceneId > 0) {
            log.info("control sceneId=$sceneId")
            client?.controlScene(sceneId)
        }
        else if (index > 0) {
            log.info("control index=$index")
            client?.controlIndex(index)
        }
        else {
            throw IllegalStateException("Either parameter scene or index must be set")
        }
    }

    /**
     * Set hex colors.
     *
     * @param ids The list of ids.
     * @param hexColors The list of hex colors.
     * @param gains The list of gains (taken from stage setup if omitted).
     * @param transitionDuration The fade duration in milli seconds.
     * @param turnOn Determines if the device should be turned on.
     */
    fun hexColors(
        ids: String,
        hexColors: String,
        gains: String,
        transitionDuration: Long,
        turnOn: Boolean,
        store: Boolean = true
    ) {
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        val lHexColors = hexColors
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        val lGains = gains
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim().toFloat() }

        hexColors(lIds, lHexColors, lGains, transitionDuration, turnOn, store)
    }

    /**
     * Set hex colors.
     *
     * @param ids The list of ids.
     * @param hexColors The list of hex colors.
     * @param gains The list of gains (taken from stage setup if omitted).
     * @param transitionDuration The fade duration in milli seconds.
     * @param turnOn Determines if the device should be turned on.
     */
    fun hexColors(
        ids: List<String>,
        hexColors: List<String>,
        gains: List<Float>,
        transitionDuration: Long,
        turnOn: Boolean,
        store: Boolean = true
    ) {
        val nd = ids.size - 1
        var d = 0
        val nh = hexColors.size - 1
        var h = 0
        val ng = gains.size - 1
        var g = 0
        val overrideGains = gains.isNotEmpty()
        for (id in ids) {
            var gain = configHolder!!.getShellyGain(id)
            if (overrideGains) {
                gain = gains[g]
            }
            var hexColor = hexColors[h]
            if (!hexColor.startsWith("#")) {
                hexColor = "#$hexColor"
            }
            val rgbColor = RGBColor(hexColor)
            setColor(
                id,
                rgbColor,
                gain,
                transitionDuration,
                turnOn,
                store
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

    fun color(
        ids: String,
        red: Int,
        green: Int,
        blue: Int,
        gains: String,
        transitionDuration: Long,
        turnOn: Boolean,
        store: Boolean
    ) {
        val lGains = gains
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim().toFloat() }
        val g = 0
        val overrideGains = StringUtils.isNotEmpty(gains)
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        lIds.forEach { id ->
            val sid = id.trim()
            var gain = configHolder!!.getShellyGain(sid)
            if (overrideGains) {
                gain = lGains[g]
            }
            setColor(
                sid,
                RGBColor(red, green, blue),
                gain,
                transitionDuration,
                turnOn,
                store
            )
        }
    }

    fun restoreColors(
        ids: String,
        transitionDuration: Long
    ) {
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        lIds.forEach { id ->
            val lastColor = configHolder!!.getLastColor(id)
            setColor(
                id = id,
                rgbColor = RGBColor(lastColor.hexColor!!),
                gain = lastColor.gain?:1.0f,
                transitionDuration = transitionDuration,
                turnOn = lastColor.on,
                store = false
            )
        }
    }

    fun power(
        ids: String,
        turnOn: Boolean,
        transitionDuration: Long
    ) {
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        lIds.forEach { id ->
        val sid = id.trim()
            val shellyDevice = configHolder!!.shellyDevices[sid]
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val command: String = shellyDevice.command
                val lastColor = configHolder.getLastColor(sid)
                lastColor.on = turnOn
                configHolder.putColor(sid, lastColor) // update state
                log.info("power: $ipAddress")
                try {
                    ShellyClient.power(
                        ipAddress = ipAddress,
                        command = command,
                        turnOn = turnOn,
                        transitionDuration = transitionDuration
                    )
                } catch (e: Exception) {
                    log.warn("Could not set power for shelly devica at '$ipAddress'")
                }
            }
        }
    }

    fun gain(
        ids: String,
        gain: Int,
        transitionDuration: Long
    ) {
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        lIds.forEach { id ->
            val sid = id.trim()
            val shellyDevice = configHolder!!.shellyDevices[sid]
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val lastColor = configHolder.getLastColor(sid)
                lastColor.gain = gain.toFloat()
                configHolder.putColor(sid, lastColor) // update state
                try {
                    ShellyClient.gain(ipAddress = ipAddress, gain = gain, transitionDuration = transitionDuration)
                } catch (e: Exception) {
                    log.warn("Could not get gain for shelly at '$ipAddress'")
                }
            }
        }
    }

    fun currentPowers(): Map<String, Status> {
        val powers: MutableMap<String, Status> = LinkedHashMap()
        status().forEach { (device: ShellyDevice, status: Status) ->
            powers[device.name] = status
        }
        return powers
    }

    fun status(): Map<ShellyDevice, Status> {
        val statusMap: MutableMap<ShellyDevice, Status> = LinkedHashMap<ShellyDevice, Status>()
        configHolder!!.preferences?.shelly?.forEach { device ->
            val ipAddress: String = device.ipAddress
            val url = "http://$ipAddress/status"
            log.info("get status: $url")
            var status: Status
            try {
                status = ShellyClient.status(ipAddress)
            } catch (e: Exception) {
                log.warn("Could not get ststus for url '$url'")
                status = Status()
                status.mode = "offline"
            }
            statusMap[device] = status
        }
        return statusMap
    }

    private fun setColor(
        id: String,
        rgbColor: RGBColor,
        gain: Float,
        transitionDuration: Long,
        turnOn: Boolean,
        store: Boolean
    ): Light? {
        if (store) {
            val hexColor: String = rgbColor.web()
            log.info("Put color '$hexColor' for id '$id'")
            configHolder!!.putColor(
                id,
                ColorState(hexColor = hexColor, gain = gain, on = turnOn)
            )
        }
        log.info(" shelly with id=$id to color ${rgbColor.ansiColor()} gain=$gain, transition=$transitionDuration, turnOn=$turnOn, store=$store")

        val sid = id.trim()
        val shellyDevice = configHolder!!.shellyDevices[sid]
        return if (shellyDevice != null) {
            val ipAddress: String = shellyDevice.ipAddress
            log.info("setColor: $ipAddress")
            try {
                ShellyClient.color(
                    ipAddress = ipAddress,
                    rgbColor = rgbColor,
                    gain = gain,
                    transitionDuration = transitionDuration,
                    turnOn = turnOn
                )
            } catch (e: Exception) {
                log.warn("Could not set color for shelly at '$ipAddress'")
                null
            }
        } else null
    }
}
