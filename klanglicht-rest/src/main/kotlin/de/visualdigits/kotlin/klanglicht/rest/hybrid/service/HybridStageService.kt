package de.visualdigits.kotlin.klanglicht.rest.hybrid.service

import de.visualdigits.kotlin.klanglicht.hardware.hybrid.HybridScene
import de.visualdigits.kotlin.klanglicht.hardware.shelly.client.ShellyClient
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class HybridStageService(
    val configHolder: ConfigHolder
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
        ids: String,
        hexColors: String,
        gains: String,
        transition: Long?,
        turnOn: Boolean,
        store: Boolean = true,
        storeName: String?
    ) {
        val currentScene = configHolder?.currentScene?.clone()
        val nextScene = configHolder?.currentScene?.clone()?.let { n ->
            HybridScene(ids, hexColors, gains, turnOn.toString(), preferences = configHolder.preferences).fadeableMap().forEach {
                n.putFadeable(it.key, it.value)
            }
            n
        }
        if (store) {
            configHolder?.updateScene(nextScene!!)
            if (storeName != null) {
                configHolder?.putColor(storeName, hexColors)
            }
        }
        log.info("nextScene: $nextScene")

        currentScene?.fade(nextScene!!, transition?:configHolder?.preferences?.fadeDurationDefault?:1000, configHolder?.preferences!!)
    }

    fun putColor(
        id: String,
        hexColor: String,
    ) {
        configHolder?.putColor(id, hexColor)
    }


    fun restoreColors(
        ids: String,
        transitionDuration: Long?
    ) {
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }

        lIds.forEach { id ->
            configHolder?.getFadeable(id)?.write(configHolder.preferences!!, transitionDuration = transitionDuration?:configHolder.preferences?.fadeDurationDefault?:1000)
        }
    }

    fun gain(
        ids: String,
        gain: Int,
        transitionDuration: Long?
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
                    ShellyClient.setGain(ipAddress = ipAddress, gain = gain, transitionDuration = transitionDuration?:configHolder?.preferences?.fadeDurationDefault?:1000)
                } catch (e: Exception) {
                    log.warn("Could not get gain for shelly at '$ipAddress'")
                }
            }
        }
    }
}
