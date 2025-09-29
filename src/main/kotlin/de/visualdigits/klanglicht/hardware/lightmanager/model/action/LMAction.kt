package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)
@JsonSubTypes(
    Type(name = "pause", value = LMActionPause::class),
    Type(name = "url", value = LMActionUrl::class),
    Type(name = "shelly", value = LMActionShelly::class),
    Type(name = "ir", value = LMActionIR::class),
    Type(name = "hybrid", value = LMActionHybrid::class),
    Type(name = "yamahaAvantage", value = LMActionYamahaAvantage::class),
    Type(name = "lmair", value = LMActionAir::class),
)
abstract class LMAction {

    open fun url(): String = ""
}

