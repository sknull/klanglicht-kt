package de.visualdigits.kotlin.klanglicht.rest.lightmanager.service

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarker
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScene
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.mutableMapOf
import kotlin.collections.set

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

    fun params(): LMParams? {
        return webClientLightmanager
            .get()
            .uri("/params.json")
            .retrieve()
            .bodyToMono(LMParams::class.java)
            .block()
    }

    fun zones(): LMZones {
        val markers: LMMarkers = markers()
        val document = html()?.let { Jsoup.parse(it) }
        val setUpName = document
            ?.select("div[id=mytitle]")
            ?.firstOrNull()
            ?.text()
            ?:""
        val zones = LMZones(setUpName)
        document
            ?.select("div[class=bigBlock]")
            ?.forEach { zoneElem -> zones.add(urlLightmanager, markers, zoneElem) }
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
        val document = html()?.let { Jsoup.parse(it) }
        val setupName = document
            ?.select("div[id=mytitle]")
            ?.firstOrNull()
            ?.text()
            ?:""
        val scenes = LMScenes(setupName)
        document
            ?.select("div[id=scenes]")
            ?.firstOrNull()
            ?.select("div[class=sbElement]")
            ?.forEach { elem ->
                scenes.add(LMScene(name = elem.child(0).text()))
            }
        return scenes
    }

    fun markers(): LMMarkers {
        val markerState = params()?.markerState
        val document = html()?.let { Jsoup.parse(it) }
        val setupName = document
            ?.select("div[id=mytitle]")
            ?.firstOrNull()
            ?.text()
            ?:""
        val markers = LMMarkers()
        markers.name = setupName
        document
            ?.select("div[id=marker]")
            ?.firstOrNull()
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
                        state = markerState?.get(id),
                        separate = false,
                        actorId = "",
                        markerState = ""
                    )
                )
            }
        return markers
    }

    fun controlScene(sceneId: Int): String? {
        return webClientLightmanager
            .post()
            .uri("/control?key=$sceneId")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    fun controlIndex(index: Int?): String? {
        return webClientLightmanager
            .post()
            .uri("/control?scene=$index")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    fun html(): String? {
        return webClientLightmanager
            .get()
            .uri("/")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }
}
