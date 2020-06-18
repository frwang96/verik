package com.verik.schema

import kotlinx.serialization.Serializable

// Copyright (c) 2020 Francis Wang

@Serializable
data class VkConfig(val source_roots: List<String>)