package com.verik.core

import com.charleskorn.kaml.Yaml
import com.verik.core.kt.KtAntlrException
import com.verik.core.kt.KtParseException
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvAssertionException
import com.verik.core.vk.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

// Copyright (c) 2020 Francis Wang

fun main(args: Array<String>) {
    if (args.size != 1) throw Exception("configuration file expected")
    val configFile = File(args[0])
    if (!configFile.exists()) throw Exception("configuration file not found")

    val config = Yaml.default.parse(Config.serializer(), configFile.readText())
    for (source in config.sources) {
        val src = configFile.resolveSibling(source.src)
        val dst = configFile.resolveSibling(source.dst)

        try {
            val file = VkFile(KtRuleParser.parseKotlinFile(src.readText()))
            val builder = SourceBuilder()
            file.extract().build(builder)
            dst.writeText(builder.toString())
        } catch (exception: IOException) {
            printError("ERROR: ${exception.message}")
            printException(exception)
        } catch (exception: KtAntlrException) {
            printError("ERROR: ${exception.linePos} ${exception.message}")
            printException(exception)
        } catch (exception: KtParseException) {
            println("ERROR: ${exception.linePos} ${exception.message}")
            printException(exception)
        } catch (exception: VkParseException) {
            println("ERROR: ${exception.linePos} ${exception.message}")
            printException(exception)
        } catch (exception: VkExtractException) {
            println("ERROR: ${exception.linePos} ${exception.message}")
            printException(exception)
        } catch (exception: VkGrammarException) {
            println("ERROR: grammar assertion failure")
            printException(exception)
        } catch (exception: SvAssertionException) {
            if (exception.message == "") {
                println("ERROR: build assertion failure")
            } else {
                println("ERROR: ${exception.message}")
            }
            printException(exception)
        } catch (exception: Exception) {
            printError("ERROR: ${exception.message}")
            printException(exception)
        }
    }
}

private fun printError(message: String) {
    print("\u001B[31m") // ANSI red
    print(message)
    print("\u001B[0m\n") // ANSI reset
}

private fun printException(exception: Exception) {
    println("${exception::class.simpleName}:")
    for (trace in exception.stackTrace) {
        println("\t$trace")
    }
}
