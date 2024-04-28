package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.json.RequestType
import org.jsoup.nodes.Element
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Arrays

class LMZones(
    val name: String? = null,
    private val lightmanagerUrl: String
) {

    val zones: MutableList<LMZone> = mutableListOf()

    fun add(markers: LMMarkers, zoneElem: Element) {
        val id: String = zoneElem.attr("id")
        if (id.startsWith("z")) {
            val headElem = zoneElem.select("div[class=bbHead]").first()!!
            val zone = LMZone(
                id = id.substring(1).toInt(),
                name = headElem.select("div[class=bbName]").first()?.text(),
                logo = headElem.select("div[class=bbLogo]").first()?.text(),
                tempChannel = headElem.select("div[class=ztemp]").first()?.attr("data-ch")?.toInt(),
                arrow = headElem.select("div[class=arrow]").first()?.text()
            )
            zoneElem
                .select("div[class=sbElement]")
                .forEach { actorElem -> addActor(markers, zone, actorElem) }
            if (zone.actors.isNotEmpty()) {
                zones.add(zone)
            }
        }
    }

    private fun addActor(markers: LMMarkers, zone: LMZone, actorElem: Element) {
        val actor = LMActor(
            id = actorElem.attr("id").substring(1).toInt()
        )
        val actorOff = actorElem.attr("data-aoff").split(",").map { it.trim() }.filter { it.isNotEmpty() }
        var colorOff = actorOff.getOrElse(1) { "" }
        val actorOn = actorElem.attr("data-aon").split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val colorOn = actorOn.getOrElse(1) { "" }
        var name = actorElem.child(0).text()
        val attributes = LMNamedAttributes(name, "color")
        if (attributes.matched()) {
            name = attributes.name
            colorOff = attributes["color"]
        }
        actor.colorOff = colorOff
        actor.colorOn = colorOn
        actor.name = name
        val dataMarker: String = actorElem.attr("data-marker")
        if (dataMarker.isNotEmpty()) {
            val mid = dataMarker.toInt()
            val marker = markers[mid]
            actor.addMarker(marker)
        } else {
            val actorMarkers = markers.getByActorId(actorElem.attr("id").substring(1).toInt())
            actorMarkers.forEach { marker ->
                actor.addMarker(marker)
            }
        }
        actor.actorOff = actorOff
        actor.actorOn = actorOn
        actor.isDimmer = !actorElem.select("div[class=myslider]").isEmpty()
        actorElem.children()
            .forEach { elem ->
                elem.children()
                    .forEach { child ->
                        addRequest(actor, child)
                    }
            }
        zone.addActor(actor)
    }

    private fun addRequest(actor: LMActor, child: Element) {
        if ("input" == child.tagName()) {
            val rq = LMDefaultRequest()
            val name: String = child.attr("value")
            rq.name = name
            val allParams = setUri(child, rq)
            val typ = getParams(allParams, "typ", 1)
            rq.type = if (typ.isNotEmpty()) RequestType.getByName(typ[0]) else RequestType.UNKNOWN
            val did = getParams(allParams, "did", 1)
            rq.deviceId = determineDeviceId(did)
            val lActorId = getParams(allParams, "aid", 1)
            rq.actorId = if (lActorId.isNotEmpty()) lActorId[0].toInt() else -1
            val acmd = getParams(allParams, "acmd", 1)
            rq.actorCommand = if (acmd.isNotEmpty()) acmd[0].toInt() else -1
            val seq = getParams(allParams, "seq", 1)
            rq.sequence = if (seq.isNotEmpty()) seq[0].toInt() else -1
            val lvl = getParams(allParams, "lvl", 1)
            rq.level = if (lvl.isNotEmpty()) lvl[0].toInt() else -1
            val lSmk = getParams(allParams, "smk", 2)
            rq.smk = if (lSmk.isNotEmpty()) intArrayOf(lSmk[0].toInt(), lSmk[1].toInt()) else IntArray(0)
            val lData = getParams(allParams, "dta", -1)
            rq.data = if (lData.isNotEmpty()) lData.toTypedArray() else arrayOf()
            actor.addRequest(name, rq)
        }
        else if ("a" == child.tagName()) {
            actor.addRequest("cam", LMCamRequest(child.attr("href")))
        }
    }

    private fun determineDeviceId(did: List<String>): Long {
        var deviceId = -1L
        if (!did.isEmpty()) {
            val sDeviceId = did[0]
            deviceId = try {
                sDeviceId.toLong()
            } catch (e: NumberFormatException) {
                sDeviceId.toLong(16)
            }
        }
        return deviceId
    }

    private fun setUri(child: Element, rq: LMDefaultRequest): List<String> {
        var allParams: List<String> = ArrayList()
        var request = ""
        try {
            request = URLDecoder.decode(child.attr("onclick"), StandardCharsets.UTF_8)
            request = request.substring(9, request.length - 2)
            allParams = ArrayList(Arrays.asList(*request.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()))
        } catch (e: UnsupportedEncodingException) {
            // ignore
        }
        val lUri = getParams(allParams, "uri", 1)
        var uri = ""
        try {
            uri = if (!lUri.isEmpty()) {
                lUri[0]
            } else {
                lightmanagerUrl + "?cmd=" + URLEncoder.encode(request, StandardCharsets.UTF_8)
            }
        } catch (e: UnsupportedEncodingException) {
            // ignore
        }
        if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
            uri = "http://$uri"
        }
        rq.uri = uri
        return allParams
    }

    private fun getParams(allParams: List<String>, name: String, numberOfParams: Int): List<String> {
        val params: List<String>
        val index = allParams.indexOf(name)
        params = if (index >= 0) {
            if (numberOfParams > 0) {
                allParams.subList(index + 1, index + 1 + numberOfParams)
            }
            else {
                allParams.subList(index + 1, allParams.size)
            }
        }
        else {
            emptyList()
        }
        return params
    }
}
