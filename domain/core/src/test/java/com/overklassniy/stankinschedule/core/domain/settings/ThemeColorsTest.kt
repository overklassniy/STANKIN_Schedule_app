package com.overklassniy.stankinschedule.core.domain.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeColorsTest {

    private val lightKeys = KeyColors.lightDefault()
    private val darkKeys = KeyColors.darkDefault()

    @Test
    fun `derive возвращает ключевые цвета без изменений`() {
        val colors = ThemeColors.derive(lightKeys)

        assertEquals(lightKeys.primary, colors.primary)
        assertEquals(lightKeys.secondary, colors.secondary)
        assertEquals(lightKeys.tertiary, colors.tertiary)
        assertEquals(lightKeys.error, colors.error)
        assertEquals(lightKeys.background, colors.background)
        assertEquals(lightKeys.surface, colors.surface)
    }

    @Test
    fun `derive на светлом фоне даёт белый текст на тёмных цветах`() {
        val colors = ThemeColors.derive(lightKeys)

        assertEquals("#FFFFFF", colors.onPrimary)
        assertEquals("#FFFFFF", colors.onError)
    }

    @Test
    fun `derive на светлом фоне даёт тёмный текст на светлом фоне`() {
        val colors = ThemeColors.derive(lightKeys)

        assertEquals("#000000", colors.onBackground)
        assertEquals("#000000", colors.onSurface)
    }

    @Test
    fun `derive на тёмном фоне даёт светлый текст`() {
        val colors = ThemeColors.derive(darkKeys)

        assertEquals("#FFFFFF", colors.onBackground)
        assertEquals("#FFFFFF", colors.onSurface)
    }

    @Test
    fun `derive вычисляет контейнерные цвета отличные от базовых`() {
        val light = ThemeColors.derive(lightKeys)
        val dark = ThemeColors.derive(darkKeys)

        assertNotEquals(light.primary, light.primaryContainer)
        assertNotEquals(dark.primary, dark.primaryContainer)
    }

    @Test
    fun `derive присваивает primary в surfaceTint`() {
        val colors = ThemeColors.derive(lightKeys)

        assertEquals(colors.primary, colors.surfaceTint)
    }

    @Test
    fun `derive возвращает валидные HEX-цвета для всех полей`() {
        val colors = ThemeColors.derive(darkKeys)
        val hexPattern = Regex("#[0-9A-F]{6}")

        assertTrue(colors.primary.matches(hexPattern))
        assertTrue(colors.onPrimaryContainer.matches(hexPattern))
        assertTrue(colors.surfaceVariant.matches(hexPattern))
        assertTrue(colors.outline.matches(hexPattern))
        assertTrue(colors.inversePrimary.matches(hexPattern))
    }
}
