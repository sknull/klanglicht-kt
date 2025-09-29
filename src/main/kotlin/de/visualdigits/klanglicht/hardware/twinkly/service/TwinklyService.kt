package de.visualdigits.klanglicht.hardware.twinkly.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.kotlin.twinkly.model.device.xmusic.moods.MoodsEffect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TwinklyService(
    val prefs: ApplicationPreferences
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun musicOn() {
        log.info("Switching twinkly music mode on")
        xledArrays()?.forEach { a -> a.setMusicEnabled(true) }
    }

    fun musicOff() {
        log.info("Switching twinkly music mode off")
        xledArrays()?.forEach { a -> a.setMusicEnabled(false) }
    }

    fun on() {
        log.info("Switching twinkly music mode on")
        xledArrays()?.forEach { a -> a.powerOn() }
    }

    fun off() {
        log.info("Switching twinkly music mode off")
        xledArrays()?.forEach { a -> a.powerOff() }
    }

    fun moodsEffect(moodsEffect: MoodsEffect) {
        log.info("Setting twinkly music mode to '${moodsEffect.moodLabel} - ${moodsEffect.label}'")
        twinklyMusic()?.setMoodsEffect(moodsEffect)
    }

    private fun twinklyMusic() = prefs.stage?.devices?.xMusicDevice

    private fun xledArrays() = prefs.stage?.devices?.xledArrays?.values
}
