package de.visualdigits.klanglicht.model.hybrid

import de.visualdigits.klanglicht.hardware.shelly.model.ShellyColor
import de.visualdigits.klanglicht.hardware.twinkly.model.XledFrameDmxFadeable
import de.visualdigits.klanglicht.model.dmx.parameter.IntParameter
import de.visualdigits.klanglicht.model.dmx.parameter.ParameterSet
import de.visualdigits.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import de.visualdigits.kotlin.twinkly.model.parameter.Fadeable
import de.visualdigits.kotlin.twinkly.model.playable.XledFrame
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class HybridScene(
    private val preferences: Preferences
) : Fadeable<HybridScene> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private var ids: List<String> = listOf()
    private var hexColors: List<String> = listOf()
    private var gains: List<Double> = listOf()
    private var turnOns: String? = null

    private val fadeables: MutableMap<String, Fadeable<*>> = mutableMapOf()

    constructor(
        ids: List<String> = listOf(),
        hexColors: List<String> = listOf(),
        gains: List<Double> = listOf(),
        turnOns: String? = "true",
        preferences: Preferences
    ) : this(preferences) {
        this.ids = ids
        this.hexColors = hexColors
        this.gains = gains
        this.turnOns = turnOns

        initializeFromParameters()
        initializeFromFadeables() // modify attributes after updating fadeables
    }

    override fun toString(): String {
        return fadeables
            .mapNotNull { it.value.toRgbColor()?.ansiColor() }
            .joinToString("")
            .trim() + " " +
        fadeables
            .mapNotNull { it.value.toRgbColor()?.hex() }
    }

    override fun clone(): HybridScene {
        return HybridScene(ids, hexColors, gains, turnOns, preferences)
    }

    fun update(nextScene: HybridScene) {
        update(nextScene.fadeables)
    }

    fun update(fadeables: Map<String, Fadeable<*>>) {
        this.fadeables.putAll(fadeables)
        initializeFromFadeables()
    }

    fun fadeableMap(): Map<String, Fadeable<*>> = fadeables.toMap()

    fun fadeables(): List<Fadeable<*>> = fadeables.values.toList()

    fun getFadeable(id: String): Fadeable<*>? = fadeables[id]

    fun putFadeable(id: String, Fadeable: Fadeable<*>) {
        fadeables[id] = Fadeable.clone()
        initializeFromFadeables()
    }

    private fun initializeFromFadeables() {
        this.ids = this.fadeables().map { sc -> sc.getId() }
        this.hexColors = this.fadeables().mapNotNull { sc -> sc.toRgbColor()?.hex() }
        this.gains = this.fadeables().map { sc -> sc.getGain() }
        this.turnOns = this.fadeables().mapNotNull { sc -> sc.getTurnOn() }.joinToString(",")
    }

    private fun initializeFromParameters() {
        val lIds = if (ids.isNotEmpty() == true) {
            ids
        } else {
            preferences?.getStageIds() ?: listOf()
        }

        val nh = hexColors.size - 1
        var h = 0

        val ng = gains.size - 1
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
                        val lc = RGBColor(hexColors.last())
                        val frame = XledFrame(
                            width = xa.width,
                            height = xa.height,
                            initialColor = RGBColor(lc.red, lc.green, lc.blue)
                        )
                        val nc = hexColors.size
                        val barWidth = xa.width / nc
                        for (x in 0 until nc - 1) {
                            val rgbColor = RGBColor(hexColors[x])
                            val bar = XledFrame(
                                width = barWidth,
                                height = xa.height,
                                initialColor = RGBColor(rgbColor.red, rgbColor.green, rgbColor.blue)
                            )
                            frame.replaceSubFrame(bar, x * barWidth, 0)
                        }
                        val fadeable = XledFrameDmxFadeable(
                            deviceId = twinklyDevice.name,
                            xledFrame = frame,
                            deviceGain = twinklyDevice.gain,
                            preferences = preferences
                        )
                        fadeables[twinklyDevice.name] = fadeable
                    }
                }
        }

        lIds.forEach { id ->
            val device = preferences.getHybridDevice(id)
            if (device != null) {
                val hexColor = hexColors[min(nh, h++)]
                val gain = gains.getOrNull(min(ng, g++))
                val turnOn = lTurnOns.getOrNull(min(nt, t++)) ?: false
                val rgbColor = RGBColor(hexColor)
                when (device.type) {
                    HybridDeviceType.dmx -> {
                        val dmxDevice = preferences.dmx?.dmxDevices?.get(id)
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

    fun setGain(id: String, gain: Double) {
        fadeables[id]?.setGain(gain)
        initializeFromFadeables()
    }

    fun getGain(id: String): Double = fadeables[id]?.getGain() ?: 1.0

    fun setRgbColor(id: String, rgbColor: RGBColor) {
        fadeables[id]?.setRgbColor(rgbColor)
        initializeFromFadeables()
    }

    fun getRgbColor(id: String): RGBColor? = fadeables[id]?.toRgbColor()

    override fun fade(
        other: HybridScene,
        fadeDuration: Long,
        frameTime: Long
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
                        if (fadeable != null && otherFadeable.toRgbColor() != fadeable.toRgbColor()) {
                            launch { otherFadeable.write(true, fadeDuration) }
                        }
                    }
                }

                // take total launch costs for shelly devices into account - use at least time for one dmx frame
                val dmxFrameTime = preferences.dmx!!.frameTime
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
//                    .filter { it.value is XledFrameDmxFadeable }
                    .map { Pair(it.key, it.value as XledFrameDmxFadeable) }
                    .toMap()
                val otherXledFrames = other.fadeables
                    .filter { xledFrames.containsKey(it.key) && it.value is XledFrameDmxFadeable }
                    .map { Pair(it.key, it.value as XledFrameDmxFadeable) }
                    .toMap()

                var factor = 0.0
                while (factor <= 1.0) {
                    if (otherParameterSets.isNotEmpty()) { // only write to dmx interface if needed
                        // first collect all frame data for the dmx frame to avoid lots of costly write operations to a serial interface
                        otherParameterSets.forEach { (id, otherParameterSet) ->
                            val parameterSet = parameterSets[id]
                            if (parameterSet != null && otherParameterSet.toRgbColor() != parameterSet.toRgbColor()) {
                                val faded = parameterSet.fade(otherParameterSet, factor, BlendMode.AVERAGE)
                                preferences.dmx!!.setDmxData(
                                    baseChannel = faded.baseChannel,
                                    bytes = bytesFromParameterset(faded)
                                )
                            }
                        }
                        preferences.dmx!!.writeDmxData()
                    }

                    if (otherXledFrames.isNotEmpty()) {
                        otherXledFrames.forEach { (id, otherXledFrame) ->
                            val xledFrame = xledFrames[id]
                            if (xledFrame != null) {
                                val faded = xledFrame.fade(otherXledFrame, factor, BlendMode.AVERAGE)
                                faded.write(true)
                            }
                        }
                    }

                    factor += step
                    Thread.sleep(dmxFrameTime)
                }
            }
        }

        other.write()
    }

    private fun bytesFromParameterset(parameterSet: ParameterSet): ByteArray {
        return (preferences.dmx!!.fixtures.get(parameterSet.baseChannel)?.map { channel ->
            (parameterSet.parameterMap[channel.name] ?: 0).toByte()
        } ?: listOf()).toByteArray()
    }

    private fun writeParameterSet(parameterSet: ParameterSet, write: Boolean, transitionDuration: Long) {
        val bytes = bytesFromParameterset(parameterSet)
        preferences.dmx!!.setDmxData(parameterSet.baseChannel, bytes)
        if (write) {
            log.debug("Writing parameter set {}", this)
            preferences.dmx!!.writeDmxData()
        }
    }

    override fun write(write: Boolean, transitionDuration: Long) {
        val parameterSets = fadeables().filterIsInstance<ParameterSet>()
        if (parameterSets.isNotEmpty()) { // only write to dmx interface if needed
            // first collect all frame data for the dmx frame to avoid lots of costly write operations to a serial interface
            parameterSets.forEach { parameterSet ->
                preferences.dmx!!.setDmxData(
                    baseChannel = parameterSet.baseChannel,
                    bytes = bytesFromParameterset(parameterSet)
                )
            }
            if (write) {
                log.debug("Writing hybrid scene {}", this)
                preferences.dmx!!.writeDmxData()
            }
        }

        // call twinkly interface which is not so fast
        fadeables().filterIsInstance<XledFrameDmxFadeable>().forEach {
            it.write(true)
        }

        // call shelly interface which is pretty fast
        fadeables().filterIsInstance<ShellyColor>().forEach {
            it.write(true)
        }
    }

    override fun fade(other: Any, factor: Double, blendMode: BlendMode): HybridScene {
        return if (other is HybridScene) {
            other
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }

    override fun toRgbColor(): RGBColor {
        return RGBColor(0,0,0)
    }
}
