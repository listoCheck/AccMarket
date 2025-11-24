package com.example.accmarket.utils.mail

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender

) {
    @Value("\${EMAIL}")
    private lateinit var emailFrom: String

    @Async
    fun sendEmail(to: String, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.from = emailFrom
        message.setTo(to)
        message.subject = subject
        message.text = text

        //println("Отправка письма на $to с темой '$subject'")
        //try {
        //    mailSender.send(message)
        //    println("Письмо успешно отправлено")
        //} catch (e: Exception) {
        //    println("Ошибка при отправке письма: ${e.message}")
        //    e.printStackTrace()
        //}
    }

}
