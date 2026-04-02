package com.example.ai

import org.springframework.ai.chat.client.ChatClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@SpringBootApplication
class AiApplication

fun main(args: Array<String>) {
    runApplication<AiApplication>(*args)
}


@Configuration
class MyConfig {



    @Bean
    fun cc(cc: ChatClient.Builder) = cc.build()
}

@Controller
@ResponseBody
class AssistantController(val cc: ChatClient) {

    @GetMapping("/ask")
    fun ask(@RequestParam question: String) = this.cc
        .prompt()
        .user(question)
        .call()
        .content()

}