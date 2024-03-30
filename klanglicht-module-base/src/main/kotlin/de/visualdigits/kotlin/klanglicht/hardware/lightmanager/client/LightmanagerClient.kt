package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.client

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.json.LmAirProject
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.json.NType
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActionIR
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActionPause
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActionUrl
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMActor
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarker
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScene
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import de.visualdigits.kotlin.util.get
import de.visualdigits.kotlin.util.post
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

class LightmanagerClient(
    val lightmanagerUrl: String
) {

    companion object {
        const val COLOR_ON = "#FF7676"
        const val COLOR_OFF = "#91FFAA"
    }

    fun html(): String? {
        return URL("$lightmanagerUrl/").get<String>()
    }

    fun configXml(): String? {
        return URL("$lightmanagerUrl/config.xml").get<String>()
    }

    fun paramsJson(): String? {
        return URL("$lightmanagerUrl/params.json").get<String>()
    }

    fun controlScene(sceneId: Int): String? {
        return URL("$lightmanagerUrl/control?key=$sceneId").post<String>()
    }

    fun controlIndex(index: Int): String? {
        return URL("$lightmanagerUrl/control?scene=$index").post<String>()
    }

    fun params(): LMParams {
        return LMParams.load(paramsJson()!!)
    }

    fun zones(): LMZones {
        val markers: LMMarkers = markers()
        val document = Jsoup.parse(html()!!)
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

    @Suppress("unchecked_cast")
    fun scenes(lmAirProject: LmAirProject? = null): LMScenes {
        val document = Jsoup.parse(html()!!)
        val setupName = document.select("div[id=mytitle]").first()?.text()
        val scenes = LMScenes(setupName)
        document
            .select("div[id=scenes]")
            .first()
            ?.select("div[class=sbElement]")
            ?.forEach { elem ->
                scenes.add(
                    LMScene(
                        id = elem.attr("id").substring(1).toInt(),
                        name = elem.child(0).text()
                    )
                )
            }
        determineActions(lmAirProject, scenes)
        return scenes
    }

    private fun determineActions(
        lmAirProject: LmAirProject?,
        scenes: LMScenes
    ) {
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
                                        }
                                        if (url != null) {
                                            LMActionUrl(
                                                comment = actuator.properties?.comment,
                                                url = url.substringAfter("v1/")
                                            )
                                        } else {
                                            val param = if (ap.command == 1) {
                                                actuator.properties?.paramOff
                                            } else {
                                                actuator.properties?.paramOn
                                            }
                                            if (param != null) {
                                                LMActionIR(comment = actuator.properties?.comment, param = param)
                                            } else {
                                                null
                                            }
                                        }
                                    }
                                }

                                NType.pause -> LMActionPause(duration = ap.duration?.let { d -> 1000 * d } ?: 0)
                                else ->
                                    null
                            }
                        }
                    }
                }
            }
        }
    }

    fun markers(): LMMarkers {
        val markerState: BooleanArray = params().markerState
        val document = Jsoup.parse(html()!!)
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
}
