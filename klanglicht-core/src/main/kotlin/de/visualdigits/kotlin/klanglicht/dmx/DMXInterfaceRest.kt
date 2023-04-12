package de.visualdigits.kotlin.klanglicht.dmx

class DMXInterfaceRest : DMXInterface() {

    override fun toString(): String {
        return repr()
    }

    override fun write() {
        write(dmxFrame.data)
    }

    override fun write(data: ByteArray) {
//        Preferences.instance().getFeignClient().writeBytes(data)
    }

    override fun read(): ByteArray {
//        dmxFrame.data = DmxFrame(Preferences.instance().getFeignClient().readBytes())
        return dmxFrame.data
    }

    override fun isOpen(): Boolean {
        return true
    }
}
