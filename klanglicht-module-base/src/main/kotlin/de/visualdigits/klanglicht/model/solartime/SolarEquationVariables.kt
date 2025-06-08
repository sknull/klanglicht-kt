package de.visualdigits.klanglicht.model.solartime


/**
 * Intermediate variables used in the sunrise equation
 * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
 */
class SolarEquationVariables(// Julian cycle (number of days since Jan 1st, 2000 12:00).
    val n: Double, // solar mean anomaly
    val m: Double, // ecliptic longitude
    val lambda: Double, // Solar transit (hour angle for solar noon)
    val jtransit: Double, // Declination of the sun
    val delta: Double
)
