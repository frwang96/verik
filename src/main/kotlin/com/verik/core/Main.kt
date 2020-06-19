package com.verik.core

import com.charleskorn.kaml.Yaml
import com.verik.schema.VkConfig
import java.io.File

// Copyright (c) 2020 Francis Wang

fun main(args: Array<String>) {
    val configFile = File(args[0])
    if (configFile.exists()) {
        val vkConfig = Yaml.default.parse(VkConfig.serializer(), configFile.readText())
        for (root in vkConfig.source_roots.map{configFile.resolveSibling(it)}) {
            println(root.path)
        }
    }
}
