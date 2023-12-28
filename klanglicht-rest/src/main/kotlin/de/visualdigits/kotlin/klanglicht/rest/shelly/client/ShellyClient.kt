package de.visualdigits.kotlin.klanglicht.rest.shelly.client

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.rest.shelly.model.status.Light
import de.visualdigits.kotlin.klanglicht.rest.shelly.model.status.Status
import java.net.URL

object ShellyClient {

    fun power(
        ipAddress: String,
        command: String,
        turnOn: Boolean,
        transitionDuration: Long
    ): String {
        return URL("http://" + ipAddress + "/" + command + "?turn=" + (if (turnOn) "on" else "off") + "&transition=" + transitionDuration + "&").readText()
    }

    fun gain(
        ipAddress: String,
        gain: Int,
        transitionDuration: Long
    ): String {
        return URL("http://$ipAddress/color/0?gain=$gain&transition=$transitionDuration&").readText()
    }

    fun status(
        ipAddress: String
    ): Status {
        val json = URL("http://$ipAddress/status").readText()
        return Status.load(json)
    }

    fun color(
        ipAddress: String,
        rgbColor: RGBColor,
        gain: Float,
        transitionDuration: Long,
        turnOn: Boolean,
    ): Light {
        val json = URL(
            "http://$ipAddress/color/0?" +
                "turn=${if (turnOn) "on" else "off"}&" +
                "red=${rgbColor.red}&" +
                "green=${rgbColor.green}&" +
                "blue=${rgbColor.blue}&" +
                "white=0&" +
                "gain=${(100 * gain).toInt()}&" +
                "transition=" + transitionDuration + "&"
        ).readText()
        return Light.load(json)
    }
}
