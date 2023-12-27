package de.visualdigits.kotlin.klanglicht.rest.shelly.handler

import de.visualdigits.kotlin.klanglicht.rest.common.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.preferences.ColorState
import de.visualdigits.kotlin.klanglicht.model.preferences.ShellyDevice
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.feign.LightmanagerClient
import de.visualdigits.kotlin.klanglicht.rest.shelly.model.status.Light
import de.visualdigits.kotlin.klanglicht.rest.shelly.model.status.Status
import feign.Request
import feign.okhttp.OkHttpClient
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.Objects
import java.util.function.BiConsumer

@Component
class ShellyHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    val httpClient: OkHttpClient = OkHttpClient()

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
     * @param lIds The list of ids.
     * @param lHexColors The list of hex colors.
     * @param lGains The list of gains (taken from stage setup if omitted).
     * @param transition The fade duration in milli seconds.
     * @param turnOn Determines if the device should be turned on.
     * @param store Determines if the new value should be stored in the cache.
     */
    /**
     * Set hex colors.
     *
     * @param lIds The list of ids.
     * @param lHexColors The list of hex colors.
     * @param lGains The list of gains (taken from stage setup if omitted).
     * @param transition The fade duration in milli seconds.
     * @param turnOn Determines if the device should be turned on.
     */
    @JvmOverloads
    fun hexColors(
        lIds: List<String>,
        lHexColors: List<String>,
        lGains: List<String>,
        transition: Int,
        turnOn: Boolean,
        store: Boolean =
            true
    ) {
        val nd = lIds.size - 1
        var d = 0
        val nh = lHexColors.size - 1
        var h = 0
        val ng = lGains.size - 1
        var g = 0
        val overrideGains = !lGains.isEmpty()
        for (id in lIds) {
            val sid = id.trim { it <= ' ' }
            var gain = configHolder?.getShellyGain(sid)?:1
            if (overrideGains) {
                gain = lGains[g].toInt()
            }
            var hexColor = lHexColors[h]
            if (!hexColor.startsWith("#")) {
                hexColor = "#$hexColor"
            }
            val rgbColor = RGBColor(hexColor)
            setColor(
                sid,
                rgbColor,
                gain,
                transition,
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
        transition: Int,
        turnOn: Boolean,
        store: Boolean
    ) {
        val lGains = Arrays.asList(*gains.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val ng = lGains.size - 1
        val g = 0
        val overrideGains = StringUtils.isNotEmpty(gains)
        for (id in ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val sid = id.trim { it <= ' ' }
            var gain = configHolder?.getShellyGain(sid)?:1
            if (overrideGains) {
                gain = lGains[g].toInt()
            }
            setColor(
                sid,
                RGBColor(red, green, blue),
                gain,
                transition,
                turnOn,
                store
            )
        }
    }

    fun restoreColors(
        ids: String,
        transition: Int
    ) {
        for (id in ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val sid = id.trim { it <= ' ' }
            val lastColor = configHolder?.getLastColor(sid)
            setColor(
                sid,
                RGBColor(lastColor?.hexColor!!),
                lastColor.gain?.toInt()?:1,
                transition,
                lastColor.on!!,
                false
            )
        }
    }

    fun power(
        ids: String,
        turnOn: Boolean,
        transition: Int
    ) {
        for (id in ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val sid = id.trim { it <= ' ' }
            val shellyDevice = configHolder?.shellyDevices?.get(sid)
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val command: String = shellyDevice.command
                val lastColor = configHolder?.getLastColor(sid)?: ColorState("#000000")
                lastColor.on = turnOn
                configHolder?.putColor(sid, lastColor) // update state
                val url =
                    "http://" + ipAddress + "/" + command + "?turn=" + (if (turnOn) "on" else "off") + "&transition=" + transition + "&"
                log.info("power: $url")
                try {
                    query(url)
                } catch (e: Exception) {
                    log.warn("Could not set power for url '$url'")
                }
            }
        }
    }

    fun gain(
        ids: String,
        gain: Int,
        transition: Int
    ) {
        for (id in ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val sid = id.trim { it <= ' ' }
            val ipAddress = configHolder?.getShellyIpAddress(sid)
            val lastColor = configHolder?.getLastColor(sid)?: ColorState("#000000")
            lastColor.gain = gain.toFloat()
            configHolder?.putColor(sid, lastColor) // update state
            val url = "http://$ipAddress/color/0?gain=$gain&transition=$transition&"
            log.info("gain: $url")
            try {
                query(url)
            } catch (e: Exception) {
                log.warn("Could not get gain for url '$url'")
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
        configHolder?.shellyDevices?.values?.forEach { device ->
            val ipAddress: String = device.ipAddress
            val url = "http://$ipAddress/status"
            log.info("get status: $url")
            var status: Status
            try {
                val json = query(url)
                status = Status.Companion.load(json)
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
        gain: Int,
        transition: Int,
        turnOn: Boolean,
        store: Boolean
    ): Light? {
        if (store) {
            val hexColor: String = rgbColor.web()
            log.info("Put color '$hexColor' for id '$id'")
            configHolder?.putColor(
                id,
                ColorState(hexColor = hexColor, gain = gain.toFloat(), on = turnOn)
            )
        }
        val red: Int = rgbColor.red
        val green: Int = rgbColor.green
        val blue: Int = rgbColor.blue
        log.info(" shelly with id=$id to color ${rgbColor.ansiColor()} gain=$gain, transition=$transition, turnOn=$turnOn, store=$store")
        val ipAddress = configHolder?.getShellyIpAddress(id)
        val url = "http://" + ipAddress + "/color/0?" +
                "turn=" + (if (turnOn) "on" else "off") + "&" +
                "red=" + red + "&" +
                "green=" + green + "&" +
                "blue=" + blue + "&" +
                "white=0&" +
                "gain=" + gain + "&" +
                "transition=" + transition + "&"
        log.info("setColor: $url")
        var light: Light? = null
        try {
            val json = query(url)
            light = Light.load(json)
        } catch (e: Exception) {
            log.warn("Could not set color for url '$url'")
        }
        return light
    }

    private fun query(url: String): String {
        val request = Request.create(Request.HttpMethod.GET, url, mapOf(), byteArrayOf(), StandardCharsets.UTF_8, null)
        val res = httpClient.execute(request, Request.Options()).use { response ->
            if (response.status() == 200) {
                response.body().asInputStream().use { ins -> String(ins.readAllBytes()) }
            } else {
                throw IOException("Unexpected code $response")
            }
        }
        return res
    }
}
