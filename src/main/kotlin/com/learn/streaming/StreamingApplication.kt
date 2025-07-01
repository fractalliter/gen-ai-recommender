package com.learn.streaming

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.KafkaListener


@SpringBootApplication
class StreamingApplication(val vectorStore: VectorStore) {

    @KafkaListener(
        topics = ["\${spring.kafka.ingest-topic}"],
        groupId = "group_id",
        containerFactory = "concurrentKafkaListenerContainerFactory"
    )
    fun listen(message: Message) {
        vectorStore.add(
            listOf(
                Document(
                    message.message,
                    mapOf("subject" to message.subject, "items" to message.items)
                )
            )
        )
    }

}

fun main(args: Array<String>) {
    runApplication<StreamingApplication>(*args)
}
