package de.visualdigits.kotlin.klanglicht.rest.hybrid.service

import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridScene
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import de.visualdigits.kotlin.klanglicht.rest.shelly.webclient.ShellyClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HybridStageService(
    private val prefs: ApplicationPreferences
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Set hex colors.
     *
     * @param ids The comma separated list of ids.
     * @param hexColors The comma separated list of hex colors.
     * @param gains The comma separated list of gains (taken from stage setup if omitted).
     * @param transition The fade duration in milli seconds.
     * @param turnOn Determines if the device should be turned on.
     * @param store Determines if the colors should be saved in the prefs.
     * @param storeName An additional name to strore values.
     */
    fun hexColor(
        ids: List<String> = listOf(),
        hexColors: List<String> = listOf(),
        gains: List<Double> = listOf(),
        transition: Long? = null,
        turnOn: Boolean = true,
        store: Boolean = true,
        storeName: String? = null
    ) {
        val currentScene = prefs.currentScene?.clone()
        val nextScene = prefs.currentScene?.clone()?.let { n ->
            HybridScene(ids, hexColors, gains, turnOn.toString(), preferences = prefs.preferences).fadeableMap().forEach {
                n.putFadeable(it.key, it.value)
            }
            n
        }
        if (store) {
            prefs.updateScene(nextScene!!)
            if (storeName != null) {
                prefs.putColor(storeName, hexColors.joinToString(","))
            }
        }
        log.info("nextScene: $nextScene")

        currentScene?.fade(nextScene!!, transition?: prefs.preferences?.fadeDurationDefault?:2000, prefs.preferences!!)
    }

    fun putColor(
        id: String,
        hexColor: String,
    ) {
        prefs.putColor(id, hexColor)
    }


    fun restoreColors(
        ids: List<String>,
        transitionDuration: Long?
    ) {
        ids.forEach { id ->
            prefs.getFadeable(id)?.write(prefs.preferences!!, transitionDuration = transitionDuration?:prefs.preferences?.fadeDurationDefault?:2000)
        }
    }

    fun gain(
        ids: List<String>,
        gain: Int,
        transitionDuration: Long?
    ) {
        ids.forEach { id ->
            val sid = id.trim()
            val shellyDevice = prefs.preferences?.getShellyDevice(sid)
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val lastColor = prefs.getFadeable(sid)
                lastColor?.setGain(gain.toDouble())
                try {
                    ShellyClient.setGain(ipAddress = ipAddress, gain = gain, transitionDuration = transitionDuration?: prefs.preferences?.fadeDurationDefault?:2000)
                } catch (e: Exception) {
                    log.warn("Could not get gain for shelly at '$ipAddress'")
                }
            }
        }
    }
}
