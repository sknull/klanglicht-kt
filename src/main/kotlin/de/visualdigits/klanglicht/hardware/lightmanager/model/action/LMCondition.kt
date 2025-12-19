package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.visualdigits.klanglicht.configuration.ApplicationPreferences

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "name"
)
@JsonSubTypes(
    Type(name = "night", value = LMConditionNight::class),
    Type(name = "allBlack", value = LMConditionAllBlack::class),
)
abstract class LMCondition(
    val name: String
) {

    abstract fun evaluate(prefs: ApplicationPreferences): Boolean
}
