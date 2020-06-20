package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

enum class KtTokenType {
    EOF, NL;

    companion object {
        fun getType(type: String): KtTokenType? {
            return when (type) {
                "EOF" -> EOF
                "NL" -> NL
                else -> null
            }
        }
    }
}