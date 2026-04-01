package com.example.demo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.repository.ListCrudRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import kotlin.time.Duration.Companion.seconds

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}


@Controller
@ResponseBody
class DemoController {

    val log = LoggerFactory.getLogger(DemoController::class.java)

    @GetMapping("/hi")
    suspend fun hi(): String = withContext(Dispatchers.IO ) {
        log.info("request incoming")
        delay(5.seconds)
        log.info("response outbound")
        "hi"
    }

    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun stream(): Flow<String> {
        val uid = UUID.randomUUID()
        return flow {

            repeat(5) {
                val msg = "${uid}: hi $it on ${ Thread.currentThread().name }"
                println(msg)
                emit(msg)
                delay(1.seconds)
            }
        }
    }
}


data class Dog(@Id val id: Int, val name: String, val description: String)

interface DogRepository : ListCrudRepository<Dog, Int> {}