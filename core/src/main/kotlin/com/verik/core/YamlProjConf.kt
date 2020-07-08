package com.verik.core

import kotlinx.serialization.Serializable

// Copyright (c) 2020 Francis Wang

@Serializable
data class YamlProjConf(
        val project: String,
        val buildDir: String = "build/verik",
        val labelLineNumbers: Boolean = true,
        val vivado: VivadoYamlProjConf,
        val src: String
)

@Serializable
data class VivadoYamlProjConf(
        val part: String,
        val constraints: String? = null
)
