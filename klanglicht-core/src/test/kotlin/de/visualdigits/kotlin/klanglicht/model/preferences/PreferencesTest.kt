package de.visualdigits.kotlin.klanglicht.model.preferences

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

internal class PreferencesTest {

    @Test
    fun testReadModel() {
        Preferences.instance(
            klanglichtDir = File(ClassLoader.getSystemResource(".klanglicht").toURI())
        )

        assertNotNull(Preferences.instance())
    }
}
