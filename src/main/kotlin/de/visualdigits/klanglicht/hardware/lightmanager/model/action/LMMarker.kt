package de.visualdigits.klanglicht.hardware.lightmanager.model.action


class LMMarker(
    val id: Int,
    val name: String,
    val colorOff: String? = null,
    val colorOn: String? = null,
    val state: Boolean? = null,

    /** Determines whether the button should stay split up (true) or should be consolidated into on toggle button (false).  */
    val separate: Boolean? = null,

    /** Determines to which actor id this marker belongs (if any).  */
    val actorId: String? = null,

    /** Determines the actor state to which this marker belongs (if any).  */
    val markerState: String? = null
)
