package com.learn.streaming

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Result @JsonCreator constructor(
    @JsonProperty("summary_text") val summary: String?)
