package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import java.util.TreeMap
import java.util.regex.Pattern

class LMNamedAttributes(
    s: String,
    vararg attributes: String
) {

    var matched: Boolean = false
    var name: String = ""
    val attributesMap: MutableMap<String, String> = TreeMap()

    companion object {
        val P_PARAMS = Pattern.compile("^([^{]*)\\{?([^}]*)\\}?.*$")
        private const val PATTERN_TEMPLATE = ":([^;}]*)"
    }

    init {
        val matcherParams = P_PARAMS.matcher(s)
        matched = matcherParams.find()
        if (matched) {
            name = matcherParams.group(1).trim { it <= ' ' }
            val params = matcherParams.group(2).trim { it <= ' ' }
            for (attribute in attributes) {
                val pattern = Pattern.compile(attribute + PATTERN_TEMPLATE)
                val matcherSeparate = pattern.matcher(params)
                if (matcherSeparate.find()) {
                    attributesMap[attribute] = matcherSeparate.group(1).trim { it <= ' ' }
                }
            }
        }
    }

    operator fun get(attribute: String): String {
        return attributesMap[attribute]?:""
    }
}
