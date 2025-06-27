package com.learn.streaming

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class MessageService(val vectorStore: VectorStore) {
    fun getMessages(prompt: String, limit: Int): List<Document?>? {
        return vectorStore.similaritySearch(
            SearchRequest.builder().query(prompt).topK(limit).build()
        )
    }
}