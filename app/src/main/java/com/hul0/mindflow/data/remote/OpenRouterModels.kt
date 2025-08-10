// hul0/mindflow/mindflow-420a1f3c6faf5a0e40f158d1d0e60c100c99aee9/app/src/main/java/com/hul0/mindflow/data/remote/OpenRouterModels.kt
package com.hul0.mindflow.data.remote

data class OpenRouterRequest(
    val model: String,
    val messages: List<RequestMessage>
)

data class RequestMessage(
    val role: String,
    val content: String
)

data class OpenRouterResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: ResponseMessage
)

data class ResponseMessage(
    val role: String,
    val content: String
)
