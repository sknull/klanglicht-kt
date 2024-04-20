package de.visualdigits.kotlin.klanglicht.model.dmx

import org.slf4j.Logger
import org.slf4j.LoggerFactory


class DmxInterfaceDummy : DmxInterface() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun toString(): String {
        return repr()
    }

    override fun open(portName: String) {
        log.debug("### open")
    }

    override fun close() {
        log.debug("### close")
    }

    override fun write() {
        log.debug("### write frame: ${dmxFrame.dump()}")
    }

    override fun read(): ByteArray {
        return ByteArray(0)
    }

    override fun write(data: ByteArray) {}

    override fun isOpen(): Boolean = true
}

