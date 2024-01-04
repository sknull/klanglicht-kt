package de.visualdigits.kotlin.klanglicht.model.hybrid

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.parameter.Fadeable
import de.visualdigits.kotlin.klanglicht.model.parameter.IntParameter
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.klanglicht.model.shelly.ShellyColor
import de.visualdigits.kotlin.klanglicht.model.twinkly.XledFrameFadeable
import de.visualdigits.kotlin.twinkly.model.playable.XledFrame
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.min
import kotlin.math.roundToInt
import de.visualdigits.kotlin.twinkly.model.color.RGBColor as TwinklyRGBColor
class HybridScene() : Fadeable<HybridScene> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private var ids: String = ""
    private var hexColors: String = ""
    private var gains: String = ""
    private var turnOns: String = ""
    private var preferences: Preferences? = null

    private val fadeables: MutableMap<String, Fadeable<*>> = mutableMapOf()

    constructor(
        ids: String,
        hexColors: String,
        gains: String,
        turnOns: String = "true",
        preferences: Preferences?
    ): this() {
        this.ids = ids
        this.hexColors = hexColors
        this.gains = gains
        this.turnOns = turnOns
        this.preferences = preferences

        initializeFromParameters()
        initializeFromFadeables() // modify attributes after updating fadeables
    }

    constructor(
        fadeables: MutableMap<String, Fadeable<*>>,
        preferences: Preferences?
    ): this() {
        this.fadeables.clear()
        this.preferences = preferences
        update(fadeables)
    }

    override fun toString(): String {
        return fadeables
            .map { "${it.key}: ${it.value.getRgbColor()?.ansiColor()} [${it.value.getGain()}]" }
            .joinToString("")
            .trim()
    }

    fun update(nextScene: HybridScene) {
        update(nextScene.fadeables)
    }

    fun update(fadeables: MutableMap<String, Fadeable<*>>) {
        this.fadeables.putAll(fadeables)
        initializeFromFadeables()
    }

    fun fadeables(): List<Fadeable<*>> = fadeables.values.toList()

    fun getFadeable(id: String): Fadeable<*>? = fadeables[id]

    fun putFadeable(id: String, fadeable: Fadeable<*>) {
        fadeables[id] = fadeable
        initializeFromFadeables()
    }

    private fun initializeFromFadeables() {
        this.ids = this.fadeables().map { sc -> sc.getId() }.joinToString(",")
        this.hexColors = this.fadeables().mapNotNull { sc -> sc.getRgbColor()?.hex() }.joinToString(",")
        this.gains = this.fadeables().map { sc -> sc.getGain() }.joinToString(",")
        this.turnOns = this.fadeables().mapNotNull { sc -> sc.getTurnOn() }.joinToString(",")
    }

    private fun initializeFromParameters() {
        val lIds = if (ids.isNotEmpty()) {
            ids
                .split(",")
                .filter { it.isNotEmpty() }
                .map { it.trim() }
        } else {
            preferences?.getStageIds()?:listOf()
        }

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

        val lTurnOns = turnOns
            .split(",")
            .filter { it.isNotEmpty() }
            .map { it.toBoolean() }
        val nt = lTurnOns.size - 1
        var t= 0

        val twinklyDevices = preferences?.getHybridDevices(HybridDeviceType.twinkly)
        if (twinklyDevices?.map { it.id }?.any { td -> lIds.any { td == it } } == true) {
            twinklyDevices
                .mapNotNull { preferences?.getTwinklyConfiguration(it.id) }
                .forEach { twinklyDevice ->
                    val xa = twinklyDevice.xledArray
                    if (xa.isLoggedIn()) {
                        val lc = RGBColor(lHexColors.last())
                        val frame = XledFrame(width = xa.width, height = xa.height, initialColor = TwinklyRGBColor(lc.red, lc.green, lc.blue))
                        val nc = lHexColors.size
                        val barWidth = xa.width / nc
                        for (x in 0 until nc - 1) {
                            val rgbColor = RGBColor(lHexColors[x])
                            val bar = XledFrame(width = barWidth, height = xa.height, initialColor = TwinklyRGBColor(rgbColor.red, rgbColor.green, rgbColor.blue))
                            frame.replaceSubFrame(bar, x * barWidth, 0)
                        }
                        val fadeable = XledFrameFadeable(
                            deviceId = twinklyDevice.name,
                            xledFrame = frame,
                            deviceGain = twinklyDevice.gain
                        )
                        fadeables[twinklyDevice.name] = fadeable
                    }
                }
        }

        lIds.forEach { id ->
            val device = preferences?.getHybridDevice(id)
            if (device != null) {
                val hexColor = lHexColors[min(nh, h++)]
                val gain = lGains.getOrNull(min(ng, g++))
                val turnOn = lTurnOns.getOrNull(min(nt, t++)) ?: false
                val rgbColor = RGBColor(hexColor)
                when (device.type) {
                    HybridDeviceType.dmx -> {
                        val dmxDevice = preferences?.getDmxDevice(id)
                        if (dmxDevice != null) {
                            val effectiveGain = gain ?: dmxDevice.gain
                            val paramGain = if (turnOn) (255 * effectiveGain).roundToInt() else 0
                            ParameterSet(
                                baseChannel = dmxDevice.baseChannel,
                                parameters = mutableListOf(
                                    IntParameter("MasterDimmer", paramGain),
                                    rgbColor
                                )
                            )
                        } else null
                    }

                    HybridDeviceType.shelly -> {
                        val shellyDevice = preferences?.getShellyDevice(id)
                        if (shellyDevice != null) {
                            val effectiveGain = gain ?: shellyDevice.gain
                            ShellyColor(
                                deviceId = shellyDevice.name,
                                ipAddress = shellyDevice.ipAddress,
                                color = rgbColor,
                                deviceGain = effectiveGain,
                                deviceTurnOn = turnOn
                            )
                        } else null
                    }

                    else -> null
                }
                    ?.let { dd -> fadeables[id] = dd }
            }
        }
    }

    fun setTurnOn(id: String, turnOn: Boolean) {
        fadeables[id]?.setTurnOn(turnOn)
        initializeFromFadeables()
    }

    fun getTurnOn(id: String): Boolean? = fadeables[id]?.getTurnOn()

    fun setGain(id: String, gain: Float) {
        fadeables[id]?.setGain(gain)
        initializeFromFadeables()
    }

    fun getGain(id: String): Float = fadeables[id]?.getGain()?:1.0f

    fun setRgbColor(id: String, rgbColor: RGBColor) {
        fadeables[id]?.setRgbColor(rgbColor)
        initializeFromFadeables()
    }

    fun getRgbColor(id: String): RGBColor? = fadeables[id]?.getRgbColor()

    override fun write(preferences: Preferences?, write: Boolean, transitionDuration: Long) {
        // first collect all frame data for the dmx frame to avoid lots of costly write operations to a serial interface
        val parameterSets = fadeables().filterIsInstance<ParameterSet>()
        if (parameterSets.isNotEmpty()) { // only write to dmx interface if needed
            parameterSets.forEach { parameterSet ->
                val baseChannel = parameterSet.baseChannel
                preferences?.let{
                    val bytes = parameterSet.toBytes(it)
                    preferences.setDmxData(baseChannel, bytes)
                    if (write) {
                        log.debug("Writing hybrid scene {}", this)
                        preferences.writeDmxData()
                    }
                }
            }
        }

        // call twinkly interface which is not so fast
        fadeables().filterIsInstance<XledFrameFadeable>().forEach { it.write(preferences, true) }

        // call shelly interface which is pretty fast
        fadeables().filterIsInstance<ShellyColor>().forEach { it.write(preferences, true) }
    }

    override fun fade(other: Any, factor: Double): HybridScene {
        return if (other is HybridScene) {
            val otherIds = other.fadeables().map { it.getId() } // ensure that we only fade elements which are in source and target scene
            val fadedScene = HybridScene(fadeables, preferences)
            fadeables()
                .filter { otherIds.contains(it.getId()) }
                .zip(other.fadeables())
                .map { Pair(it.first.fade(it.second, factor).getId(), it.first.fade(it.second, factor)) }
                .forEach { (id, fadeable) ->
                    fadedScene.putFadeable(id, fadeable)
                }

            // fixme - at this point parameterMap contains default values - why?

            fadedScene
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
