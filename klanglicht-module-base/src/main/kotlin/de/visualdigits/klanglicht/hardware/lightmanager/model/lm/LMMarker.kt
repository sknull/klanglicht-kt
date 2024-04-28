package de.visualdigits.klanglicht.hardware.lightmanager.model.lm


class LMMarker(
    var id: Int,
    var name: String,
    var colorOff: String? = null,
    var colorOn: String? = null,
    var state: Boolean? = null,

    /** Determines whether the button should stay split up (true) or should be consolidated into on toggle button (false).  */
    var separate: Boolean? = null,

    /** Determines to which actor id this marker belongs (if any).  */
    var actorId: String? = null,

    /** Determines the actor state to which this marker belongs (if any).  */
    var markerState: String? = null
)
