package com.example.accmarket.utils.banwords
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class Banword {

    private val bannedPatterns: List<Regex>

    init {
        val mapper = jacksonObjectMapper()
        val files = listOf(
            "jsonLibs/russian.json",
            "jsonLibs/english.json",
            "jsonLibs/hate_speech.json",
            "jsonLibs/drugs.json"
        )

        val words = files.flatMap { path ->
            val resource = ClassPathResource(path)
            val json = resource.inputStream.bufferedReader().readText()
            val dict: Map<String, List<String>> = mapper.readValue(json)
            val roots = dict["roots"] ?: emptyList()
            println("Загружен файл: $path, слов: ${roots.size}")
            roots
        }.distinct()

        println("Всего уникальных запрещённых слов: ${words.size}")

        bannedPatterns = words.map { word ->
            Regex("\\b${Regex.escape(word)}\\w*\\b", RegexOption.IGNORE_CASE)
        }
    }

    private fun normalize(input: String): String {
        return input
            .lowercase()
            .replace("[^a-zа-я0-9]".toRegex(), "")
            .replace("0", "o")
            .replace("1", "i")
            .replace("3", "e")
            .replace("4", "a")
            .replace("5", "s")
            .replace("6", "b")
            .replace("7", "t")
            .replace("8", "b")
            .replace("а", "a")
            .replace("е", "e")
            .replace("ё", "e")
            .replace("о", "o")
            .replace("р", "p")
            .replace("с", "c")
            .replace("у", "y")
            .replace("х", "x")
            .replace("к", "k")
            .replace("м", "m")
            .replace("н", "h")
            .replace("т", "t")
    }

    fun contains(text: String): Boolean {
        val n = normalize(text)
        return bannedPatterns.any { it.containsMatchIn(n) }
    }

    fun find(text: String): List<String> {
        val n = normalize(text)
        return bannedPatterns.flatMap { it.findAll(n).map { m -> m.value } }
    }
}
