package de.visualdigits.kotlin.klanglicht.rest.hybrid.service

import de.visualdigits.kotlin.klanglicht.hardware.shelly.client.ShellyClient
import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridScene
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HybridStageService(
    private val prefs: ApplicationPreferences,
    private val configHolder: ConfigHolder
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
     * @param store Determines if the colors should be saved in the ConfigHolder.
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
        val currentScene = configHolder.currentScene?.clone()
        val nextScene = configHolder.currentScene?.clone()?.let { n ->
            HybridScene(ids, hexColors, gains, turnOn.toString(), preferences = configHolder.preferences).fadeableMap().forEach {
                n.putFadeable(it.key, it.value)
            }
            n
        }
        if (store) {
            configHolder.updateScene(nextScene!!)
            if (storeName != null) {
                configHolder.putColor(storeName, hexColors.joinToString(","))
            }
        }
        log.info("nextScene: $nextScene")

        currentScene?.fade(nextScene!!, transition?: prefs.fadeDurationDefault, configHolder.preferences!!)
    }

    fun putColor(
        id: String,
        hexColor: String,
    ) {
        configHolder.putColor(id, hexColor)
    }


    fun restoreColors(
        ids: List<String>,
        transitionDuration: Long?
    ) {
        ids.forEach { id ->
            configHolder.getFadeable(id)?.write(configHolder.preferences!!, transitionDuration = transitionDuration?:prefs.fadeDurationDefault)
        }
    }

    fun gain(
        ids: List<String>,
        gain: Int,
        transitionDuration: Long?
    ) {
        ids.forEach { id ->
            val sid = id.trim()
            val shellyDevice = configHolder.preferences?.getShellyDevice(sid)
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val lastColor = configHolder.getFadeable(sid)
                lastColor?.setGain(gain.toDouble())
                try {
                    ShellyClient.setGain(ipAddress = ipAddress, gain = gain, transitionDuration = transitionDuration?: prefs.fadeDurationDefault)
                } catch (e: Exception) {
                    log.warn("Could not get gain for shelly at '$ipAddress'")
                }
            }
        }
    }
}
