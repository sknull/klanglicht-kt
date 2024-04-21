package de.visualdigits.kotlin.klanglicht.rest.lightmanager.service

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarker
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScene
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import de.visualdigits.kotlin.util.get
import de.visualdigits.kotlin.util.post
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.net.URL

@Service
class LightmanagerService(
    @Qualifier("webClientLightmanager") private val webClientLightmanager: WebClient,
) {

    companion object {
        const val COLOR_ON = "#FF7676"
        const val COLOR_OFF = "#91FFAA"
    }

    @Value("\${application.services.lmair.url}")
    private var urlLightmanager: String = ""

    fun params(): LMParams {
        return LMParams.load(URL("$urlLightmanager/params.json").get<String>()?:"")
    }

    fun zones(): LMZones {
        val markers: LMMarkers = markers()
        val document = Jsoup.parse(html())
        val setUpName = document.select("div[id=mytitle]").first()?.text()?:""
        val zones = LMZones(setUpName)
        document
            .select("div[class=bigBlock]")
            .forEach { zoneElem -> zones.add(urlLightmanager, markers, zoneElem) }
        return zones
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

    fun scenes(): LMScenes {
        val document = Jsoup.parse(html())
        val setupName = document.select("div[id=mytitle]").first()?.text()
        val scenes = LMScenes(setupName)
        document
            .select("div[id=scenes]")
            .first()
            ?.select("div[class=sbElement]")
            ?.forEach { elem -> scenes.add(LMScene(name = elem.child(0).text())) }
        return scenes
    }

    fun markers(): LMMarkers {
        val markerState: BooleanArray = params().markerState
        val document = Jsoup.parse(html())
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

    fun controlScene(sceneId: Int): String {
        return URL("$urlLightmanager/control?key=$sceneId").post<String>()?:""
    }

    fun controlIndex(index: Int?): String {
        return URL("$urlLightmanager/control?scene=$index").post<String>()?:""
    }

    fun html(): String {
        return URL("$urlLightmanager/").get<String>()?:""
    }
}
