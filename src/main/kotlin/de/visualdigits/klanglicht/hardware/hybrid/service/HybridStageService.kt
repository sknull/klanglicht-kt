package de.visualdigits.klanglicht.hardware.hybrid.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.hybrid.model.HybridScene
import de.visualdigits.klanglicht.hardware.lightmanager.model.client.ClientColorPicker
import de.visualdigits.klanglicht.hardware.lightmanager.model.client.ClientDevice
import de.visualdigits.klanglicht.hardware.lightmanager.model.client.ClientGroup
import de.visualdigits.klanglicht.hardware.lightmanager.model.client.ClientScene
import de.visualdigits.klanglicht.hardware.lightmanager.model.client.ClientStage
import de.visualdigits.klanglicht.hardware.shelly.webclient.ShellyClient
import jakarta.annotation.PreDestroy
import org.jetbrains.kotlin.util.prefixIfNot
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HybridStageService(
    private val prefs: ApplicationPreferences
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @PreDestroy
    fun tearDown() {
        log.info("### Shutting down...")
        hexColor("shutdown", hexColors = listOf("000000"), store = false)
        prefs.stage?.devices?.dmx?.tearDownDmx()
    }

    fun getClientStage(): ClientStage {
        val scenes = prefs.loadScenes()

        return ClientStage(
            name = scenes.name?:"?",
            currentScene = prefs.currentScene?.fadeableMap()?.map { (id, fadeable) -> ClientDevice(id, fadeable.toRgbColor().web() ) } ?: listOf(),
            colorPickers = prefs.stage?.devices?.colorWheelMap?.map { (id, colorWheel) ->
                val currentColor = prefs.colorStore[id]
                Pair(id, ClientColorPicker(
                    id = id,
                    updates = colorWheel.devices + colorWheel.updates,
                    currentColor = currentColor?.prefixIfNot("#")?:"#000000"
                ))
            }?.toMap()?:mapOf(),
            groups = scenes.groups.map { group ->
                val colorPickers = mutableListOf<Pair<String, String>>()
                if (group.hasColorWheel) {
                    colorPickers.add(Pair("all", group.name))
                }
                if (group.hasColorWheelOddEven) {
                    colorPickers.add(Pair("even", "${group.name}Even"))
                    colorPickers.add(Pair("odd", "${group.name}Odd"))
                }
                ClientGroup(
                    name = group.name,
                    label = group.displayName,
                    isSelectable = group.selectable,
                    colorPickers = colorPickers,
                    scenes = group.scenes.map { scene ->
                        val label = if (scene.name.startsWith(group.name, ignoreCase = true)) {
                            scene.name.substring(group.name.length).trim { it <= ' ' }
                        } else {
                            scene.name
                        }

                        ClientScene(
                            label = label,
                            colors = scene.color?:listOf(),
                            url = "${prefs.baseUrl}:${prefs.port}/v1/scenes/json/control?group=${group.name}&scene=${scene.name}"
                        )
                    }
                )
            }
        )
    }

    private fun createColorPicker(
        id: String
    ): ClientColorPicker? {
        val colorWheel = prefs.stage?.devices?.colorWheelMap?.get(id)
        return if (colorWheel != null) {
            ClientColorPicker(
                id = id,
                updates = colorWheel.devices + colorWheel.updates,
                currentColor = prefs.colorStore[id]?.prefixIfNot("#") ?: "#000000"
            )
        } else {
            null
        }
    }

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

        val finalHexColors = if (hexColors.size == 1) {
            val hc = hexColors.first()
            val csc = currentScene?.colors()?.toMutableList()?:mutableListOf()
            if (storeName?.endsWith("Odd") == true && hexColors.isNotEmpty()) {
                (1 until csc.size step 2).forEach { index ->
                    csc[index] = hc
                }
                csc
            } else if (storeName?.endsWith("Even") == true && hexColors.isNotEmpty()) {
                (0 until csc.size step 2).forEach { index ->
                    csc[index] = hc
                }
                csc
            } else {
                (0 until csc.size).forEach { index ->
                    csc[index] = hc
                }
                csc
            }
        } else {
            hexColors
        }

        val nextScene = prefs.currentScene?.clone()
            ?.let { hybridScene ->
                HybridScene(prefs.stage!!, ids, finalHexColors, gains, turnOn.toString())
                    .fadeableMap()
                    .forEach {
                        hybridScene.putFadeable(it.key, it.value)
                    }
                hybridScene
            }
        if (store) {
            storeScene(nextScene, hexColors, wheelId, storeName)
        }
        log.info("nextScene:$nextScene")

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
        val colors = hexColors.joinToString(",")

        if (storeName != null) {
            prefs.putColor(storeName, colors)
        }

        // update other affected color wheels
        val colorWheel = prefs.stage?.devices?.colorWheelMap
            ?.get(wheelId)
            ?: prefs.stage?.devices?.colorWheelMap?.get(storeName)
        val colorWheels = (colorWheel?.updates ?: listOf()) + (colorWheel?.devices ?: listOf())
        colorWheels.forEach { storeName ->
            prefs.putColor(storeName, colors)
        }

        // update remaining stores
        val remaining = prefs.getDeviceIds().toMutableList()
        remaining.removeAll(colorWheels)
        remaining.forEach { id ->
            prefs.currentScene
                ?.getRgbColor(id)
                ?.also { c ->
                    prefs.putColor(id, c.hex())
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
