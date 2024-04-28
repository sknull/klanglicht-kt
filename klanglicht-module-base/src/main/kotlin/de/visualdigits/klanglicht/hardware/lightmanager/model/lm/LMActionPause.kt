package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

class LMActionPause(
    val duration: Long? = null
) : LMAction() {

    override fun toString(): String {
        return "[Pause] $duration"
    }
}

