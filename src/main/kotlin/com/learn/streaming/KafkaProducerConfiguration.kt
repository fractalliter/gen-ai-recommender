package com.learn.streaming

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfiguration(@Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String) {

    @Bean
    fun producerFactory(): ProducerFactory<String, Message> {
        return DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringSerializer",
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to "org.springframework.kafka.support.serializer.JsonSerializer"
            ),
        )
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Message> {
        return KafkaTemplate(producerFactory())
    }
}