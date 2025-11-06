package com.example.accmarket.logging

import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.hibernate.boot.jaxb.SourceType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class ControllerLoggingAspect(
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Before("@within(restController)")
    fun logControllerMethod(joinPoint: JoinPoint, restController: RestController) {
        val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        val request = requestAttributes?.request

        val method = joinPoint.signature.name
        val args = joinPoint.args.joinToString { arg ->
            try {
                objectMapper.writeValueAsString(arg)
            } catch (e: Exception) {
                arg.toString()
            }
        }

        val url = request?.requestURI
        val httpMethod = request?.method
        println("[AUTH] incoming request: $httpMethod $url -> $method with args: $args")
        //logger.info("Incoming request: $httpMethod $url -> $method with args: $args")
    }
}
