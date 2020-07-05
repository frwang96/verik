package com.verik.core

import kotlinx.serialization.Serializable

// Copyright (c) 2020 Francis Wang

@Serializable
data class YamlConfig(val vivado: YamlConfigVivado, val src: String)

@Serializable
data class YamlConfigVivado(val part: String, val constraints: String)
