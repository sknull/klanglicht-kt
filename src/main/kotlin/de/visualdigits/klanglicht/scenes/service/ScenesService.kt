package de.visualdigits.klanglicht.scenes.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.hybrid.service.HybridStageService
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMAction
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionAir
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionHybrid
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionPause
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionShelly
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionTwinkly
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionYamahaAvantage
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMScene
import de.visualdigits.klanglicht.hardware.lightmanager.service.LightmanagerService
import de.visualdigits.klanglicht.hardware.shelly.service.ShellyService
import de.visualdigits.klanglicht.hardware.twinkly.service.TwinklyService
import de.visualdigits.klanglicht.hardware.yamahaavantage.service.YamahaAvantageService
import de.visualdigits.kotlin.twinkly.model.device.xmusic.moods.Moods
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ScenesService(
    private val prefs: ApplicationPreferences,
    private val shellyService: ShellyService,
    private val lightmanagerService: LightmanagerService,
    private val hybridStageService: HybridStageService,
    private val yamahaAvantageService: YamahaAvantageService,
    private val twinklyService: TwinklyService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private var previousSceneName: String = ""

    fun executeScene(
        group: String,
        scene: String
    ) {
        val loadScenes = prefs.loadScenes()
        val map = loadScenes.scenesMap[group]
        val get = map?.get(scene)
        get
            ?.also { s ->
                if (scene != previousSceneName || s.repeatable == true) {
                    val execute = if (s.condition != null) {
                        val result = s.condition.evaluate(prefs)
                        log.info("Evaluated condition '${s.condition.name}': $result")
                        result
                    } else {
                        true
                    }
                    if (execute) {
                        log.info("Executing scene '$group - $scene'...")
                        previousSceneName = scene
                        s.actions.forEach { action ->
                            executeAction(action, scene)
                        }
                    } else {
                        log.info("Not executing scene '$group - $scene'...")
                    }
                } else {
                    log.info("Scene '$group - $scene' already set - skipping")
                }
            } ?: also {
            log.info("No scene with name '$group - $scene'")
        }
    }

    private fun executeAction(
        action: LMAction,
        scene: String
    ) {
        log.info("Executing action '${action.name()}'...")
        when (action) {
            is LMActionAir -> lmair(action.sceneIndex ?: -1)
            is LMActionShelly -> shelly(action.ids, action.turnOn == true)
            is LMActionHybrid -> hybrid(action.ids, action.hexColors, action.gains, scene)
            is LMActionYamahaAvantage -> yamahaAvantage(
                action.command ?: "",
                action.program ?: "",
                action.enable == true
            )

            is LMActionTwinkly -> twinkly(action.command, action.moodsIndex, action.effectIndex)
            is LMActionPause -> action.duration?.also { Thread.sleep(it) }
            else -> log.warn("  Unsupported action '${action.name()}'")
        }
    }

    fun saveCustomScene(
        name: String
    ) {
        log.info("Saving scene '$name': ${prefs.currentScene}")
        val scenes = prefs.loadScenes()
        scenes.scenesGroupMap["Custom"]?.scenes?.add(LMScene(
            name = if (name.startsWith("Custom ")) name else "Custom $name",
            color = prefs.currentScene?.fadeables()?.map { it.toRgbColor().web() }?:listOf(),
            actions = listOf(LMActionHybrid(hexColors = prefs.currentScene?.fadeables()?.map { it.toRgbColor().hex() }?:listOf()))
        ))
        prefs.writeScenes(scenes)
    }

    fun deleteCustomScene(
        name: String
    ) {
        log.info("Deleting scene '$name'")
        val scenes = prefs.loadScenes()
        scenes.scenesGroupMap["Custom"]?.also {  g -> g.scenes.find { s -> s.name == name }?.also { sc -> g.scenes.remove(sc) } }
        prefs.writeScenes(scenes)
    }

    fun sceneNames(): Set<String> = prefs.loadScenes().scenesMap.keys

    fun hybrid(ids: List<String>, hexColors: List<String>, gains: List<Double>, sceneName: String? = null) {
        hybridStageService.hexColor(
            sceneName = sceneName,
            ids = ids,
            hexColors = hexColors,
            gains = gains
        )
    }

    fun shelly(ids: List<String>, turnOn: Boolean, transitionDuration: Long? = 2000) {
        shellyService.power(ids = ids, turnOn = turnOn, transitionDuration = transitionDuration)
    }

    fun lmair(sceneIndex: Int) {
        lightmanagerService.controlIndex(index = sceneIndex)
    }

    fun yamahaAvantage(command: String, program: String, enable: Boolean) {
        when (command) {
            "surroundProgram" -> yamahaAvantageService.setSurroundProgram(program = program)
            "setPureDirect" -> yamahaAvantageService.setPureDirect(enable = enable)
        }
    }

    fun twinkly(command: String, moodsIndex: Int, effectIndex: Int) {
        when (command) {
            "on" -> twinklyService.on()
            "off" -> twinklyService.off()
            "musicOn" -> twinklyService.musicOn()
            "musicOff" -> twinklyService.musicOff()
            "moodsEffect" -> Moods.fromIndex(moodsIndex)
                ?.effectFromIndex(effectIndex)
                ?.also { me ->
                    twinklyService.moodsEffect(me)
                }
        }
    }
}
