package de.visualdigits.klanglicht.scenes.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMAction
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionAir
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionHybrid
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionPause
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionShelly
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionTwinkly
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMActionYamahaAvantage
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMScene
import de.visualdigits.klanglicht.hybrid.service.HybridStageService
import de.visualdigits.klanglicht.lightmanager.service.LightmanagerService
import de.visualdigits.klanglicht.shelly.service.ShellyService
import de.visualdigits.klanglicht.twinkly.service.TwinklyService
import de.visualdigits.klanglicht.yamahaavantage.service.YamahaAvantageService
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

    fun executeScene(sceneName: String) {
        prefs.loadScenes().scenesMap[sceneName]
            ?.also { scene ->
                if (sceneName != previousSceneName || scene.repeatable) {
                    log.info("Executing scene '$sceneName'...")
                    if (scene.condition?.let { c -> c.evaluate(prefs.stage!!) == true }?:true ) {
                        previousSceneName = sceneName
                        scene.actions.forEach { action ->
                            executeAction(action, sceneName)
                        }
                    } else {
                        log.info("Condition '${scene.condition?.javaClass?.simpleName}' not true - skipping actions")
                    }
                } else {
                    log.info("Scene '$sceneName' already set - skipping")
                }
            } ?: also {
            log.info("No scene with name '$sceneName'")
        }
    }

    private fun executeAction(
        action: LMAction,
        sceneName: String
    ) {
        log.info("  Executing action '$action'...")
        when (action) {
            is LMActionAir -> lmair(action.sceneIndex ?: -1)
            is LMActionShelly -> shelly(action.ids, action.turnOn == true)
            is LMActionHybrid -> hybrid(action.ids, action.hexColors, action.gains, sceneName)
            is LMActionYamahaAvantage -> yamahaAvantage(
                action.command ?: "",
                action.program ?: "",
                action.enable == true
            )

            is LMActionTwinkly -> twinkly(action.command, action.moodsIndex, action.effectIndex)
            is LMActionPause -> action.duration?.also { Thread.sleep(it) }
            else -> log.warn("  Unsupported action '${action.javaClass}'")
        }
    }

    fun saveScene(name: String) {
        log.info("Saving scene '$name': ${prefs.currentScene}")
        val scenes = prefs.loadScenes()
        scenes.scenes["Custom"]?.scenes?.add(LMScene(
            name = if (name.startsWith("Custom ")) name else "Custom $name",
            color = prefs.currentScene?.fadeables()?.map { it.toRgbColor().web() }?:listOf(),
            actions = listOf(LMActionHybrid(hexColors = prefs.currentScene?.fadeables()?.map { it.toRgbColor().hex() }?:listOf()))
        ))
        prefs.writeScenes(scenes)
    }

    fun deleteScene(name: String) {
        log.info("Deleting scene '$name'")
        val scenes = prefs.loadScenes()
        scenes.scenes["Custom"]?.also {  g -> g.scenes.find { s -> s.name == name }?.also { sc -> g.scenes.remove(sc) } }
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
            "moodsEffect" -> Moods.fromIndex(moodsIndex)?.effectFromIndex(effectIndex)?.also { me -> twinklyService.moodsEffect(me) }
        }
    }
}
