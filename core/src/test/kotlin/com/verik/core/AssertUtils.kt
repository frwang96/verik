package com.verik.core

import org.junit.jupiter.api.Assertions.assertEquals

// Copyright (c) 2020 Francis Wang

fun assertStringEquals(expected: Any, actual: Any) {
    assertEquals(expected.toString().trim(), actual.toString().trim())
}
