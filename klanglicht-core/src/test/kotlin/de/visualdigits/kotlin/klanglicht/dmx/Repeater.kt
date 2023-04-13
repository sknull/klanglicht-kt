package de.visualdigits.kotlin.klanglicht.dmx

import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences

class Repeater(
    val preferences: Preferences
) : Thread("DMX Repeater") {

    private val dmxFrameTime: Long = preferences.dmxFrameTime!! / 2

    private var loop = false

    private var running = false

    companion object {

        var theOne: Repeater? = null

        fun instance(preferences: Preferences): Repeater {
            if (theOne == null) {
                theOne = Repeater(preferences)
                theOne?.start()
            }
            return theOne!!
        }
    }

    override fun run() {
        running = true
        loop = true
        println("### repeater started")
        while (loop) {
            if (running) {
                preferences.write()
                sleep(dmxFrameTime)
            }
        }
    }

    fun play() {
        running = true
    }

    fun pause() {
        running = false
    }

    fun end() {
        loop = false
    }
}
