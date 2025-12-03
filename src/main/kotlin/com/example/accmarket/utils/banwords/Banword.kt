package com.example.accmarket.utils.banwords

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class Banword {

    private val bannedWords: List<String>

    init {
        val mapper = jacksonObjectMapper()
        val files = listOf(
            "jsonLibs/russian.json",
            "jsonLibs/english.json",
            "jsonLibs/hate_speech.json",
            "jsonLibs/drugs.json"
        )

        bannedWords = files.flatMap { path ->
            val json = ClassPathResource(path).inputStream.bufferedReader().readText()
            val dict: Map<String, List<String>> = mapper.readValue(json)
            dict["roots"] ?: emptyList()
        }.distinct()
    }

    private fun normalize(input: String): String {
        // Приводим к нижнему регистру и оставляем только буквы
        return input.lowercase().filter { it.isLetter() }
    }

    fun contains(text: String): Boolean {
        val n = normalize(text)
        return bannedWords.any { word -> n.contains(word.lowercase()) }
    }

    fun find(text: String): List<String> {
        val n = normalize(text)
        return bannedWords.filter { word -> n.contains(word.lowercase()) }
    }

}
