package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionPause(
    val duration: Long? = null
) : LMAction() {

    override fun toString(): String {
        return "[Pause] $duration"
    }
}

