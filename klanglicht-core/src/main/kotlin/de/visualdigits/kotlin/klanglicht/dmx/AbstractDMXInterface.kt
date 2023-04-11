package de.visualdigits.kotlin.klanglicht.dmx

import org.apache.commons.lang3.StringUtils


abstract class AbstractDMXInterface : DMXInterface {

    protected val dmxFrame: DmxFrame = DmxFrame()

    override fun toString(): String {
        return dmxFrame.dump()
    }

    override fun repr(): String {
        val lst = ArrayList<String>()
        for (b in dmxFrame.frame()) {
            lst.add(StringUtils.leftPad(Integer.toHexString(b.toInt()), 8, '0'))
        }
        return lst.toString()
    }

    override fun open(portName: String) {}

    override fun close() {}

    override fun read(): ByteArray {
        return dmxFrame.data
    }

    override fun write(dmxFrame: DmxFrame) {}

    override fun setChannel(channel: Int, value: Int) {
        dmxFrame.set(channel, value)
    }

    override fun setData(baseChannel: Int, data: ByteArray) {
        dmxFrame.set(baseChannel, data)
    }

    override fun clearChannels() {
        for (i in 1..511) {
            setChannel(i, 0)
        }
    }

    override fun getChannel(channel: Int): Int {
        return dmxFrame.get(4 + channel)
    }

    override fun clear() {
        initFrame()
        write()
    }

    override fun initFrame() {
        dmxFrame.init()
    }
}

