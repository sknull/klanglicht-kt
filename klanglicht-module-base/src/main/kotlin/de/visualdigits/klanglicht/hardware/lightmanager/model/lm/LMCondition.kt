package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.visualdigits.klanglicht.model.preferences.Preferences

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)
@JsonSubTypes(
    Type(name = "night", value = LMConditionNight::class),
)
abstract class LMCondition {

    abstract fun evaluate(prefs: Preferences): Boolean
}
