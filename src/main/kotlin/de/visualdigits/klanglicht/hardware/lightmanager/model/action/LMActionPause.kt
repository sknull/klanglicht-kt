package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMActionPause(
    val duration: Long? = null
) : LMAction("Pause") {

    override fun toString(): String {
        return "[Pause] $duration"
    }
}

