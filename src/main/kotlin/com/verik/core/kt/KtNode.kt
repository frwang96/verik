package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

sealed class KtNode
data class KtToken(val type: KtTokenType, val text: String): KtNode()
data class KtRule(val type: KtRuleType): KtNode()
