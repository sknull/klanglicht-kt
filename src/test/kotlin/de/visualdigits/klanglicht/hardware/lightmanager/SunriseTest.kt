package de.visualdigits.klanglicht.hardware.lightmanager

import org.junit.jupiter.api.Test
import org.shredzone.commons.suncalc.SunTimes
import java.time.LocalDateTime

class SunriseTest {

    @Test
    fun testSunriseAndSunset() {
        val sunTimes = SunTimes.compute()
            .on(LocalDateTime.now())
            .at(53.551, 9.994)
            .execute()
        println(sunTimes.rise)
        println(sunTimes.noon)
        println(sunTimes.set)
    }
}
