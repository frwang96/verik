package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

enum class KtRuleType {
    KOTLIN_FILE;

    companion object {
        fun getType(type: String): KtRuleType? {
            return when (type) {
                "kotlinFile" -> KOTLIN_FILE
                else -> null
            }
        }
    }
}