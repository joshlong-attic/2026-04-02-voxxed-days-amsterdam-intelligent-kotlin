package com.example.kotlinftw

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.html.body
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.title
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
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.rmi.ServerError
import kotlin.time.Duration.Companion.seconds

@Import(MyBeanRegistrar::class)
@SpringBootApplication
class KotlinftwApplication

fun main(args: Array<String>) {
    runApplication<KotlinftwApplication>(*args)
}

class MyRunner(val name: String = "world") : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        println("hello $name !")
    }

}

class MyBeanRegistrar : BeanRegistrarDsl({

    registerBean<MyRunner>()

    registerBean {
        router {
            GET("/router") {
                ServerResponse.ok().body(mapOf("message" to "Hello Router"))
            }
        }
    }

    for (i in 1..10)
        registerBean<MyRunner>(supplier = { MyRunner("another $i ") })

})


@Controller
@ResponseBody
class HelloController {

    @GetMapping("/hw")
    fun hw(mav: ModelAndView): Map<String, String> {
        return mapOf("message" to "Hello World")
    }

}


@Controller
class DogsController(val dogRepository: DogRepository) {

    @ResponseBody
    @GetMapping("/kotlinx")
    fun kotlinx(): String = createHTMLDocument().html {
        head { title { +"Kotlin HTML" } }
        body {
            dogRepository.findAll().forEach {
                p { +it.name }
            }
        }

    }
        .serialize()

    @GetMapping("/jte.html")
    fun index(model: Model): String {
        model.addAttribute("dogs", dogRepository.findAll())
        return "index"
    }

    @ResponseBody
    @GetMapping("/rest")
    fun rest() = this.dogRepository.findAll()

    @GetMapping("/sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun sse(): Flow<String> {
        return flow {
            dogRepository.findAll().forEach {
                emit("data: $it ${Thread.currentThread()}")
                delay(1.seconds)
            }
        }
    }

}

// look mom, no Lombok!!
data class Dog(@Id val id: Int, val name: String, val description: String)

interface DogRepository : ListCrudRepository<Dog, Int>

