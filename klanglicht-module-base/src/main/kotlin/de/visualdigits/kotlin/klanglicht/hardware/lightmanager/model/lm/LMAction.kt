package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

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
    Type(name = "ir", value = LMActionIR::class),
)
abstract class LMAction(
    val comment: String? = null
)
