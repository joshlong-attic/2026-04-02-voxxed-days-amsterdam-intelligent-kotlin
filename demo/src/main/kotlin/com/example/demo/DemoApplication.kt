package com.example.demo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import org.springframework.beans.factory.BeanRegistrarDsl
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.data.annotation.Id
import org.springframework.data.repository.ListCrudRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import kotlin.time.Duration.Companion.seconds

@Import(DogsBeanRegistrar::class)
@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@Controller
@ResponseBody
class SampleController {

    @GetMapping("/hw")
    fun hw(mav: ModelAndView): String {
//        println(mav.status!!.isError)
        return "Hello World!"
    }
}

@Controller
class DogsController(val dogsRepository: DogRepository) {

    // let sleeping dogs lie.. rest!
    @GetMapping("/rest")
    @ResponseBody
    fun rest(): List<Dog> = dogsRepository.findAll()

    @GetMapping("/sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun sse(): Flow<String> {
        return flow {
            for (dog in dogsRepository.findAll()) {
                emit("${dog.name} - ${dog.id} ${Thread.currentThread()}")
                delay(2.seconds)
            }
        }
    }

    @GetMapping("/jte")
    fun jte(model: Model): String {
        model.addAttribute("dogs", dogsRepository.findAll())
        return "index"
    }

    // wait. does this mean that html IS engineering?
    @GetMapping("/kotlinxhtml")
    @ResponseBody
    fun kotlinxhtml(model: Model): String {
        return createHTMLDocument().html {
            head {
                title {
                    +"KotlinX HTML"
                }
            }
            body {
                dogsRepository.findAll().forEach {
                    p { +"${it.name} - ${it.id}" }
                }
            }
        }
            .serialize()
    }

}


// look mom, no Lombok!
data class Dog(@Id val id: Int, val name: String)

interface DogRepository : ListCrudRepository<Dog, Int>

class DogsBeanRegistrar : BeanRegistrarDsl({
    registerBean<MyRunner>()
    registerBean<MyRunner>(supplier = { MyRunner("James") })
    registerBean {
        router {
            GET("/hwr") {
                ServerResponse.ok().body(mapOf("message" to "Hello World!"))
            }
        }
    }
})

//@Component
class MyRunner(private val name: String = "world") : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        println("hello ${name}!")
    }
}


//@Configuration
//class MyConfiguration {
//
//    @Bean
//    fun myRunner() = MyRunner()
//}