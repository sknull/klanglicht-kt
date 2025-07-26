package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMMarkers(
    val name: String,
    private val markers: MutableMap<Int, LMMarker> = mutableMapOf()
) {
    fun add(lmMarker: LMMarker) {
        val attributes = LMNamedAttributes(lmMarker.name, "separate", "actorId", "state")
        val marker = if (attributes.matched) {
            val name = if (attributes.name.isNotEmpty()) {
                attributes.name
            } else {
                lmMarker.name
            }
            LMMarker(
                id = lmMarker.id,
                name = name,
                colorOff = lmMarker.colorOff,
                colorOn = lmMarker.colorOn,
                separate = (attributes["separate"]?:"false").toBoolean(),
                actorId = attributes["actorId"],
                markerState = attributes["state"]
            )
        } else {
            lmMarker
        }
        markers[marker.id] = marker
    }

    operator fun get(id: Int): LMMarker? {
        return markers[id]
    }

    fun getByActorId(aid: Int): Set<LMMarker> {
        val markers: MutableList<LMMarker> = mutableListOf()
        val said = aid.toString()
        this.markers.values.forEach { m ->
            if (m.actorId.equals(said)) {
                markers.add(m)
            }
        }
        return markers.toSet()
    }
}
