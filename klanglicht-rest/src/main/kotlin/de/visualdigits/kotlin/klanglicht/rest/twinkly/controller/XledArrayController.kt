package de.visualdigits.kotlin.klanglicht.rest.twinkly.controller

import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBWColor
import de.visualdigits.kotlin.twinkly.model.device.xled.response.Timer
import de.visualdigits.kotlin.twinkly.model.device.xled.response.mode.DeviceMode
import de.visualdigits.kotlin.twinkly.model.playable.Playable
import de.visualdigits.kotlin.twinkly.model.playable.XledFrame
import de.visualdigits.kotlin.twinkly.model.playable.XledSequence
import de.visualdigits.kotlin.twinkly.model.playable.transition.TransitionDirection
import de.visualdigits.kotlin.twinkly.model.playable.transition.TransitionType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController()
@RequestMapping("/twinkly/api/v1/xledarray")
class XledArrayController(
    var configHolder: ConfigHolder
) {

    private val log = LoggerFactory.getLogger(XledArrayController::class.java)


    private var playable: Playable? = null

    private var currentMode: DeviceMode = DeviceMode.off

    @GetMapping("/hello")
    fun hello(): String {
        return "hello"
    }

    @PutMapping("/power/on")
    fun powerOn() {
        log.info("Powering on")
        configHolder.preferences?.getXledArrays()?.forEach { it.powerOn() }
    }

    @PutMapping("/power/off")
    fun powerOff() {
        log.info("Powering off")
        configHolder.preferences?.getXledArrays()?.forEach { it.powerOff() }
    }

    @PutMapping("/brightness/{brightness}")
    fun setBrightness(
        @PathVariable brightness: Float,
    ) {
        log.info("Setting brightness to $brightness")
        configHolder.preferences?.getXledArrays()?.forEach { it.setBrightness(brightness) }
    }

    @PutMapping("/saturation/{saturation}")
    fun setSaturation(
        @PathVariable saturation: Float,
    ) {
        log.info("Setting saturation to $saturation")
        configHolder.preferences?.getXledArrays()?.forEach { it.setSaturation(saturation) }
    }

    @PutMapping("/mode/{mode}")
    fun setMode(
        @PathVariable mode: String,
    ) {
        log.info("Setting saturation to $mode")
        currentMode = DeviceMode.valueOf(mode)
        configHolder.preferences?.getXledArrays()?.forEach { it.setMode(currentMode) }
    }

    @PutMapping("/color/{red}/{green}/{blue}/{white}")
    fun setColor(
        @PathVariable red: Int,
        @PathVariable green: Int,
        @PathVariable blue: Int,
        @PathVariable white: Int,
    ) {
        val rgbwColor = RGBWColor(red, green, blue, white)
        log.info("Showing color ${rgbwColor.ansiColor()}")
        configHolder.preferences?.getXledArrays()?.forEach { it.setMode(DeviceMode.color) }
        configHolder.preferences?.getXledArrays()?.forEach { it.setColor(rgbwColor) }
    }

    @PostMapping("/image")
    fun showImage(@RequestBody bytes: ByteArray) {
        if (playable != null && playable?.running == true) {
            stopLoop()
        }
// todo
//        currentMode = configHolder?.xledArray?.getMode()
        configHolder.preferences?.getXledArrays()?.forEach { it.setMode(DeviceMode.rt) }
        playable = XledFrame(bytes)
        configHolder.preferences?.getXledArrays()?.forEach { playable?.playAsync(xled = it) }
    }

    @PostMapping("/sequence")
    fun showSequence(
        @RequestParam(required = false, defaultValue = "-1") loop: Int,
        @RequestParam(required = false, defaultValue = "false") random: Boolean,
        @RequestParam(required = false, defaultValue = "STRAIGHT") transitionType: TransitionType,
        @RequestParam(required = false, defaultValue = "LEFT_RIGHT") transitionDirection: TransitionDirection,
        @RequestParam(required = false, defaultValue = "REPLACE") transitionBlendMode: BlendMode,
        @RequestParam(required = false, defaultValue = "2550") transitionDuration: Long,
        @RequestParam(required = false, defaultValue = "100") frameDelay: Long,
        @RequestBody directory: String
    ) {
        if (playable != null && playable?.running == true) {
            stopLoop()
        }
// todo
//        currentMode = configHolder?.xledArray?.getMode()
        configHolder.preferences?.getXledArrays()?.forEach { it.setMode(DeviceMode.rt) }
        playable = XledSequence(frameDelay = frameDelay,
            directory = File(ClassLoader.getSystemResource(directory).toURI()))
        configHolder.preferences?.getXledArrays()?.forEach {
            playable?.playAsync(
                xled = it,
                loop = loop,
                random = random,
                transitionType = transitionType,
                transitionDirection = transitionDirection,
                transitionBlendMode = transitionBlendMode,
                transitionDuration = transitionDuration
            )
        }
    }

    @PutMapping("/loop/stop")
    fun stopLoop() {
        configHolder.preferences?.getXledArrays()?.forEach { it.setMode(currentMode) }
        playable?.stop()
    }

    @GetMapping("/timer", produces = ["application/json"])
    fun getTimer(): Timer? {
        return configHolder.preferences?.getXledArrays()?.firstOrNull()?.getTimer()
    }

    @PostMapping("/timer", consumes = ["application/json"], produces = ["application/json"])
    fun setTimer(
        @RequestBody timer: Timer
    ): Timer? {
        return configHolder.preferences?.getXledArrays()?.map { it.setTimer(timer) }?.firstOrNull()
    }
}
