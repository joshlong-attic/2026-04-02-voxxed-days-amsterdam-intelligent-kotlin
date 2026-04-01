package com.example.dogs

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import org.springframework.beans.factory.BeanRegistrarDsl
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.annotation.Id
import org.springframework.data.repository.ListCrudRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import kotlin.time.Duration.Companion.seconds

@Import(DogsBeanRegistrar::class)
@SpringBootApplication
class DogsApplication

fun main(args: Array<String>) {
    runApplication<DogsApplication>(*args)
}


data class Dog(val name: String, @Id val id: Int, val description: String)

interface DogsRepository : ListCrudRepository<Dog, Int>

@Configuration
class RouterConfiguration {

    @Bean
    fun mvcRouter() = router {
        GET("/hello") {
            ServerResponse.ok().body(mapOf("message" to "Hello World"))
        }
    }
}

class DogRunner(val name: String = "World") : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        println("Hello ${this.name}")
    }

}

class DogsBeanRegistrar : BeanRegistrarDsl({
    registerBean<DogRunner>()
    for (i in 1..10) {
        registerBean<DogRunner>(supplier = { DogRunner("Voxxed Days Amsterdam, ${i}!") })
    }
    registerBean<RouterFunction<ServerResponse>> {
        router {
            GET("/beans") {
                ServerResponse.ok().body(mapOf("message" to "Hello Beans"))
            }
        }
    }
})


@Controller
class DogsController(val dogsRepository: DogsRepository) {

    @GetMapping("/hw")
    fun hw(mav: ModelAndView) {
        println(mav.status!!.isError)
        mapOf("message" to "hello world")
    }

    @GetMapping("/jte.html")
    fun index(model: Model): String {
        model.addAttribute("dogs", dogsRepository.findAll())
        return "index"
    }


    @GetMapping("/kotlinx.html")
    fun dogs(model: Model): String {
        model.addAttribute("dogs", dogsRepository.findAll())
        return "dogs"
    }


    @ResponseBody
    @GetMapping("/kotlinx", produces = [MediaType.TEXT_HTML_VALUE])
    fun kotlinx(): String {
        val dogs = dogsRepository.findAll()
        val doc = createHTMLDocument().html {
            head {
                title { +"kotlinx.html + htmx demo" }
            }
            body {
                dogs.forEach {
                    div {
                        id = it.id.toString()
                        b { +it.name }
                        +it.description
                    }
                }
            }
        }
        return doc.serialize()

    }

    @GetMapping("/sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun stream(): kotlinx.coroutines.flow.Flow<String> {
        val dogs = dogsRepository.findAll()
        return flow {
            dogs.forEach {
                emit(it.name)
                delay(2.seconds)
            }
        }
    }

}

