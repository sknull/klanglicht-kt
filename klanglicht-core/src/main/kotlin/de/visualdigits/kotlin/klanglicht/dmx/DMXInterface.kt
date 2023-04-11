package de.visualdigits.kotlin.klanglicht.dmx


interface DMXInterface {

    /**
     * Opens the given serial port for communication with the physical device.
     *
     * @param portName The port name (i.e. 'COM9'.
     */
    fun open(portName: String)

    /**
     * Closes the current serial port.
     */
    fun close()

    /**
     * Writes the current internal data bytes to the device.
     */
    fun write()

    /**
     * Writes the given data bytes to the device.
     *
     * @param data The frame data to write.
     */
    fun write(data: ByteArray)

    /**
     * Writes the given data bytes to the device.
     *
     * @param dmxFrame The frame data to write.
     */
    fun write(dmxFrame: DmxFrame)

    /**
     * Returns the current frame data.
     */
    fun read(): ByteArray?

    /**
     * Clears all 512 DMX channels in the internal data bytes by setting to 0.
     */
    fun clearChannels()

    /**
     * Sets the given DMX channel in the internal data bytes to the given value.
     *
     * @param channel The channel to set [1..512].
     * @param value The value to set [0..255].
     */
    fun setChannel(channel: Int, value: Int)

    /**
     * Sets the given DMX channel in the internal data bytes to the given value.
     *
     * @param baseChannel The base channel to set [1..512].
     * @param data The bytes to set.
     */
    fun setData(baseChannel: Int, data: ByteArray)

    /**
     * Returns the currently set value of the given DMX channel.<br></br>
     * This method does not actually read from the device but returns
     * the current value form the internal data bytes.
     *
     * @param channel The DMX channel to retrieve [1..512].
     * @return byte
     */
    fun getChannel(channel: Int): Int

    /**
     * Initializes a new DMX frame appropriate for the target device.
     */
    fun initFrame()

    /**
     * Sets all channels to 0.
     */
    fun clear()

    fun repr(): String?

    fun isOpen(): Boolean

    companion object {

        private var dmxInterface: DMXInterface? = null

        fun instance(): DMXInterface = dmxInterface!!

        fun load(type: DMXInterfaceType): DMXInterface {
            if (dmxInterface == null) {
                dmxInterface = when (type) {
                    DMXInterfaceType.Eurolite512Pro -> DMXInterfaceEurolite512Pro()
                    DMXInterfaceType.Dummy -> DMXInterfaceDummy()
                    DMXInterfaceType.Rest -> DMXInterfaceRest()
                }
            }
            return dmxInterface!!
        }
    }
}
