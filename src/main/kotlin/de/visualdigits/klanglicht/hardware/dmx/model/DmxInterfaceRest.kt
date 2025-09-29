package de.visualdigits.klanglicht.hardware.dmx.model

class DmxInterfaceRest : DmxInterface() {

    override fun toString(): String {
        return repr()
    }

    override fun write() {
        write(dmxFrame.data)
    }

    override fun write(data: ByteArray) {
        dmxFrame.data = data.clone()
    }

    override fun read(): ByteArray {
        return dmxFrame.data
    }

    override fun isOpen(): Boolean {
        return true
    }
}
