package com.learn.streaming

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class MessageController(
    private val kafkaTemplate: KafkaTemplate<String, Message>,
    private val topic: NewTopic,
    private val messageService: MessageService
) {

    @PostMapping("/ingest")
    fun post(@RequestBody message: Message): ResponseEntity<Message> {
        return try {
            val result = kafkaTemplate.send(topic.name(), message).get()
            ResponseEntity(result.producerRecord.value(), HttpStatus.CREATED)
        } catch (_: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @GetMapping("/recommend")
    fun recommend(
        @RequestParam("prompt") prompt: String,
        @RequestParam("size") size: Int
    ) = ResponseEntity.ok(messageService.getMessages(prompt, size))
}
