package de.visualdigits.kotlin.klanglicht.dmx

import de.visualdigits.kotlin.klanglicht.model.dmx.DMXInterfaceType
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxFrame
import jssc.SerialPort
import jssc.SerialPortException
import org.apache.commons.lang3.StringUtils


open class DMXInterface {

    val dmxFrame = DmxFrame()

    private var serialPort: SerialPort? = null

    companion object {
        fun load(type: DMXInterfaceType): DMXInterface {
            return when (type) {
                DMXInterfaceType.Serial -> DMXInterface()
                DMXInterfaceType.Dummy -> DMXInterfaceDummy()
                DMXInterfaceType.Rest -> DMXInterfaceRest()
            }
        }
    }

    override fun toString(): String {
        return dmxFrame.dump()
    }

    fun repr(): String {
        val lst = ArrayList<String>()
        for (b in dmxFrame.frame()) {
            lst.add(StringUtils.leftPad(Integer.toHexString(b.toInt()), 8, '0'))
        }
        return lst.toString()
    }

    /**
     * Opens the given serial port for communication with the physical device.
     *
     * @param portName The port name (i.e. 'COM9'.
     */
    open fun open(portName: String) {
        if (serialPort == null && !StringUtils.isEmpty(portName)) {
            try {
                serialPort = SerialPort(portName)
                serialPort?.openPort()
                serialPort?.setParams(9600, 8, 1, 0)
            } catch (e: Exception) {
                System.err.println("Could not open DMX port '$portName'")
            }
        }
    }

    open fun isOpen(): Boolean {
        return serialPort?.isOpened == true
    }

    /**
     * Closes the current serial port.
     */
    open fun close() {
        if (isOpen()) {
            try {
                serialPort?.closePort()
            } catch (e: SerialPortException) {
                throw IllegalStateException("Could not close port", e)
            }
        }
    }

    /**
     * This methid does not actually read from the interface as that one is write only.
     * Instead it returns the current bytes frame the frame data.
     */
    open fun read(): ByteArray {
        return dmxFrame.data
    }

    /**
     * Writes the given data bytes to the device.
     * The data bytes must be exactly 512 bytes long.
     *
     * @param data The frame data to write.
     */
    open fun write(data: ByteArray) {
        if (data.size != 512) throw IllegalArgumentException("Data must be exactly 512 bytes long")
        dmxFrame.set(0, data)
        write()
    }

    /**
     * Writes the current state of the internal dmx frame to the interface.
     */
    open fun write() {
        if (isOpen()) {
            try {
                serialPort?.writeBytes(dmxFrame.frame())
            } catch (e: SerialPortException) {
                throw IllegalStateException("Could write dmxFrame to port", e)
            }
        } else {
            throw IllegalStateException("Tried to write to non open port")
        }
    }

    /**
     * Sets the given DMX channel in the internal data bytes to the given value.
     *
     * @param channel The channel to set [1..512].
     * @param value The value to set [0..255].
     */
    fun setChannel(channel: Int, value: Int) {
        dmxFrame.set(channel, value)
    }

    /**
     * Sets the given DMX channel in the internal data bytes to the given value.
     *
     * @param baseChannel The base channel to set [1..512].
     * @param data The bytes to set.
     */
    fun setData(baseChannel: Int, data: ByteArray) {
        dmxFrame.set(baseChannel, data)
    }

    /**
     * Returns the currently set value of the given DMX channel.<br></br>
     * This method does not actually read from the device but returns
     * the current value form the internal data bytes.
     *
     * @param channel The DMX channel to retrieve [1..512].
     * @return byte
     */
    fun getChannel(channel: Int): Int {
        return dmxFrame.get(channel) // todo had offset of 4 - why?
    }

    /**
     * Sets all channels to 0.
     */
    fun clear() {
        dmxFrame.init()
    }
}

