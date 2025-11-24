package com.example.accmarket.utils.models.response

import com.example.accmarket.utils.models.response.Response

object ResponseHandler {
    fun userAlreadyExists(): Response {
        return Response(code = 409, message = "This login already exists")
    }

    fun incorrectLoginOrPassword(): Response {
        return Response(code = 400, message = "Incorrect login or password")
    }

    fun userNotFound(): Response {
        return Response(code = 400, message = "User not found")
    }

    fun tokenNotFound(): Response {
        return Response(code = 406, message = "Token not found")
    }

    fun invalidToken(): Response {
        return Response(code = 403, message = "Invalid token")
    }

    fun success(message: String = "Success", body: Any? = null): Response {
        return Response(code = 200, message = message, body = body)
    }

    fun invalidAdminSecret(): Response {
        return Response(code = 403, message = "Invalid admin secret")
    }

    fun insufficientPermissions(): Response {
        return Response(code = 403, message = "Insufficient permissions")
    }
    fun targetUserNotFound(): Response {
        return Response(code = 404, message = "Target user not found")
    }
    fun emailAlreadyExists(): Response {
        return Response(code = 409, message = "Email already exists")
    }
}