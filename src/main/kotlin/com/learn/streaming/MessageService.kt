package com.learn.streaming

import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
class MessageService(val vectorStore: VectorStore, val jdbcTemplate: JdbcTemplate) {

    fun getMessages(prompt: String, limit: Int): String? {
         val searchRequest = SearchRequest.builder()
            .query(prompt).topK(limit)
            .build()
        val similarDocuments: List<Document>? = vectorStore.similaritySearch(searchRequest)
        if (similarDocuments.isNullOrEmpty()) return null
        val context = similarDocuments.filter { it.isText }.sortedBy { it.score }.mapNotNull { it.text }
        val sql = """
            SELECT pgml.transform(
                task   => '{
                 "task": "summarization",
                 "model": "facebook/bart-large-cnn"
               }'::JSONB, 
                inputs => ARRAY${context.joinToString(prefix = "['", postfix = "']", separator = "', '"){ it.replace("'", "''") }.trimIndent()},
                args => '{"max_new_tokens": 25}'::JSONB
            )
            """.trimIndent()
        return jdbcTemplate.queryForObject(sql, String::class.java)
    }
}
