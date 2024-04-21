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
            val keys = prefs.preferences?.stage?.map { it.id }?:listOf()
            val remaining = prefs.preferences?.colorWheels?.map { it.id }?.toMutableSet()?:mutableSetOf()

            if (storeName != null) {
                prefs.putColor(storeName, hexColors.joinToString(","))
                remaining.remove(storeName)
            }

            val pairs = hexColors.chunked(2)
            val stagePairs = keys.chunked(2)

            val odd = pairs.mapNotNull { if (it.isNotEmpty()) it[0] else null }
            val stageOdd = stagePairs.mapNotNull { if (it.isNotEmpty()) it[0] else null }
            val oddColor = odd.firstOrNull()
            if (odd.all { it == oddColor }) {
                val hexColor = oddColor
                prefs.putColor("AllOdd", hexColor)
                remaining.remove("AllOdd")
                stageOdd.forEach { id ->
                    prefs.putColor(id, oddColor)
                    remaining.remove(id)
                }
            }

            val even = pairs.mapNotNull {if (it.size > 1) it[1] else null }
            val stageEven = stagePairs.mapNotNull {if (it.size > 1) it[1] else null }
            val evenColor = even.firstOrNull()
            if (even.all { it == evenColor }) {
                prefs.putColor("AllEven", evenColor)
                remaining.remove("AllEven")
                stageEven.forEach { id ->
                    prefs.putColor(id, evenColor)
                    remaining.remove(id)
                }
            }

            val allColors = hexColors.first()
            if (hexColors.all { it == allColors }) {
                remaining.remove("All")
                prefs.preferences?.colorWheels?.forEach { cw ->
                    prefs.putColor(cw.id, allColors)
                    remaining.remove(cw.id)
                }
            }

            remaining.forEach { id ->
                prefs.colorStore.remove(id)
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
