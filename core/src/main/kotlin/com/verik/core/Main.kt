package com.verik.core

import com.verik.core.kt.KtAntlrException
import com.verik.core.kt.KtParseException
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvAssertionException
import com.verik.core.vk.*
import java.io.File
import kotlin.system.exitProcess

// Copyright (c) 2020 Francis Wang

const val VERSION = "1.0"

fun main(args: Array<String>) {
    val configPath = when (args.size) {
        0 -> "vkconfig.yaml"
        1 -> args[1]
        else -> exitWithError("ERROR: config file expected", null)
    }

    val config = try {
        Config(configPath)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    try {
        val configFile = File(configPath)
        val configCopyFile = config.buildDir.resolve("vkconfig.yaml")
        configFile.copyTo(configCopyFile)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    try {
        val output = getOutput(config)
        config.dstFile.parentFile.mkdirs()
        config.dstFile.writeText(output)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    try {
        val tcl = TclBuilder.build(config)
        config.vivado.tclFile.parentFile.mkdirs()
        config.vivado.tclFile.writeText(tcl)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }
}

private fun getOutput(config: Config): String {
    val txtFile = try {
        config.srcFile.readText()
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    val ktFile = try {
        KtRuleParser.parseKotlinFile(txtFile)
    } catch (exception: KtAntlrException) {
        exitWithError("ERROR: ${exception.linePos} ${exception.message}", exception)
    } catch (exception: KtParseException) {
        exitWithError("ERROR: ${exception.linePos} ${exception.message}", exception)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    val vkFile = try {
        VkFile(ktFile)
    } catch (exception: VkGrammarException) {
        exitWithError("ERROR: grammar assertion failure", exception)
    } catch (exception: VkParseException) {
        exitWithError("ERROR: ${exception.linePos} ${exception.message}", exception)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    val svFile = try {
        vkFile.extract()
    } catch (exception: VkExtractException) {
        exitWithError("ERROR: ${exception.linePos} ${exception.message}", exception)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    return try {
        val builder = SourceBuilder(config)
        svFile.build(builder)
        builder.toString()
    } catch (exception: SvAssertionException) {
        val message = if (exception.message == "") {
            "build assertion failure"
        } else exception.message
        exitWithError("ERROR: $message", exception)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }
}

private fun exitWithError(message: String, exception: java.lang.Exception?): Nothing {
    print("\u001B[31m") // ANSI red
    print(message)
    print("\u001B[0m\n") // ANSI reset
    if (exception != null) {
        println("${exception::class.simpleName}:")
        for (trace in exception.stackTrace) {
            println("\t$trace")
        }
    }
    exitProcess(1)
}