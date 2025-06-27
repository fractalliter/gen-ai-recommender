package com.learn.streaming

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.ai.document.Document
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class MessageController(
    val kafkaTemplate: KafkaTemplate<String, Message>,
    val topic: NewTopic,
    val messageService: MessageService) {

    @PostMapping("/ingest")
    fun post(@RequestBody message: Message): ResponseEntity<Message> {
        val future = kafkaTemplate.send(topic.name(), message)
        return try {
            val result = future.get()
            ResponseEntity(result.producerRecord.value(), HttpStatus.CREATED)
        } catch (_: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @GetMapping("/recommend")
    fun recommend(@RequestParam("prompt") prompt: String, @RequestParam("size") size: Int): ResponseEntity<List<Document?>?> {
       val result = messageService.getMessages(prompt, size)
        return ResponseEntity.ok(result)
    }
}