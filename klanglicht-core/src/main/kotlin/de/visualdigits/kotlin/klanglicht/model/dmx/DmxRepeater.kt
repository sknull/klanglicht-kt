package de.visualdigits.kotlin.klanglicht.model.dmx

class DmxRepeater(
    val dmxInterface: DMXInterface,
    val dmxFrameTime: Long
) : Thread("DMX Repeater") {

    private var loop = false

    private var running = false

    companion object {

        var dmxRepeater: DmxRepeater? = null

        fun instance(
            dmxInterface: DMXInterface,
            dmxFrameTime: Long
        ): DmxRepeater {
            if (dmxRepeater == null) {
                dmxRepeater = DmxRepeater(dmxInterface, dmxFrameTime / 2)
                dmxRepeater?.start()
            }
            return dmxRepeater!!
        }
    }

    override fun run() {
        running = true
        loop = true
        println("### repeater started")
        while (loop) {
            if (running) {
                dmxInterface.write()
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
