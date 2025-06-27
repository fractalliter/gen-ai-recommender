package com.learn.streaming

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Message @JsonCreator constructor(
    @JsonProperty("message") val message: String,
    @JsonProperty("subject") val subject: String,
    @JsonProperty("items") val items: Int
)
