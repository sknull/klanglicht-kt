package de.visualdigits.klanglicht.util

import de.visualdigits.klanglicht.model.solartime.Altitude
import de.visualdigits.klanglicht.model.solartime.DayPeriod
import de.visualdigits.klanglicht.model.solartime.JulianConstants
import de.visualdigits.klanglicht.model.solartime.SolarEquationVariables
import de.visualdigits.klanglicht.model.solartime.TimeSpan
import java.lang.Double.isNaN
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Provides methods to determine the sunrise, sunset, civil twilight,
 * nautical twilight, and astronomical twilight times of a given
 * location, or if it is currently day or night at a given location. <br></br>
 * Also provides methods to convert between Gregorian and Julian dates.<br></br>
 * The formulas used by this class are from the Wikipedia articles on Julian Day
 * and Sunrise Equation. <br></br>
 *
 * @see [Julian Day on Wikipedia](http://en.wikipedia.org/wiki/Julian_day)
 *
 * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
 */
object SolarTime {

    private const val EARTH_MAX_TILT_TOWARDS_SUN = 23.439

    private const val DAYS_PER_4000_YEARS = 146097
    private const val DAYS_PER_CENTURY = 36524
    private const val DAYS_PER_4_YEARS = 1461
    private const val DAYS_PER_5_MONTHS = 153

    /**
     * Calculate the astronomical twilight time for the given date and given location.
     *
     * @param dateTime  The day for which to calculate astronomical twilight
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     *
     * @return astronomical dawn or empty if there is no astronomical twilight (e.g. no twilight in Antarctica in December)
     */
    fun calculateAstronomicalDawn(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDawnEvent(dateTime, latitude, longitude, Altitude.ASTRONOMICAL)
    }

    /**
     * Calculate the astronomical twilight time for the given date and given location.
     *
     * @param dateTime  The day for which to calculate astronomical twilight
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     *
     * @return astronomical dusk or empty if there is no astronomical twilight (e.g. no twilight in Antarctica in December)
     */
    fun calculateAstronomicalDusk(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDuskEvent(dateTime, latitude, longitude, Altitude.ASTRONOMICAL)
    }

    /**
     * Calculate the nautical twilight time for the given date and given location.
     *
     * @param dateTime  The day for which to calculate nautical twilight
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     *
     * @return nautical dawn or empty if there is no nautical twilight (e.g. no twilight in Antarctica in December)
     */
    fun calculateNauticalDawn(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDawnEvent(dateTime, latitude, longitude, Altitude.NAUTICAL)
    }

    /**
     * Calculate the nautical twilight time for the given date and given location.
     *
     * @param dateTime  The day for which to calculate nautical twilight
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     *
     * @return nautical dusk or empty if there is no nautical twilight (e.g. no twilight in Antarctica in December)
     */
    fun calculateNauticalDusk(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDuskEvent(dateTime, latitude, longitude, Altitude.NAUTICAL)
    }

    /**
     * Calculate the civil twilight time for the given date and given location.
     *
     * @param dateTime  The day for which to calculate civil twilight
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     *
     * @return civil dawn or empty if there is no civil twilight (e.g. no twilight in Antarctica in December)
     */
    fun calculateCivilDawn(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDawnEvent(dateTime, latitude, longitude, Altitude.CIVIL)
    }

    /**
     * Calculate the civil twilight time for the given date and given location.
     *
     * @param dateTime  The day for which to calculate civil twilight
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     *
     * @return civil dusk or empty if there is no civil twilight (e.g. no twilight in Antarctica in December)
     */
    fun calculateCivilDusk(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDuskEvent(dateTime, latitude, longitude, Altitude.CIVIL)
    }

    /**
     * Calculate the twilight time when to switch off lights.
     *
     * @param dateTime  The day for which to calculate civil twilight
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     *
     * @return civil dawn or empty if there is no civil twilight (e.g. no twilight in Antarctica in December)
     */
    fun calculateLightsOff(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDawnEvent(dateTime, latitude, longitude, Altitude.LIGHTS)
    }

    /**
     * Calculate the twilight time to switch lights on.
     *
     * @param dateTime  The day for which to calculate civil twilight
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     *
     * @return civil dusk or empty if there is no civil twilight (e.g. no twilight in Antarctica in December)
     */
    fun calculateLightsOn(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDuskEvent(dateTime, latitude, longitude, Altitude.LIGHTS)
    }

    fun switchLightsOn(dateTime: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        val lightsOnToday = calculateLightsOn(dateTime, latitude, longitude)
        val lightsOffTomorrow = calculateLightsOff(dateTime.plusDays(1), latitude, longitude)
        return dateTime.isAfter(lightsOnToday) && dateTime.isBefore(lightsOffTomorrow)
    }

    fun calculateSunrise(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDawnEvent(dateTime, latitude, longitude, Altitude.SUNRISE_SUNSET)
    }

    /**
     * Calculate the solar noon time for the given date and given location.
     * This is based on the Wikipedia article on the Sunrise equation.
     *
     * @param dateTime    The day for which to calculate sunrise and sunset
     * @param latitude    the latitude of the location in degrees.
     * @param longitude   the longitude of the location in degrees (West is negative)
     * @return            a Calendar with the time set to solar noon for the given day.
     * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
     */
    fun calculateSolarNoon(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        val solarEquationVariables = calculateSolarEquationVariables(dateTime, longitude)

        // Add a check for Antarctica in June and December (sun always down or up, respectively).
        // In this case, jtransit will be filled in, but we need to check the hour angle omega for
        // sunrise.
        // If there's no sunrise (omega is NaN), there's no solar noon.
        val latitudeRad = Math.toRadians(latitude)

        // Hour angle
        return calculateHourAngle(
            Altitude.SUNRISE_SUNSET,
            latitudeRad,
            solarEquationVariables.delta
        )
            ?.let { _ ->
                // Convert jtransit Gregorian dates, in UTC
                val localTimeNoon = toGregorianDate(solarEquationVariables.jtransit)
                val zone = dateTime.zone
                val noonWithMovedTimeZone = localTimeNoon.withZoneSameInstant(zone)
                noonWithMovedTimeZone
            }
    }

    fun calculateSunset(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        return calculateDuskEvent(dateTime, latitude, longitude, Altitude.SUNRISE_SUNSET)
    }

    fun calculatePreviousSolarMidnight(
        dateTime: ZonedDateTime,
        latitude: Double,
        longitude: Double
    ): ZonedDateTime? {
        val previousDateTime = dateTime.minusDays(1)

        return calculateAstronomicalDusk(previousDateTime, latitude, longitude)
            ?.let { dusk: ZonedDateTime? ->
                calculateAstronomicalDawn(dateTime, latitude, longitude)
                    ?.let { dawn: ZonedDateTime? -> calculateMidpoint(dusk, dawn) }
            }
    }

    fun calculateNextSolarMidnight(dateTime: ZonedDateTime, latitude: Double, longitude: Double): ZonedDateTime? {
        val nextDateTime = dateTime.plusDays(1)

        return calculateAstronomicalDusk(dateTime, latitude, longitude)
            ?.let { dusk: ZonedDateTime? ->
                calculateAstronomicalDawn(nextDateTime, latitude, longitude)
                    ?.let { dawn: ZonedDateTime? -> calculateMidpoint(dusk, dawn) }
            }
    }

    /**
     * @param calendar the datetime for which to determine if it's civil twilight in the given location
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is civil twilight at the given location and the given calendar.
     * This returns true if the given time at the location is between sunset and civil twilight dusk
     * or between civil twilight dawn and sunrise.
     */
    fun isCivilTwilight(calendar: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        return calculateSunset(calendar, latitude, longitude)
            ?.let { sunset: ZonedDateTime? ->
                calculateSunrise(calendar, latitude, longitude)
                    ?.let { sunrise: ZonedDateTime? ->
                        calculateCivilDusk(calendar, latitude, longitude)
                            ?.let { civilDusk: ZonedDateTime? ->
                                calculateCivilDawn(calendar, latitude, longitude)
                                    ?.let { civilDawn: ZonedDateTime? ->
                                        inBetween(
                                            calendar,
                                            TimeSpan(civilDawn, sunrise),
                                            TimeSpan(sunset, civilDusk)
                                        )
                                    }
                            }
                    }
            } ?: false
    }

    /**
     * @param calendar the datetime for which to determine if it's nautical twilight in the given location
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is nautical twilight at the given location and the given calendar.
     * This returns true if the given time at the location is between civil and nautical twilight dusk
     * or between nautical and civil twilight dawn.
     */
    fun isNauticalTwilight(calendar: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        return calculateNauticalDawn(calendar, latitude, longitude)
            ?.let { nauticalDawn: ZonedDateTime? ->
                calculateNauticalDusk(calendar, latitude, longitude)
                    ?.let { nauticalDusk: ZonedDateTime? ->
                        calculateCivilDusk(calendar, latitude, longitude)
                            ?.let { civilDusk: ZonedDateTime? ->
                                calculateCivilDawn(calendar, latitude, longitude)
                                    ?.let { civilDawn: ZonedDateTime? ->
                                        inBetween(
                                            calendar,
                                            TimeSpan(nauticalDawn, civilDawn),
                                            TimeSpan(civilDusk, nauticalDusk)
                                        )
                                    }
                            }
                    }
            } ?: false
    }

    /**
     * @param calendar the datetime for which to determine if it's astronomical twilight in the given location
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is astronomical twilight at the given location and the given calendar.
     * This returns true if the given time at the location is between nautical and astronomical twilight dusk
     * or between astronomical and nautical twilight dawn.
     */
    fun isAstronomicalTwilight(calendar: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        return calculateNauticalDawn(calendar, latitude, longitude)
            ?.let { nauticalDawn: ZonedDateTime? ->
                calculateNauticalDusk(calendar, latitude, longitude)
                    ?.let { nauticalDusk: ZonedDateTime? ->
                        calculateAstronomicalDusk(calendar, latitude, longitude)
                            ?.let { astronomicalDusk: ZonedDateTime? ->
                                calculateAstronomicalDawn(calendar, latitude, longitude)
                                    ?.let { astronomicalDawn: ZonedDateTime? ->
                                        inBetween(
                                            calendar,
                                            TimeSpan(astronomicalDawn, nauticalDawn),
                                            TimeSpan(nauticalDusk, astronomicalDusk)
                                        )
                                    }
                            }
                    }
            } ?: false
    }

    /**
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @param dateTime the given datetime to check for twilight
     * @return true if at the given location and dateTime, it is civil, nautical, or astronomical twilight.
     */
    fun isTwilight(dateTime: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        return (isCivilTwilight(dateTime, latitude, longitude)
                || isNauticalTwilight(dateTime, latitude, longitude)
                || isAstronomicalTwilight(dateTime, latitude, longitude))
    }


    fun getDayPeriod(dateTime: ZonedDateTime, latitude: Double, longitude: Double): DayPeriod {
        val period = when {
            isDay(dateTime, latitude, longitude) -> {
                DayPeriod.DAY
            }
            isCivilTwilight(dateTime, latitude, longitude) -> {
                DayPeriod.CIVIL_TWILIGHT
            }
            isNauticalTwilight(dateTime, latitude, longitude) -> {
                DayPeriod.NAUTICAL_TWILIGHT
            }
            isAstronomicalTwilight(dateTime, latitude, longitude) -> {
                DayPeriod.ASTRONOMICAL_TWILIGHT
            }
            else -> {
                // no need to call isNight here if this is the default case
                DayPeriod.NIGHT
            }
        }

        return period
    }

    fun is24HourDayTime(dateTime: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        val solarEquationVariables = calculateSolarEquationVariables(dateTime, longitude)
        val sunDeclination = solarEquationVariables.delta
        val rads = Math.toRadians(latitude)

        return tan(rads) * tan(sunDeclination) > 1
    }

    fun is24HourNightTime(dateTime: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        val solarEquationVariables = calculateSolarEquationVariables(dateTime, longitude)
        val sunDeclination = solarEquationVariables.delta
        val rads = Math.toRadians(latitude)

        return tan(rads) * tan(sunDeclination) < -1
    }

    /**
     * @param dateTime  a datetime
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is day at the given location and given datetime. This returns
     * true if the given datetime at the location is after the sunrise and
     * before the sunset for that location.
     */
    fun isDay(dateTime: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        return calculateSunrise(dateTime, latitude, longitude)
            ?.let { sunrise: ZonedDateTime? ->
                calculateSunset(dateTime, latitude, longitude)
                    ?.let { sunset: ZonedDateTime? -> dateTime.isAfter(sunrise) && dateTime.isBefore(sunset) }
            }
            ?: is24HourDayTime(dateTime, latitude, longitude)
    }

    /**
     * @param calendar  a datetime
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is night at the given location and datetime. This returns
     * true if the given datetime at the location is after the astronomical twilight dusk and before
     * the astronomical twilight dawn.
     */
    fun isNight(calendar: ZonedDateTime, latitude: Double, longitude: Double): Boolean {
        return calculateAstronomicalDawn(calendar, latitude, longitude)
            ?.let { dawn: ZonedDateTime? ->
                calculateAstronomicalDusk(calendar, latitude, longitude)
                    ?.let { dusk: ZonedDateTime? -> calendar.isBefore(dawn) || calendar.isAfter(dusk) }
            } ?: is24HourNightTime(calendar, latitude, longitude)
    }

    /**
     * Convert a Gregorian calendar date to a Julian date. Accuracy is to the
     * second.
     * <br></br>
     * This is based on the Wikipedia article for Julian day.
     *
     * @param gregorianDate Gregorian date in any time zone.
     * @return the Julian date for the given Gregorian date.
     * @see [Converting to Julian day number on Wikipedia](http://en.wikipedia.org/wiki/Julian_day.Converting_Julian_or_Gregorian_calendar_date_to_Julian_Day_Number)
     */
    fun toJulianDate(gregorianDate: ZonedDateTime): Double {
        // Convert the date to the UTC time zone.
        val utc = ZoneOffset.UTC
        val gregorianDateUTC = gregorianDate.withZoneSameInstant(utc)

        // For the year (Y) astronomical year numbering is used, thus 1 BC is 0,
        // 2 BC is -1, and 4713 BC is -4712.
        val year = gregorianDateUTC.year
        // The months (M) January to December are 1 to 12
        val month = gregorianDateUTC.monthValue
        // D is the day of the month.
        val day = gregorianDateUTC.dayOfMonth
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3

        val julianDay = day + (153 * m + 2) / 5 + 365 * y + (y / 4) - (y / 100) + (y / 400) - 32045
        val hour = gregorianDateUTC.hour.toDouble()
        val minute = gregorianDateUTC.minute.toDouble()
        val second = gregorianDateUTC.second.toDouble()

        return julianDay + (hour - 12) / 24 + minute / 1440 + second / 86400
    }

    /**
     * Convert a Julian date to a Gregorian date. The Gregorian date will be in
     * the local time zone. Accuracy is to the second.
     * <br></br>
     * This is based on the Wikipedia article for Julian day.
     *
     * @param julianDate The date to convert
     * @return a Gregorian date in the local time zone.
     * @see [Converting from Julian day to Gregorian date, on Wikipedia](http://en.wikipedia.org/wiki/Julian_day.Gregorian_calendar_from_Julian_day_number)
     */
    fun toGregorianDate(julianDate: Double): ZonedDateTime {
        // this shifts the epoch back by one half dateTime,
        // to start it at 00:00UTC, instead of 12:00 UTC
        val jd = (julianDate + 0.5).toInt()

        // this shifts the epoch back to astronomical
        // year -4800 instead of the start of the Christian era in year AD 1 of
        // the proleptic Gregorian calendar
        val j = jd + 32044

        val g = j / DAYS_PER_4000_YEARS
        val dg = j % DAYS_PER_4000_YEARS

        val c = ((dg / DAYS_PER_CENTURY + 1) * 3) / 4
        val dc = dg - c * DAYS_PER_CENTURY

        val b = dc / DAYS_PER_4_YEARS
        val db = dc % DAYS_PER_4_YEARS

        val a = ((db / 365 + 1) * 3) / 4
        val da = db - a * 365

        // this is the integer number of full years elapsed since March 1, 4801 BC at 00:00 UTC
        val y = g * 400 + c * 100 + b * 4 + a

        // this is the integer number of full months elapsed since the last March 1 at 00:00 UTC
        val m = (da * 5 + 308) / DAYS_PER_5_MONTHS - 2

        // this is the number of days elapsed since day 1 of the month at 00:00 UTC,
        // including fractions of one day
        val d = da - ((m + 4) * DAYS_PER_5_MONTHS) / 5 + 122

        val year = y - 4800 + (m + 2) / 12
        val month = (m + 2) % 12
        val day = d + 1

        // Apply the fraction of the day in the Julian date to the Gregorian
        // date.
        // Example: dayFraction = 0.717
        val dayFraction = (julianDate + 0.5) - jd

        // Ex: 0.717*24 = 17.208 hours. We truncate to 17 hours.
        val hours = (dayFraction * 24).toInt()
        // Ex: 17.208 - 17 = 0.208 days. 0.208*60 = 12.48 minutes. We truncate
        // to 12 minutes.
        val minutes = ((dayFraction * 24 - hours) * 60.0).toInt()
        // Ex: 17.208*60 - (17*60 + 12) = 1032.48 - 1032 = 0.48 minutes. 0.48*60
        // = 28.8 seconds.
        // We round to 29 seconds.
        val seconds = ((dayFraction * 24 * 3600 - (hours * 3600 + minutes * 60)) + .5).toInt()

        // Create the gregorian date in UTC.
        // through the round we can get 60 seconds but this is invalid by the time API
        // thus adding 60 seconds is equivalent to adding one minute
        val gregorianDateUTC = ZonedDateTime.of(year, month + 1, day, hours, minutes, 0, 0, ZoneOffset.UTC)
            .plusSeconds(seconds.toLong())
        val utc = ZoneId.systemDefault()
        val localZoned = gregorianDateUTC.withZoneSameInstant(utc)

        return localZoned
    }


    private fun calculateHourAngle(altitude: Altitude, latitudeRad: Double, sunDeclination: Double): Double? {
        val omega = acos(
            (sin(Math.toRadians(altitude.degreesBelowHorizon)) - sin(latitudeRad) * sin(sunDeclination)) / (cos(
                latitudeRad
            ) * cos(sunDeclination))
        )
        return if (!isNaN(omega)) {
            omega
        } else {
            null
        }
    }

    private fun shiftDayToZoneOfOtherDay(toBeShifted: ZonedDateTime?, otherDateTime: ZonedDateTime): ZonedDateTime {
        val zone = otherDateTime.zone
        val shifted = toBeShifted!!.withZoneSameInstant(zone)

        return shifted
    }

    private fun calculateJulianSunrise(
        dateTime: ZonedDateTime,
        latitude: Double,
        longitude: Double,
        altitude: Altitude
    ): Double? {
        val maybeSunset = calculateJulianSunset(dateTime, latitude, longitude, altitude)
        return maybeSunset?.let { sunset: Double ->
            val solarEquationVariables =
                calculateSolarEquationVariables(dateTime, longitude)
            // Sunrise
            solarEquationVariables.jtransit - (sunset - solarEquationVariables.jtransit)
        }
    }

    private fun calculateJulianSunset(
        dateTime: ZonedDateTime,
        latitude: Double,
        longitude: Double,
        altitude: Altitude
    ): Double? {
        val solarEquationVariables = calculateSolarEquationVariables(dateTime, longitude)

        val inverted = -longitude
        val latitudeRad = Math.toRadians(latitude)

        // Hour angle
        val maybeOmega = calculateHourAngle(altitude, latitudeRad, solarEquationVariables.delta)

        return maybeOmega?.let { omega ->
            // Sunset
            JulianConstants.JULIAN_DATE_2000_01_01 + JulianConstants.CONST_0009 + ((Math.toDegrees(omega) + inverted) / JulianConstants.CONST_360 + solarEquationVariables.n + (0.0053 * sin(
                solarEquationVariables.m
            )) - 0.0069 * sin(2 * solarEquationVariables.lambda))
        }
    }

    /**
     * Return intermediate variables used for calculating sunrise, sunset, and solar noon.
     *
     * @param dateTime    The day for which to calculate the ecliptic longitude and jtransit
     * @param longitude   the longitude of the location in degrees (West is negative)
     * @return a 2-element array with the ecliptic longitude (lambda) as the first element, and solar transit (jtransit) as the second element
     * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
     */
    private fun calculateSolarEquationVariables(dateTime: ZonedDateTime, longitude: Double): SolarEquationVariables {
        var lt = longitude
        lt = -lt

        // Get the given date as a Julian date.
        val julianDate = toJulianDate(dateTime)

        // Calculate current Julian cycle (number of days since 2000-01-01).
        val nstar = julianDate - JulianConstants.JULIAN_DATE_2000_01_01 - JulianConstants.CONST_0009 - lt / JulianConstants.CONST_360
        val n = Math.round(nstar).toDouble()

        // Approximate solar noon
        val jstar = JulianConstants.JULIAN_DATE_2000_01_01 + JulianConstants.CONST_0009 + (lt / JulianConstants.CONST_360) + n
        // Solar mean anomaly
        val m = Math
            .toRadians(
                (357.5291 + 0.98560028 * (jstar - JulianConstants.JULIAN_DATE_2000_01_01))
                        % JulianConstants.CONST_360
            )

        // Equation of center
        val c = 1.9148 * sin(m) + 0.0200 * sin(2 * m) + 0.0003 * sin(3 * m)

        // Ecliptic longitude
        val lambda = Math
            .toRadians((Math.toDegrees(m) + 102.9372 + c + 180) % JulianConstants.CONST_360)

        // Solar transit (hour angle for solar noon)
        val jtransit = jstar + 0.0053 * sin(m) - 0.0069 * sin(2 * lambda)

        // Declination of the sun.
        val delta = asin(sin(lambda) * sin(Math.toRadians(EARTH_MAX_TILT_TOWARDS_SUN)))


        return SolarEquationVariables(n, m, lambda, jtransit, delta)
    }

    /**
     * sunrise
     * civil dawn
     * nautical dawn
     * astronomical dawn
     */
    private fun calculateDawnEvent(
        dateTime: ZonedDateTime,
        latitude: Double,
        longitude: Double,
        altitude: Altitude
    ): ZonedDateTime? {
        return calculateJulianSunrise(dateTime, latitude, longitude, altitude)
            ?.let { julianDate: Double -> toGregorianDate(julianDate) }
            ?.let { sunrise -> shiftDayToZoneOfOtherDay(sunrise, dateTime) }
    }

    /**
     * Calculate the sunrise and sunset times for the given date and given
     * location. This is based on the Wikipedia article on the Sunrise equation.
     *
     * @param dateTime  The day for which to calculate sunrise and sunset
     * @param latitude  the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return a two-element Gregorian Calendar array. The first element is the
     * sunrise, the second element is the sunset. This will return null if there is no sunrise or sunset. (Ex: no sunrise in Antarctica in June)
     * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
     */
    private fun calculateDuskEvent(
        dateTime: ZonedDateTime,
        latitude: Double,
        longitude: Double,
        altitude: Altitude
    ): ZonedDateTime? {
        return calculateJulianSunset(dateTime, latitude, longitude, altitude)
            ?.let { julianDate: Double -> toGregorianDate(julianDate) }
            ?.let { duskEvent -> shiftDayToZoneOfOtherDay(duskEvent, dateTime) }
    }

    private fun inBetween(now: ZonedDateTime, early: TimeSpan, late: TimeSpan): Boolean {
        return now.isAfter(early.earlier) && now.isBefore(early.later) ||
                now.isAfter(late.earlier) && now.isBefore(late.later)
    }

    private fun calculateMidpoint(first: ZonedDateTime?, second: ZonedDateTime?): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(
            (first!!.toInstant().toEpochMilli() + second!!.toInstant().toEpochMilli()) / 2
        ), first.zone)
    }
}
