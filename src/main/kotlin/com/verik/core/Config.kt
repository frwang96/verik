package com.verik.core

import kotlinx.serialization.Serializable

// Copyright (c) 2020 Francis Wang

@Serializable
data class Config(val sources: List<ConfigSources>)

@Serializable
data class ConfigSources(val src: String, val dst: String)