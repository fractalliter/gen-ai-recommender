package com.learn.streaming

import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
class MessageService(val vectorStore: VectorStore, val jdbcTemplate: JdbcTemplate) {
    private val promptText = """
                Answer the following question based on the provided context.
                If the answer is not found in the context, please state that you don't have enough information.

                Context:
                {context}

                Question:
                {query}
                """

    fun getMessages(prompt: String, limit: Int): String? {
        val similarDocuments = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(prompt).topK(limit)
                .build()
        )
        if (similarDocuments == null) return null
        val context: String? = similarDocuments
            .joinToString("\n\n") { it.text.toString() }
        val promptTemplate = PromptTemplate(promptText)
        val render = promptTemplate.render(
            mapOf(
                "context" to context,
                "query" to prompt
            )
        )
        val sql = "SELECT pgml.transform('summarization', ?)"
        return jdbcTemplate.queryForObject(
            sql,
            String::class.java,
            render
        )
    }
}