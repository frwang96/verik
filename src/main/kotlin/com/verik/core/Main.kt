package com.verik.core

import com.charleskorn.kaml.Yaml
import com.verik.core.kt.KtTree
import com.verik.core.vk.VkClassDeclaration
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

        val tree = KtTree.parseTopLevelObject(src.readText())
        val classDeclaration = VkClassDeclaration(tree)
        val module = VkModule(classDeclaration)
        val builder = SourceBuilder()
        module.extract().build(builder)
        dst.writeText(builder.toString())
    }
}
