package com.overklassniy.stankinschedule.core.domain.ext

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionExtentionsTest {

    @Test
    fun `более новая версия с большим патчем распознаётся как новая`() {
        assertTrue("3.3.0" isOlderVersionThan "3.3.1")
    }

    @Test
    fun `более новая версия с большим минором распознаётся как новая`() {
        assertTrue("3.3.1" isOlderVersionThan "3.4.0")
    }

    @Test
    fun `более новая версия с большим мажором распознаётся как новая`() {
        assertTrue("3.9.9" isOlderVersionThan "4.0.0")
    }

    @Test
    fun `одинаковые версии не считаются новой`() {
        assertFalse("3.3.1" isOlderVersionThan "3.3.1")
    }

    @Test
    fun `старая версия не считается новой`() {
        assertFalse("3.3.2" isOlderVersionThan "3.3.1")
    }

    @Test
    fun `недостающие компоненты считаются нулём`() {
        assertTrue("3.3" isOlderVersionThan "3.3.1")
        assertFalse("3.3.0" isOlderVersionThan "3.3")
    }

    @Test
    fun `версия с суффиксом старее версии без суффикса`() {
        // Компонент "1-beta" нечисловой и отбрасывается: 3.3.1-beta -> 3.3.0 < 3.3.1
        assertTrue("3.3.1-beta" isOlderVersionThan "3.3.1")
    }
}
