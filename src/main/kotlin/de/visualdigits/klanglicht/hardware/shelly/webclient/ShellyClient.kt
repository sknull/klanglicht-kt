package de.visualdigits.klanglicht.hardware.shelly.webclient

import de.visualdigits.klanglicht.hardware.shelly.model.status.Light
import de.visualdigits.klanglicht.hardware.shelly.model.status.Status
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import de.visualdigits.kotlin.util.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

object ShellyClient {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    fun setPower(
        ipAddress: String,
        turnOn: Boolean? = false,
        command: String = "",
        transitionDuration: Long = 1
    ): String? {
        log.debug("setPower: $ipAddress = $turnOn")

        return URI("http://$ipAddress/$command?turn=${if (turnOn == true) "on" else "off"}&transition=$transitionDuration&")
            .toURL()
            .get(clazz = String::class.java)
    }

    fun setGain(
        ipAddress: String,
        gain: Int,
        transitionDuration: Long
    ): String? {
        log.debug("setGain: $ipAddress = $gain")

        return URI("http://$ipAddress/color/0?gain=$gain&transition=$transitionDuration&")
            .toURL()
            .get(clazz = String::class.java)
    }

    fun getStatus(
        ipAddress: String
    ): Status? {
        log.debug("getStatus: $ipAddress")

        return URI("http://$ipAddress/status")
            .toURL()
            .get(clazz = Status::class.java)
    }

    fun setColor(
        ipAddress: String,
        rgbColor: RGBColor,
        gain: Double,
        transitionDuration: Long = 1, // zero is interpreted as empty which leads to the default of 2000 millis
        turnOn: Boolean? = true,
    ): Light? {
        log.debug("setColor: $ipAddress = ${rgbColor.ansiColor()} [$gain]")

        return URI("http://$ipAddress/color/0?turn=${if (turnOn == true) "on" else "off"}&red=${rgbColor.red}&green=${rgbColor.green}&blue=${rgbColor.blue}&white=0&gain=${(100 * gain).toInt()}&transition=$transitionDuration&")
            .toURL()
            .get(clazz = Light::class.java)
    }
}
