package com.example.accmarket

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class AccMarketApplication {

    //@Bean
    //fun init(roleManagementService: RoleManagementService) = CommandLineRunner {
    //    roleManagementService.initializeAdminSecret()
    //}
}

fun main(args: Array<String>) {
    val dotenv = dotenv()
    System.setProperty("DB_URL", dotenv["DB_URL"])
    System.setProperty("DB_USER", dotenv["DB_USER"])
    System.setProperty("DB_PASS", dotenv["DB_PASS"])
    System.setProperty("ADMIN_SECRET_DEFAULT", dotenv["ADMIN_SECRET"])
    System.setProperty("SPRING_PORT", dotenv["SPRING_PORT"])
    System.setProperty("EMAIL", dotenv["EMAIL"])
    System.setProperty("EMAIL_CODE_EDU", dotenv["EMAIL_CODE_EDU"])
    System.setProperty("EMAIL_HOST", dotenv["EMAIL_HOST"])
    System.setProperty("EMAIL_PORT", dotenv["EMAIL_PORT"])
    runApplication<AccMarketApplication>(*args)
}
