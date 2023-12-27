package de.visualdigits.klanglicht.controller

import de.visualdigits.klanglicht.handler.KlanglichtHandler
import de.visualdigits.klanglicht.model.parameter.MultiParameterSet
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Arrays

/**
 * REST controller for DMX devices.
 */
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/v1")
class KlanglichtController {

    @Autowired
    val klanglichtHandler: KlanglichtHandler? = null

    @PostMapping(value = ["/writeBytes"], consumes = [MediaType.ALL_VALUE])
    fun writeBytes(@RequestBody data: ByteArray?) {
        klanglichtHandler.writeBytes(data)
    }

    @GetMapping(value = ["/readBytes"])
    fun readBytes(): ByteArray {
        return klanglichtHandler.readBytes()
    }

    @GetMapping(value = ["/getStageSetup"])
    val stageSetup: String
        get() = klanglichtHandler.stageSetup

    @PostMapping("/setParameter")
    fun setParameter(@RequestBody parameterSet: ParameterSet?) {
        klanglichtHandler.setParameter(parameterSet)
    }

    @PostMapping("/playSequence")
    fun playSequence(
        @RequestParam(value = "loop", required = false, defaultValue = "false") loop: Boolean,
        @RequestBody sequence: SceneSequence<Scene?>?
    ) {
        klanglichtHandler.playSequence(loop, sequence)
    }

    @GetMapping("/playPreset")
    fun playPreset(
        @RequestParam(value = "loop", required = false, defaultValue = "false") loop: Boolean,
        @RequestParam preset: String?
    ) {
        klanglichtHandler.playPreset(loop, preset)
    }

    @PostMapping("/saveSequence")
    fun saveSequence(
        @RequestParam(value = "fileName") fileName: String?,
        @RequestBody sequence: SceneSequence<Scene?>?
    ) {
        klanglichtHandler.saveSequence(fileName, sequence)
    }

    @PostMapping("/setMultiParameterSet")
    fun setMultiParameterSet(
        @RequestBody nextTake: MultiParameterSet?,
        @RequestParam(required = false, defaultValue = "2000") fadeDuration: Long,
        @RequestParam(required = false, defaultValue = "0") stepDuration: Long,
        @RequestParam(required = false, defaultValue = "FADE") transformationName: String?,
        @RequestParam(required = false, defaultValue = "false") loop: Boolean
    ) {
        klanglichtHandler.MultiParameterSet = nextTake, fadeDuration, stepDuration, transformationName, loop
    }

    @GetMapping("/playTake")
    fun playTake(
        @RequestParam(value = "take") take: String?,
        @RequestParam(required = false, defaultValue = "2000") fadeDuration: Long,
        @RequestParam(required = false, defaultValue = "0") stepDuration: Long,
        @RequestParam(required = false, defaultValue = "FADE") transformationName: String?,
        @RequestParam(required = false, defaultValue = "false") loop: Boolean
    ) {
        klanglichtHandler.playTake(take, fadeDuration, stepDuration, transformationName, loop)
    }

    @GetMapping(value = ["/singleColor"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun singleColor(
        @RequestParam(value = "hexColor") hexColor: String?,
        @RequestParam(required = false, defaultValue = "2000") fadeDuration: Long?,
        @RequestParam(required = false, defaultValue = "0") stepDuration: Long?,
        @RequestParam(required = false, defaultValue = "FADE") transformationName: String?,
        @RequestParam(required = false, defaultValue = "false") loop: Boolean?,
        @RequestParam(value = "id", required = false, defaultValue = "id") id: String?
    ): String? {
        return klanglichtHandler?.singleColor(hexColor!!, fadeDuration!!, stepDuration!!, transformationName!!, loop!!, id!!)
    }

    @GetMapping("/colors")
    fun colors(
        @RequestParam(value = "hexColors") hexColors: String,
        @RequestParam(value = "gains", required = false, defaultValue = "") gains: String?,
        @RequestParam(value = "baseChannels", required = false, defaultValue = "") baseChannels: String?,
        @RequestParam(value = "fadeDuration", required = false, defaultValue = "2000") fadeDuration: Long?,
        @RequestParam(value = "stepDuration", required = false, defaultValue = "0") stepDuration: Long?,
        @RequestParam(value = "transformationName", required = false, defaultValue = "FADE") transformationName: String?,
        @RequestParam(value = "loop", required = false, defaultValue = "false") loop: Boolean?,
        @RequestParam(value = "id", required = false, defaultValue = "id") id: String?
    ) {
        val lIds = baseChannels
            ?.split(",".toRegex())
            ?.dropLastWhile { it.isEmpty() }
            ?.filter { it.isNotEmpty() }
            ?: listOf()
        val lHexColors = hexColors.split(",".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
            .filter { it.isNotEmpty() }
        val lGains = gains?.split(",".toRegex())
            ?.dropLastWhile { it.isEmpty() }
            ?: listOf()
        klanglichtHandler?.hexColors(lIds, lHexColors, lGains, fadeDuration!!, stepDuration!!, transformationName!!, loop!!, id!!)
    }
}
