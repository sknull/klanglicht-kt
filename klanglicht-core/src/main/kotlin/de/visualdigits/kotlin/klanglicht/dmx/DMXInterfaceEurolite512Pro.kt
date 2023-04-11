package de.visualdigits.kotlin.klanglicht.dmx

import jssc.SerialPort
import jssc.SerialPortException
import org.apache.commons.lang3.StringUtils

class DMXInterfaceEurolite512Pro : AbstractDMXInterface() {

    private var serialPort: SerialPort? = null

    override fun open(portName: String) {
        serialPort = null
        if (!StringUtils.isEmpty(portName)) {
            try {
                serialPort = SerialPort(portName)
                serialPort?.openPort()
                serialPort?.setParams(9600, 8, 1, 0)
            } catch (e: Exception) {
                System.err.println("Could not open DMX port '$portName'")
            }
        }
    }

    override fun close() {
        if (isOpen()) {
            try {
                serialPort?.closePort()
            } catch (e: SerialPortException) {
                throw IllegalStateException("Could not close port", e)
            }
        }
    }

    override fun write() {
        write(dmxFrame)
    }

    override fun write(data: ByteArray) {
        write(DmxFrame(data))
    }

    override fun write(dmxFrame: DmxFrame) {
        if (isOpen()) {
            try {
                val frame = dmxFrame.frame()
                serialPort?.writeBytes(frame)
            } catch (e: SerialPortException) {
                throw IllegalStateException("Could write dmxFrame to port", e)
            }
        } else {
            throw IllegalStateException("Tried to write to non open port")
        }
    }

    override fun isOpen(): Boolean {
        return serialPort != null && serialPort?.isOpened == true
    }
}
