package de.visualdigits.kotlin.klanglicht.model.color


data class SimpleColor(
    val blue: Int = 0,
    val green: Int = 0,
    val red: Int = 0,
    val white: Int = 0,
    val amber: Int = 0
) : Color {
    inline fun <reified T : RGBColor> color(): T {
        return when (T::class) {
            RGBWAColor::class ->  RGBWAColor(red, green, blue, white, amber) as T
            RGBWColor::class -> RGBWColor(red, green, blue, white) as T
            RGBAColor::class -> RGBAColor(red, green, blue, amber) as T
            else -> RGBColor(red, green, blue) as T
        }
    }

    override fun toRGB(): RGBColor {
        return color<RGBColor>()
    }

    inline fun <reified T : Color> mix(other: Color, factor: Double): T {
        return toRGB().mix(other, factor)
    }

    override fun value(): Long {
        return toRGB().value()
    }

    override fun hex(): String {
        return toRGB().hex()
    }

    override fun web(): String {
        return toRGB().web()
    }

    override fun ansiColor(): String {
        return toRGB().ansiColor()
    }

    override fun toHSV(): HSVColor {
        return toRGB().toHSV()
    }

    override fun toRGBW(): RGBWColor {
        return toRGB().toRGBW()
    }

    override fun toRGBA(): RGBAColor {
        return toRGB().toRGBA()
    }
}
