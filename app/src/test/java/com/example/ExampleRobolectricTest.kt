package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SaveData
import com.example.data.SaveManager
import com.example.model.Currencies
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Сердце Бездны", appName)
    }

    @Test
    fun `test save manager persistence and export import`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val saveManager = SaveManager(context)

        val customData = SaveData(
            currencies = Currencies(echoes = 12345.0, shards = 42.0),
            activeBiomeId = 2
        )

        val saved = saveManager.saveGame(customData)
        assertTrue(saved)

        val loaded = saveManager.loadGame()
        assertEquals(12345.0, loaded.currencies.echoes, 0.001)
        assertEquals(42.0, loaded.currencies.shards, 0.001)
        assertEquals(2, loaded.activeBiomeId)

        // Test export & import string
        val exportStr = saveManager.exportSaveString(loaded)
        assertNotNull(exportStr)
        assertTrue(exportStr.isNotEmpty())

        val imported = saveManager.importSaveString(exportStr)
        assertNotNull(imported)
        assertEquals(12345.0, imported!!.currencies.echoes, 0.001)
        assertEquals(42.0, imported.currencies.shards, 0.001)
    }
}
