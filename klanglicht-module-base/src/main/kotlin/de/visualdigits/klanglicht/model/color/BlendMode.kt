@file:OptIn(ExperimentalStdlibApi::class)

package de.visualdigits.klanglicht.model.color

enum class BlendMode {

    ADD,
    SUBTRACT,
    AVERAGE,
    REPLACE
    ;

    companion object {
        fun random(): BlendMode {
            return entries.random()
        }
    }
}
