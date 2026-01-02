package de.visualdigits.klanglicht.hardware.harmony.model.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

class HarmonyConfig {

    @JsonProperty("activity")
    var activities: MutableList<Activity> = ArrayList()

    @JsonProperty("device")
    var devices: MutableList<Device> = mutableListOf()

    var content: MutableMap<String?, String> = mutableMapOf()

    var global: Global? = null

    fun toJson(): String? {
        try {
            return ObjectMapper().writer(DefaultPrettyPrinter()).writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            throw RuntimeException("Error serializing config to json", e)
        }
    }

    val deviceLabels: MutableMap<Int?, String?>
        get() {
            val results: MutableMap<Int?, String?> = HashMap()
            for (device in devices) {
                results[device.id] = device.label
            }
            return results
        }

    fun getActivityById(result: Int?): Activity? {
        for (activity in activities) {
            if (activity.id == result) return activity
        }
        return null
    }

    fun getActivityByName(label: String?): Activity? {
        for (activity in activities) {
            if (activity.label == label) return activity
        }
        return null
    }

    fun getDeviceByName(label: String?): Device? {
        for (device in devices) {
            if (device.label == label) return device
        }
        return null
    }

    companion object {
        fun parse(config: String?): HarmonyConfig {
            try {
                return ObjectMapper().readValue(config, HarmonyConfig::class.java)
            } catch (e: IOException) {
                throw RuntimeException("Error parsing config from json", e)
            }
        }
    }
}
