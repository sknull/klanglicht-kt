package de.visualdigits.kotlin.klanglicht.hardware.shelly.client

import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.status.Light
import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.status.Status
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.util.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

object ShellyClient {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    fun setPower(
        ipAddress: String,
        turnOn: Boolean? = false,
        command: String = "",
        transitionDuration: Long = 1
    ): String? {
        log.debug("setPower: $ipAddress = $turnOn")
        return URL("http://$ipAddress/$command?turn=${if (turnOn == true) "on" else "off"}&transition=$transitionDuration&").get<String>()
    }

    fun setGain(
        ipAddress: String,
        gain: Int,
        transitionDuration: Long
    ): String? {
        log.debug("setGain: $ipAddress = $gain")
        return URL("http://$ipAddress/color/0?gain=$gain&transition=$transitionDuration&").get<String>()
    }

    fun getStatus(
        ipAddress: String
    ): Status? {
        log.debug("getStatus: $ipAddress")
        return URL("http://$ipAddress/status").get<Status>()
    }

    fun setColor(
        ipAddress: String,
        rgbColor: RGBColor,
        gain: Double,
        transitionDuration: Long = 1, // zero is interpreted as empty which leads to the default of 2000 millis
        turnOn: Boolean? = true,
    ): Light? {
        log.debug("setColor: $ipAddress = ${rgbColor.ansiColor()} [$gain]")
        return URL(
            "http://$ipAddress/color/0?turn=${if (turnOn == true) "on" else "off"}&red=${rgbColor.red}&green=${rgbColor.green}&blue=${rgbColor.blue}&white=0&gain=${(100 * gain).toInt()}&transition=$transitionDuration&"
        ).get<Light>()
    }
}
