package com.konigsoftware.decimal

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class UtilitiesTest {
    @Test
    fun `max tests`() {
        assertEquals(KonigDecimal("10.124124"), max(KonigDecimal("10.124124"), KonigDecimal("0.912")))
        assertEquals(KonigDecimal("123.912"), max(KonigDecimal("1.01234"), KonigDecimal("123.912")))
        assertEquals(KonigDecimal("10.124124"), max(KonigDecimal("10.124124"), KonigDecimal("10.124124")))
    }

    @Test
    fun `min tests`() {
        assertEquals(KonigDecimal("0.912"), min(KonigDecimal("10.124124"), KonigDecimal("0.912")))
        assertEquals(KonigDecimal("1.01234"), min(KonigDecimal("1.01234"), KonigDecimal("123.912")))
        assertEquals(KonigDecimal("10.124124"), min(KonigDecimal("10.124124"), KonigDecimal("10.124124")))
    }
}
