package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.client

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarker
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import de.visualdigits.kotlin.util.get
import de.visualdigits.kotlin.util.post
import org.jsoup.Jsoup
import java.net.URL

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
            .forEach { zoneElem -> zones.add(lightmanagerUrl, markers, zoneElem) }
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
