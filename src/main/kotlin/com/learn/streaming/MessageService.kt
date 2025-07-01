package com.learn.streaming

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val vectorStore: VectorStore, 
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper
) {

    fun getMessages(prompt: String, limit: Int): Result? {
        val searchRequest = SearchRequest.builder().query(prompt).topK(limit).build()
        val similarDocuments: List<Document>? = vectorStore.similaritySearch(searchRequest)
        if (similarDocuments.isNullOrEmpty()) return null
        val context = similarDocuments.filter { it.isText }.sortedBy { it.score }.mapNotNull { it.text }
        val inputs = context.joinToString(separator = "\n") { it.replace("'", "''") }
        val sql = """
            SELECT pgml.transform(
                task   => '{
                 "task": "summarization",
                 "model": "facebook/bart-large-cnn"
               }'::JSONB, 
                inputs => ARRAY['${inputs}'],
                args => '{"min_new_tokens": 50}'::JSONB
            )
            """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            val summary = rs.getString(1)
            objectMapper.readValue<List<Result>>(
                summary,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java, Result::class.java
                )
            ).firstOrNull()
        }.firstOrNull()
    }
}
