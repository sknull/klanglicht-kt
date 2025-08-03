package de.visualdigits.klanglicht.twinkly.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.kotlin.twinkly.model.device.xmusic.XMusic
import de.visualdigits.kotlin.twinkly.model.device.xmusic.moods.MoodsEffect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TwinklyService(
    prefs: ApplicationPreferences
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val twinklyMusic = prefs.stage
        ?.devices
        ?.xledArrays
        ?.get("twinklymusic")
        ?.xLedDevices
        ?.firstOrNull()
        ?.firstOrNull()
        ?.let { tm -> tm as XMusic }

    private val xledArrays = prefs.stage
        ?.devices
        ?.xledArrays
        ?.values
        ?.filter { a -> (a.xLedDevices.firstOrNull()?.firstOrNull()?:Any())::class != XMusic::class }
        ?:listOf()

    fun musicOn() {
        log.info("Switching twinkly music mode on")
        xledArrays.forEach { a -> a.setMusicEnabled(true) }
    }

    fun musicOff() {
        log.info("Switching twinkly music mode off")
        xledArrays.forEach { a -> a.setMusicEnabled(false) }
    }

    fun on() {
        log.info("Switching twinkly music mode on")
        xledArrays.forEach { a -> a.powerOn() }
    }

    fun off() {
        log.info("Switching twinkly music mode off")
        xledArrays.forEach { a -> a.powerOff() }
    }

    fun moodsEffect(moodsEffect: MoodsEffect) {
        log.info("Setting twinkly music mode to '${moodsEffect.moodLabel} - ${moodsEffect.label}'")
        twinklyMusic?.setMoodsEffect(moodsEffect)
    }
}
