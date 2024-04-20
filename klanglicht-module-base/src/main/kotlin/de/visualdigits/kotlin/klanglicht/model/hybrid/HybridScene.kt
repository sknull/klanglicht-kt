package de.visualdigits.kotlin.klanglicht.model.hybrid

import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.ShellyColor
import de.visualdigits.kotlin.klanglicht.hardware.twinkly.model.XledFrameFadeable
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.dmx.parameter.Fadeable
import de.visualdigits.kotlin.klanglicht.model.dmx.parameter.IntParameter
import de.visualdigits.kotlin.klanglicht.model.dmx.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.twinkly.model.playable.XledFrame
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import de.visualdigits.kotlin.twinkly.model.color.RGBColor as TwinklyRGBColor

class HybridScene() : Fadeable<HybridScene> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private var ids: String? = null
    private var hexColors: String? = null
    private var gains: String? = null
    private var turnOns: String? = null
    private var preferences: Preferences? = null

    private val fadeables: MutableMap<String, Fadeable<*>> = mutableMapOf()

    constructor(
        ids: String? = null,
        hexColors: String? = null,
        gains: String? = null,
        turnOns: String? = "true",
        preferences: Preferences? = null
    ) : this() {
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
    ) : this() {
        this.fadeables.clear()
        this.preferences = preferences
        update(fadeables)
    }

    fun repr(): String {
        return fadeables
            .map { "${it.key}: ${it.value.getRgbColor()?.ansiColor()} [${it.value.getGain()}]" }
            .joinToString("")
            .trim()
    }

    override fun toString(): String {
        return fadeables
            .mapNotNull { it.value.getRgbColor()?.ansiColor() }
            .joinToString("")
            .trim()
    }

    override fun clone(): HybridScene {
        return HybridScene(ids, hexColors, gains, turnOns, preferences)
    }

    fun update(nextScene: HybridScene) {
        update(nextScene.fadeables)
    }

    fun update(fadeables: MutableMap<String, Fadeable<*>>) {
        this.fadeables.putAll(fadeables)
        initializeFromFadeables()
    }

    fun fadeableMap(): Map<String, Fadeable<*>> = fadeables.toMap()

    fun fadeables(): List<Fadeable<*>> = fadeables.values.toList()

    fun getFadeable(id: String): Fadeable<*>? = fadeables[id]

    fun putFadeable(id: String, fadeable: Fadeable<*>) {
        fadeables[id] = fadeable.clone()
        initializeFromFadeables()
    }

    private fun initializeFromFadeables() {
        this.ids = this.fadeables().map { sc -> sc.getId() }.joinToString(",")
        this.hexColors = this.fadeables().mapNotNull { sc -> sc.getRgbColor()?.hex() }.joinToString(",")
        this.gains = this.fadeables().map { sc -> sc.getGain() }.joinToString(",")
        this.turnOns = this.fadeables().mapNotNull { sc -> sc.getTurnOn() }.joinToString(",")
    }

    private fun initializeFromParameters() {
        val lIds = if (ids?.isNotEmpty() == true) {
            ids?.split(",")
                ?.filter { it.isNotEmpty() }
                ?.map { it.trim() }
                ?: listOf()
        } else {
            preferences?.getStageIds() ?: listOf()
        }

        val lHexColors = hexColors
            ?.split(",")
            ?.filter { it.isNotEmpty() }
            ?: listOf()
        val nh = lHexColors.size - 1
        var h = 0

        val lGains = gains
            ?.split(",")
            ?.filter { it.isNotEmpty() }
            ?.map { it.toFloat() }
            ?: listOf()
        val ng = lGains.size - 1
        var g = 0

        val lTurnOns = turnOns
            ?.split(",")
            ?.filter { it.isNotEmpty() }
            ?.map { it.toBoolean() }
            ?: listOf()
        val nt = lTurnOns.size - 1
        var t = 0

        val twinklyDevices = preferences?.getHybridDevices(HybridDeviceType.twinkly)
        if (twinklyDevices?.map { it.id }?.any { td -> lIds.any { td == it } } == true) {
            twinklyDevices
                .mapNotNull { preferences?.getTwinklyConfiguration(it.id) }
                .forEach { twinklyDevice ->
                    val xa = twinklyDevice.xledArray
                    if (xa.isLoggedIn()) {
                        val lc = RGBColor(lHexColors.last())
                        val frame = XledFrame(
                            width = xa.width,
                            height = xa.height,
                            initialColor = TwinklyRGBColor(lc.red, lc.green, lc.blue)
                        )
                        val nc = lHexColors.size
                        val barWidth = xa.width / nc
                        for (x in 0 until nc - 1) {
                            val rgbColor = RGBColor(lHexColors[x])
                            val bar = XledFrame(
                                width = barWidth,
                                height = xa.height,
                                initialColor = TwinklyRGBColor(rgbColor.red, rgbColor.green, rgbColor.blue)
                            )
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
                            val paramGain = (255 * effectiveGain).roundToInt()
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

    fun getGain(id: String): Float = fadeables[id]?.getGain() ?: 1.0f

    fun setRgbColor(id: String, rgbColor: RGBColor) {
        fadeables[id]?.setRgbColor(rgbColor)
        initializeFromFadeables()
    }

    fun getRgbColor(id: String): RGBColor? = fadeables[id]?.getRgbColor()

    override fun fade(
        other: HybridScene,
        fadeDuration: Long,
        preferences: Preferences
    ) {
        if (fadeDuration > 0) {
            runBlocking {
                val t = System.currentTimeMillis()
                coroutineScope {
                    // call shelly interface which is pretty slow with native fading (RGBW shelly devices can fade on their own)
                    // but api calls cost around 100 millis
                    other.fadeableMap().filter { it.value is ShellyColor }.forEach {
                        val otherFadeable = it.value as ShellyColor
                        val fadeable = fadeables[otherFadeable.getId()]
                        if (fadeable != null && otherFadeable.getRgbColor() != fadeable.getRgbColor()) {
                            launch { otherFadeable.write(preferences, true, fadeDuration) }
                        }
                    }
                }

                // take total launch costs for shelly devices into account - use at least time for one dmx frame
                val dmxFrameTime = preferences.getDmxFrameTime()
                val remainingDuration = max(dmxFrameTime, fadeDuration - System.currentTimeMillis() + t)
                val step = 1.0 / remainingDuration.toDouble() * dmxFrameTime.toDouble()

                val parameterSets = fadeables
                    .filter { it.value is ParameterSet }
                    .map { Pair(it.key, it.value as ParameterSet) }
                    .toMap()
                val otherParameterSets = other.fadeables
                    .filter { parameterSets.containsKey(it.key) && it.value is ParameterSet }
                    .map { Pair(it.key, it.value as ParameterSet) }
                    .toMap()

                val xledFrames = fadeables
                    .filter { it.value is XledFrameFadeable }
                    .map { Pair(it.key, it.value as XledFrameFadeable) }
                    .toMap()
                val otherXledFrames = other.fadeables
                    .filter { xledFrames.containsKey(it.key) && it.value is XledFrameFadeable }
                    .map { Pair(it.key, it.value as XledFrameFadeable) }
                    .toMap()

                var factor = 0.0
                while (factor <= 1.0) {
                    if (otherParameterSets.isNotEmpty()) { // only write to dmx interface if needed
                        preferences.let {
                            // first collect all frame data for the dmx frame to avoid lots of costly write operations to a serial interface
                            otherParameterSets.forEach { (id, otherParameterSet) ->
                                val parameterSet = parameterSets[id]
                                if (parameterSet != null && otherParameterSet.getRgbColor() != parameterSet.getRgbColor()) {
                                    val faded = parameterSet.fade(otherParameterSet, factor)
                                    preferences.setDmxData(
                                        baseChannel = faded.baseChannel,
                                        bytes = faded.toBytes(it)
                                    )
                                }
                            }
                            preferences.writeDmxData()
                        }
                    }

                    if (otherXledFrames.isNotEmpty()) {
                        otherXledFrames.forEach { (id, otherXledFrame) ->
                            val xledFrame = xledFrames[id]
                            if (xledFrame != null) {
                                val faded = xledFrame.fade(otherXledFrame, factor)
                                faded.write(preferences, true)
                            }
                        }
                    }

                    factor += step
                    Thread.sleep(dmxFrameTime)
                }
            }
        }

        other.write(preferences)
    }

    override fun write(preferences: Preferences?, write: Boolean, transitionDuration: Long) {
        val parameterSets = fadeables().filterIsInstance<ParameterSet>()
        if (parameterSets.isNotEmpty()) { // only write to dmx interface if needed
            preferences?.let {
                // first collect all frame data for the dmx frame to avoid lots of costly write operations to a serial interface
                parameterSets.forEach { parameterSet ->
                    preferences.setDmxData(
                        baseChannel = parameterSet.baseChannel,
                        bytes = parameterSet.toBytes(it)
                    )
                }
                if (write) {
                    log.debug("Writing hybrid scene {}", this)
                    preferences.writeDmxData()
                }
            }
        }

        // call twinkly interface which is not so fast
        fadeables().filterIsInstance<XledFrameFadeable>().forEach {
            it.write(preferences, true)
        }

        // call shelly interface which is pretty fast
        fadeables().filterIsInstance<ShellyColor>().forEach {
            it.write(preferences, true)
        }
    }

    override fun fade(other: Any, factor: Double): HybridScene {
        return if (other is HybridScene) {
            other
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
