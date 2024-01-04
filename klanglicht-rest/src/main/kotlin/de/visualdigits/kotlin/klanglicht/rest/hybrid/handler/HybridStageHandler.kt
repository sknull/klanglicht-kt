package de.visualdigits.kotlin.klanglicht.rest.hybrid.handler

import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridScene
import de.visualdigits.kotlin.klanglicht.model.shelly.client.ShellyClient
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HybridStageHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    val configHolder: ConfigHolder? = null

     /**
     * Set hex colors.
     *
     * @param ids The comma separated list of ids.
     * @param hexColors The comma separated list of hex colors.
     * @param gains The comma separated list of gains (taken from stage setup if omitted).
     * @param transitionDuration The fade duration in milli seconds.
     * @param turnOn Determines if the device should be turned on.
     * @param store Determines if the colors should be saved in the ConfigHolder.
     */
    fun hexColor(
        ids: String,
        hexColors: String,
        gains: String,
        transitionDuration: Long,
        turnOn: Boolean,
        store: Boolean = true
    ) {
        val nextScene = HybridScene(ids, hexColors, gains, turnOn.toString(), preferences = configHolder?.preferences)

         configHolder?.currentScene?.fade(nextScene, transitionDuration, configHolder.preferences!!)

         if (store) {
             configHolder?.updateScene(nextScene)
         }
    }

    fun restoreColors(
        ids: String,
        transitionDuration: Long
    ) {
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }

        lIds.forEach { id ->
            configHolder?.getFadeable(id)?.write(configHolder.preferences!!, transitionDuration = transitionDuration)
        }
    }

    fun gain(
        ids: String,
        gain: Int,
        transitionDuration: Long
    ) {
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        lIds.forEach { id ->
            val sid = id.trim()
            val shellyDevice = configHolder?.preferences?.getShellyDevice(sid)
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val lastColor = configHolder?.getFadeable(sid)
                lastColor?.setGain(gain.toFloat())
                try {
                    ShellyClient.setGain(ipAddress = ipAddress, gain = gain, transitionDuration = transitionDuration)
                } catch (e: Exception) {
                    log.warn("Could not get gain for shelly at '$ipAddress'")
                }
            }
        }
    }
}
