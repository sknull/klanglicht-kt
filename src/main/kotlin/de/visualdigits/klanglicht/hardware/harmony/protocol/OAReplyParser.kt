package de.visualdigits.klanglicht.hardware.harmony.protocol

import org.jivesoftware.smack.packet.IQ
import java.util.regex.Pattern

abstract class OAReplyParser {
    
    companion object {
        
        val validResponses = setOf(
            "100",
            "200",
            "506", // Bluetooth not connected
            "566" // Command not found for device, recoverable
        )

        val kvRE: Pattern = Pattern.compile("(.*?)=(.*)")

        fun parseKeyValuePairs(statusCode: String?, errorString: String?, contents: String?): Map<String, Any?> {
            val params = mutableMapOf<String, Any?>(
                "statusCode" to statusCode,
                "errorString" to errorString
            )
            for (pair in contents?.split(":")?:listOf()) {
                val matcher = kvRE.matcher(pair);
                if (!matcher.matches()) {
                    continue
                    // throw new AuthFailedException(format("failed to parse element in auth response: %s", pair));
                }

                val value = matcher.group(2);
                val valueObj = if (value.startsWith("{")) {
                    parsePseudoJson(value);
                } else {
                    value;
                }
                params[matcher.group(1)] = valueObj;
            }

            return params;
        }

        fun parsePseudoJson(value:String): Map<String, Any?> {
            val params = mutableMapOf<String, Any?>()
            val value = value.substring(1, value.length - 1);
            for (pair in value.split(", ?")) {
                val matcher = kvRE.matcher(pair);
                if (!matcher.matches()) {
                    throw RuntimeException("failed to parse element in auth response: $value");
                }
                params[matcher.group(1)] = parsePseudoJsonValue(matcher.group(2));
            }
            return params;
        }

        fun  parsePseudoJsonValue(value: String): Any? {
            return when (value[0]) {
                '{' -> parsePseudoJsonValue(value)
                '"' -> value.substring(1, value.length - 1)
                '\'' -> value.substring(1, value.length - 1)
                else -> {
                    try {
                        value.toLong()
                    } catch (e: NumberFormatException) {
                        null
                    }
                    try {
                        value.toDouble()
                    } catch (e: NumberFormatException) {
                        null
                    }
                }
            }
        }

    }

    abstract fun parseReplyContents(statusCode: String?, errorString: String?, contents: String?): IQ?

    open fun validResponseCode(code: String?): Boolean {
        return validResponses.contains(code)
    }
}