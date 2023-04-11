package de.visualdigits.kotlin.klanglicht.dmx


class DMXInterfaceDummy() : AbstractDMXInterface() {

    override fun toString(): String {
        return repr()
    }

    override fun open(portName: String) {
        println("### open")
    }

    override fun close() {
        println("### close")
    }

    override fun write() {
        println(toString())
    }

    override fun read(): ByteArray {
        return ByteArray(0)
    }

    override fun write(data: ByteArray) {}

    override fun isOpen(): Boolean = true
}

