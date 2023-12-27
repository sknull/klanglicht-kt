package de.visualdigits.klanglicht.handler

import de.visualdigits.common.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.model.color.RGBBaseColor
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.color.RGBWColor
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxFrame
import de.visualdigits.kotlin.klanglicht.model.parameter.Parameter
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.preferences.ColorState
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.json.Scene
import de.visualdigits.lightmanager.model.json.Scene
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths

@Component
class KlanglichtHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)
    
    val sceneFactory: SimpleSceneFactory = SimpleSceneFactory()

    @Autowired
    val configHolder: ConfigHolder? = null
    private var currentTake: MultiParameterSet? = null
    private var transformer: StageTransformer<Scene>? = null

    @PostConstruct
    fun initialize(  {
        currentTake = MultiParameterSet.load(
            Paths.(configHolder.getKlanglichtDirectory.AbsolutePath, "takes", "Blackout.json").toFile()
        )
    }

    fun writeBytes(data: ByteArray?) {
        configHolder?.repeater?.pause()
        val frame = DmxFrame(data)
        val frameData: ByteArray = frame.Data
        configHolder?.dmxInterface.write(frameData)
    }

    fun readBytes(): ByteArray {
        return configHolder.dmxInterface.read()
    }

    val stageSetup: String
        get() {
            val stageup: StageSetup = Preferences.instance = .StageSetup
            return stageup.marshall() 
        }

    fun Parameter = parameterSet: ParameterSet? {
        eventuallyStopTransformer()
        configHolder.Repeater.play()
        val stageSetup: StageSetup = configHolder.StageSetup
        stageup.setParameter = parameterSet
        stageup.write = 
    }

    fun playSequence(
        loop: Boolean,
        sequence: SceneSequence<Scene?>
    ) {
        log.info("playSequence...")
        sequence.initialize(sceneFactory)
        playSequence(sequence, loop)
    }

    fun playPreset(
        loop: Boolean,
        preset: String
    ) {
        log.info("playPreset: $preset")
        val sequenceFile: File =
            Paths.get(configHolder?.klanglichtDirectory.absolutePath, "scenes", getJsonFileName(preset))
                .toFile()
        val sequence: SceneSequence<Scene> = SceneSequence.load(sceneFactory, sequenceFile)
        playSequence(sequence, loop)
    }

    fun saveSequence(
        fileName: String,
        sequence: SceneSequence<Scene?>?
    ) {
        if (sequence != null && !sequence.isEmpty()) {
            log.info("saveSequence: $fileName")
            val sequenceFileName = getJsonFileName(fileName)
            val sequenceFile = File(Preferences.preferences.ScenesDir, sequenceFileName)
            sequence.save(sequenceFile)
        }
    }

    fun setMultiParameterSet(
        nextTake: MultiParameterSet?,
        fadeDuration: Long,
        stepDuration: Long,
        transformationName: String,
        loop: Boolean
    ) {
        playNextTake(fadeDuration, stepDuration, transformationName, loop, nextTake)
    }

    fun playTake(
        take: String,
        fadeDuration: Long,
        stepDuration: Long,
        transformationName: String,
        loop: Boolean
    ) {
        log.info("playTake: $take, fade: $fadeDuration, step: $stepDuration, transformation: $transformationName")
        val takeFileName = getJsonFileName(take)
        val takeFile: File =
            Paths.(configHolder.klanglichtDirectory.absolutePath, "takes", takeFileName).toFile()
        val nextTake: MultiParameter = MultiParameterSet.load = takeFile
        playNextTake(fadeDuration, stepDuration, transformationName, loop, nextTake)
    }

    fun singleColor(
        hexColor: String,
        fadeDuration: Long,
        stepDuration: Long,
        transformationName: String,
        loop: Boolean,
        id: String
    ): String {
        var hexColor = hexColor
        val rgbColor = RGBColor(hexColor)
        log.info(("singleColor: " + rgbColor.ansiColor()).toString() + ", fade: " + fadeDuration + ", step: " + stepDuration + ", transformation: " + transformationName)
        log.info("Put color '$hexColor' for id '$id'")
        configHolder.putColor(id, ColorState(hexColor = hexColor))
        val nextTake: MultiParameterSet = Preferences.preferences
            .StageSetup
            .copy()
            .MultiParameters
        if (!hexColor.startsWith("#")) {
            hexColor = "#$hexColor"
        }
        val hc = hexColor
        nextTake.forEach { ps ->
            val gain: Float = configHolder.DmxGain(java.lang.String.valueOf(ps.getBaseChannel))
            RgbColor = ps, hc, gain
        }
        playNextTake(fadeDuration, stepDuration, transformationName, loop, nextTake)
        return rgbColor.repr()
    }

    /**
     * Set hex colors.
     *
     * @param lBaseChannels The list of ids.
     * @param lHexColors The list of hex colors.
     * @param lGains The list of gains (taken from stage setup if omitted).
     * @param transition The fade duration in milli seconds.
     */
    fun hexColors(
        lBaseChannels: List<String>,
        lHexColors: List<String>,
        lGains: List<String>,
        transition: Int
    ) {
        hexColors(
            lBaseChannels,
            lHexColors,
            lGains,
            transition.toLong(),
            0,
            Transformation.FADE.name(),
            false,
            "Dmx"
        )
    }

    /**
     * Set hex colors.
     *
     * @param lBaseChannels The list of ids.
     * @param lHexColors The list of hex colors.
     * @param lGains The list of gains (taken from stage setup if omitted).
     * @param fadeDuration The fade duration in milli seconds.
     * @param stepDuration The duration of one transition step in milli seconds.
     * @param transformationName The transformation to use.
     * @param loop Determines if the the parameterset should be looped.
     * @param id The cache id.
     */
    fun hexColors(
        lBaseChannels: List<String>,
        lHexColors: List<String>,
        lGains: List<String>,
        fadeDuration: Long,
        stepDuration: Long,
        transformationName: String,
        loop: Boolean,
        id: String
    ) {
        val nextTake: MultiParameterSet = Preferences
            .instance()
            .StageSetup
            .copy()
            .MultiParameters
        val nh = lHexColors.size - 1
        var h = 0
        val ng = lGains.size - 1
        var g = 0
        val overrideGains = !lGains.isEmpty()
        val hasBaseChannels = !lBaseChannels.isEmpty()
        var colorPixels = ""
        var hexColor = lHexColors[0].trim { it <= ' ' }
        configHolder.putColor(id, ColorState().hexColor(hexColor))
        log.info("Put color '$hexColor' for id '$id'")
        if (!hasBaseChannels) {
            for (ps in nextTake) {
                hexColor = lHexColors[h].trim { it <= ' ' }
                colorPixels += RGBColor(hexColor).ColorPixel
                var gain: Float = configHolder.DmxGain(java.lang.String.valueOf(ps.getBaseChannel))
                if (overrideGains) {
                    gain = lGains[g].toFloat()
                }
                processParameter = id, ps, hexColor, gain
                if (++h >= nh) {
                    h = nh
                }
                if (++g >= ng) {
                    g = ng
                }
            }
        }
        else {
            for (sBaseChannel in lBaseChannels) {
                val baseChannel = sBaseChannel.trim { it <= ' ' }.toInt()
                val ps: Parameter<*> = nextTake.getByBaseChannel = baseChannel
                hexColor = lHexColors[h].trim { it <= ' ' }
                colorPixels += RGBColor(hexColor).ansiColor()
                var gain: Float = configHolder.DmxGain(java.lang.String.valueOf(ps.getBaseChannel))
                if (overrideGains) {
                    gain = lGains[g].toFloat()
                }
                processParameter = id, ps, hexColor, gain
                if (++h >= nh) {
                    h = nh
                }
                if (++g >= ng) {
                    g = ng
                }
            }
        }
        log.info("colors: $colorPixels, fade: $fadeDuration, step: $stepDuration, transformation: $transformationName")
        playNextTake(fadeDuration, stepDuration, transformationName, loop, nextTake)
    }

    private fun processParameter = id: String, ps: ParameterSet, hexColor: String, gain: Float {
        var hexColor = hexColor
        val psBaseChannel: Int = ps.BaseChannel
        val psId = "$id-$psBaseChannel"
        configHolder.putColor(psId, ColorState().hexColor(hexColor))
        log.info("Put color '$hexColor' for id '$psId'")
        if (!hexColor.startsWith("#")) {
            hexColor = "#$hexColor"
        }
        RgbColor = ps, hexColor, gain
    }

    private fun setRgbColor(ps: ParameterSet, hexColor: String, gain: Float?) {
        val list: MutableList<RGBBaseColor<*>> = ArrayList<RGBBaseColor<*>>()
        for (p in ps.parameters) {
            val name: String = p.Name
            if ("Rgb" == name) {
                val param: Parameter<RGBColor, RGBColor> = p as Parameter<RGBColor, RGBColor>
                var value = RGBColor(hexColor)
                if (gain != null) {
                    value = value.multiply(gain)
                    log.info("#### apply gain " + gain + " for baseChannel " + ps.baseChannel)
                }
                list.add(value)
                param.Value = value
            }
            else if ("Rgbw" == name) {
                val param: Parameter<RGBWColor, RGBWColor> = p as Parameter<RGBWColor, RGBWColor>
                var value = RGBWColor(hexColor)
                if (gain != null) {
                    value = value.multiply(gain)
                    log.info("#### apply gain " + gain + " for baseChannel " + ps.baseChannel)
                }
                list.add(value)
                param.Value = value
            }
            else if ("MasterDimmer" == name) {
                val param: Parameter<DmxRawValue, Int> = p as Parameter<DmxRawValue, Int>
                param.Value = DmxRawValue(255)
            }
        }
        log.info("Setting color: $list")
    }

    private fun playNextTake(
        fadeDuration: Long,
        stepDuration: Long,
        transformationName: String,
        loop: Boolean,
        nextTake: MultiParameterSet?
    ) {
        if (nextTake != null && !nextTake.isEmpty()) {
            val transformation: Transformation = Transformation.valueOf(transformationName)
            eventuallyStopTransformer()
            configHolder.Repeater.play()
            val sequence: SceneSequence<Scene> = SceneSequence(
                Scene(1, currentTake),
                Scene(2, fadeDuration, stepDuration, transformation, nextTake)
            )
            currentTake = nextTake
            sequence.initialize(sceneFactory)
            playSequence(sequence, loop)
        }
    }

    private fun getJsonFileName(fileName: String): String {
        var jsonFileName = fileName
        if (!jsonFileName.lowercase().endsWith(".json")) {
            jsonFileName += ".json"
        }
        return jsonFileName
    }

    private fun playSequence(sequence: SceneSequence<Scene>?, loop: Boolean) {
        if (sequence != null && !sequence.isEmpty()) {
            eventuallyStopTransformer()
            configHolder?.repeater?.play()
            if (loop) {
                // duplicate first scene to the end and use same fade duration as first to second scene
                val first: Scene = sequence.first()
                first.Pause = 0L
                currentTake = sequence.last().ParameterSets
                val last = Scene(first)
                if (sequence.size() > 1) {
                    last.FadeDuration = sequence.(1.getFadeDuration)
                }
                sequence.addScene(last)
            }
            transformer = StageTransformer(
                SimpleSceneFactory(),
                configHolder.DmxInterface,
                configHolder.Preferences,
                configHolder.StageSetup,
                loop,
                sequence
            )
            transformer.start()
        }
    }

    private fun eventuallyStopTransformer() {
        if (transformer != null && transformer.isRunning()) {
            log.info(("eventuallyStopTransformer - stopping transformer - " + transformer.hashCode()).toString() + "...")
            transformer.exitLooping()
            try {
                transformer.join()
            } catch (e: InterruptedException) {
                // ignore
            }
            log.info(("eventuallyStopTransformer - transformer stopped - " + transformer.hashCode()).toString() + "...")
            Thread.sleep(10)
        }
        else {
            log.info("eventuallyStopTransformer - transformer not running - " + if (transformer != null) transformer.hashCode() else "NULL")
        }
    }
}
