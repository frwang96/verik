package com.verik.core

import com.charleskorn.kaml.Yaml
import com.verik.core.kt.KtRuleParser
import com.verik.core.vk.VkClassDeclaration
import com.verik.core.vk.VkDeclaration
import com.verik.core.vk.VkModule
import java.io.File

// Copyright (c) 2020 Francis Wang

fun main(args: Array<String>) {
    if (args.size != 1) throw Exception("configuration file expected")
    val configFile = File(args[0])
    if (!configFile.exists()) throw Exception("configuration file not found")

    val config = Yaml.default.parse(Config.serializer(), configFile.readText())
    for (source in config.sources) {
        val src = configFile.resolveSibling(source.src)
        val dst = configFile.resolveSibling(source.dst)
        if (!src.exists()) throw Exception("source file not found")

        val tree = KtRuleParser.parseDeclaration(src.readText())
        val declaration = VkDeclaration(tree)
        val module = VkModule(declaration as VkClassDeclaration)
        val builder = SourceBuilder()
        module.extract().build(builder)
        dst.writeText(builder.toString())
    }
}
