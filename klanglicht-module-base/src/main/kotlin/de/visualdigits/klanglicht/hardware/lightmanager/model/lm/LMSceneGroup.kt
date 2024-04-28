package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

class LMSceneGroup(
    val name: String = "",
    val hasColorWheel: Boolean = false,
    val colorWheelOddEven: Boolean = false,
    val scenes: MutableList<LMScene> = mutableListOf()
)
