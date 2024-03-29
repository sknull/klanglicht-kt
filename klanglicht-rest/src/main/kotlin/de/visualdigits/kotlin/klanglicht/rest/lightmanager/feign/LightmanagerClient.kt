package de.visualdigits.kotlin.klanglicht.rest.lightmanager.feign

import de.visualdigits.kotlin.klanglicht.model.lightmanager.json.LmAirProject
import de.visualdigits.kotlin.klanglicht.model.lightmanager.json.NType
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMAction
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMActor
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMMarker
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMMarkers
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMParams
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMScene
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMScenes
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMZones
import jakarta.annotation.PostConstruct
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LightmanagerClient(
    var lightmanagerUrl: String? = null,
    var client: LightmanagerFeignClient? = null
) {

    @Autowired
    val configHolder: ConfigHolder? = null

    @PostConstruct
    fun initialize() {
        if (lightmanagerUrl?.isEmpty() == true) {
            lightmanagerUrl = configHolder!!.preferences?.getService("lmair")?.url
        }
        client = LightmanagerFeignClient.client(lightmanagerUrl)
    }

    fun params(): LMParams {
        return LMParams.load(client!!.paramsJson()!!)
    }

    fun zones(): LMZones {
        val markers: LMMarkers = markers()
        val document = Jsoup.parse(client!!.html()!!)
        val setUpName = document.select("div[id=mytitle]").first()?.text()?:""
        val zones = LMZones(setUpName)
        document
            .select("div[class=bigBlock]")
            .forEach { zoneElem -> zones.add(lightmanagerUrl!!, markers, zoneElem) }
        return zones
    }

    fun getActorById(actorId: Int): LMActor? {
        var a: LMActor? = null
        val zones: LMZones = zones()
        for (zone in zones.zones) {
            for (actor in zone.actors) {
                if (actor.id == actorId) {
                    a = actor
                    break
                }
            }
        }
        return a
    }

    fun knownActors(): Map<Int, String> {
        val actors: MutableMap<Int, String> = mutableMapOf()
        val zones: LMZones = zones()
        for (zone in zones.zones) {
            for (actor in zone.actors) {
                actors[actor.id!!] = actor.name!!
            }
        }
        return actors
    }

    fun scenes(lmAirProject: LmAirProject? = null): LMScenes {
        val document = Jsoup.parse(client!!.html()!!)
        val setupName = document.select("div[id=mytitle]").first()?.text()
        val scenes = LMScenes(setupName)
        document
            .select("div[id=scenes]")
            .first()
            ?.select("div[class=sbElement]")
            ?.forEach { elem ->
                scenes.add(
                    LMScene(
                        elem.attr("id").substring(1).toInt(),
                        elem.child(0).text()
                    )
                )
            }
        if (lmAirProject != null) {
            scenes.scenes.forEach {
                it.value.forEach { scene ->
                    lmAirProject.scenesMap[scene.id]?.let { s ->
                        val actuatorProperties = s.getActuatorProperties()
                        scene.actions = actuatorProperties.mapNotNull { ap ->
                            when (ap.ntype) {
                                NType.irlan -> {
                                    lmAirProject.actuatorsMap[ap.actorIndex]?.let { actuator ->
                                        val url = if (ap.command == 1) {
                                            actuator.properties?.url2
                                        } else {
                                            actuator.properties?.url
                                        }?:""
                                        LMAction(url)
                                    }
                                }
                                NType.pause ->
                                    LMAction(duration = ap.duration?:0)
                                else -> null
                            }
                        }
                    }
                }
            }
        }
        return scenes
    }

    fun markers(): LMMarkers {
        val markerState: BooleanArray = params().markerState
        val document = Jsoup.parse(client!!.html()!!)
        val setupName = document.select("div[id=mytitle]").first()?.text()
        val markers = LMMarkers()
        markers.name = setupName
        document
            .select("div[id=marker]")
            .first()
            ?.select("div[class=mk mtouch]")
            ?.forEach { elem ->
                val colorOff: String = elem.attr("data-coff")
                val colorOn: String = elem.attr("data-con")
                val id: Int = elem.attr("id").substring(1).toInt()
                markers.add(
                    LMMarker(
                        id = id,
                        name = elem.text(),
                        colorOff = colorOff.ifEmpty { COLOR_OFF },
                        colorOn = colorOn.ifEmpty { COLOR_ON },
                        state = markerState[id],
                        separate = false,
                        actorId = "",
                        markerState = ""
                    )
                )
            }
        return markers
    }

    fun controlScene(sceneId: Int) {
        client!!.controlScene(sceneId)
    }

    fun controlIndex(index: Int) {
        client!!.controlIndex(index)
    }

    companion object {
        const val COLOR_ON = "#FF7676"
        const val COLOR_OFF = "#91FFAA"
    }
}
