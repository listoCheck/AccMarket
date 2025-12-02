package com.example.accmarket.utils.Json
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class JsonbConverter: AttributeConverter<Map<String, Any>, String> {
    private val mapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: Map<String, Any>?): String {
        return attribute?.let { mapper.writeValueAsString(it) } ?: "{}"
    }

    override fun convertToEntityAttribute(dbData: String?): Map<String, Any> {
        return dbData?.let { mapper.readValue(it, Map::class.java) as Map<String, Any> } ?: emptyMap()
    }
}