package com.example.accmarket

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class AccMarketApplication

fun main(args: Array<String>) {
    val dotenv = dotenv()
    System.setProperty("DB_URL", dotenv["DB_URL"])
    System.setProperty("DB_USER", dotenv["DB_USER"])
    System.setProperty("DB_PASS", dotenv["DB_PASS"])
    System.setProperty("SPRING_PORT", dotenv["SPRING_PORT"])
    runApplication<AccMarketApplication>(*args)
}
