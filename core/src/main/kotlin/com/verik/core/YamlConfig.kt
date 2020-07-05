package com.verik.core

import kotlinx.serialization.Serializable

// Copyright (c) 2020 Francis Wang

@Serializable
data class YamlConfig(
        val project: String,
        val buildDir: String = "build/verik",
        val labelLineNumbers: Boolean = true,
        val vivado: VivadoYamlConfig,
        val src: String
)

@Serializable
data class VivadoYamlConfig(
        val part: String,
        val constraints: String
)
