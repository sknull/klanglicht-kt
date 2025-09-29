package de.visualdigits.klanglicht.hardware.hybrid.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.hybrid.model.HybridScene
import de.visualdigits.klanglicht.hardware.shelly.webclient.ShellyClient
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
        sceneName: String? = null,
        wheelId: String? = null,
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
            HybridScene(prefs.stage!!, ids, hexColors, gains, turnOn.toString()).fadeableMap().forEach {
                n.putFadeable(it.key, it.value)
            }
            n
        }
        if (store) {
            storeScene(nextScene, hexColors, wheelId, storeName)
        }
        log.info("nextScene: $nextScene")

        sceneName?.also { s -> prefs.currentSceneName = s }
        currentScene?.fade(nextScene!!, transition?:prefs.stage?.fadeDurationDefault?:2000)
    }

    private fun storeScene(
        nextScene: HybridScene?,
        hexColors: List<String>,
        wheelId: String?,
        storeName: String?
    ) {
        nextScene?.also { s -> prefs.updateScene(s) } ?: log.warn("No next scene")
        val keys = prefs.stage?.devices?.stage?.map { it.id }
        val remaining = prefs.stage?.devices?.colorWheels?.map { it.id }?.toMutableSet() ?: mutableSetOf()
        val colors = hexColors.joinToString(",")
        // update other affected color wheels
        wheelId
            ?.let { wid -> prefs.stage?.devices?.colorWheelMap?.get(wid)?.updates }
            ?.forEach { wid -> prefs.putColor(wid, colors) }
        if (storeName != null) {
            prefs.putColor(storeName, colors)
            remaining.remove(storeName)
        }

        val pairs = hexColors.chunked(2)
        val stagePairs = keys?.chunked(2)

        storeOdd(pairs, stagePairs, remaining)
        storeEven(pairs, stagePairs, remaining)
        storeAll(hexColors, remaining)

        remaining.forEach { id ->
            prefs.colorStore.remove(id)
        }
    }

    private fun storeAll(
        hexColors: List<String>,
        remaining: MutableSet<String>
    ) {
        val allColors = hexColors.first()
        if (hexColors.all { it == allColors }) {
            remaining.remove("All")
            prefs.stage?.devices?.colorWheels?.forEach { cw ->
                prefs.putColor(cw.id, allColors)
                remaining.remove(cw.id)
            }
        }
    }

    private fun storeEven(
        pairs: List<List<String>>,
        stagePairs: List<List<String>>?,
        remaining: MutableSet<String>
    ) {
        val even = pairs.mapNotNull { if (it.size > 1) it[1] else null }
        val stageEven = stagePairs?.mapNotNull { if (it.size > 1) it[1] else null }
        val evenColor = even.firstOrNull()
        if (even.all { it == evenColor }) {
            prefs.putColor("AllEven", evenColor)
            remaining.remove("AllEven")
            stageEven?.forEach { id ->
                prefs.putColor(id, evenColor)
                remaining.remove(id)
            }
        }
    }

    private fun storeOdd(
        pairs: List<List<String>>,
        stagePairs: List<List<String>>?,
        remaining: MutableSet<String>
    ) {
        val odd = pairs.mapNotNull { if (it.isNotEmpty()) it[0] else null }
        val stageOdd = stagePairs?.mapNotNull { if (it.isNotEmpty()) it[0] else null }
        val oddColor = odd.firstOrNull()
        if (odd.all { it == oddColor }) {
            val hexColor = oddColor
            prefs.putColor("AllOdd", hexColor)
            remaining.remove("AllOdd")
            stageOdd?.forEach { id ->
                prefs.putColor(id, oddColor)
                remaining.remove(id)
            }
        }
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
            prefs.getFadeable(id)?.write(transitionDuration = transitionDuration?: prefs.stage?.fadeDurationDefault?:2000)
        }
    }

    fun gain(
        ids: List<String>,
        gain: Int,
        transitionDuration: Long?
    ) {
        ids.forEach { id ->
            val sid = id.trim()
            val shellyDevice = prefs.stage?.devices?.shellyMap?.get(sid)
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val lastColor = prefs.getFadeable(sid)
                lastColor?.setGain(gain.toDouble())
                try {
                    ShellyClient.setGain(ipAddress = ipAddress, gain = gain, transitionDuration = transitionDuration?: prefs.stage?.fadeDurationDefault?:2000)
                } catch (e: Exception) {
                    log.warn("Could not get gain for shelly at '$ipAddress'")
                }
            }
        }
    }
}
