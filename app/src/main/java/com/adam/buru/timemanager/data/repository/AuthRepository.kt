package com.adam.buru.timemanager.data.repository

import com.adam.buru.timemanager.data.model.ErrorResponse
import com.adam.buru.timemanager.data.model.User
import com.adam.buru.timemanager.data.remote.KtorClient
import com.adam.buru.timemanager.data.request.LoginRequest
import com.adam.buru.timemanager.data.response.LoginResponse
import io.ktor.client.call.*
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

interface AuthRepository {
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun register(user: User): String
}

class AuthRepositoryImpl(private val api: KtorClient) : AuthRepository {
    private companion object {
        const val BASE_URL = "/api/auth"
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response: HttpResponse = api.client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            val responseBody: Map<String, String> = response.body()

            if (response.status == HttpStatusCode.OK) {
                LoginResponse(token = responseBody["token"], error = null)
            } else {
                LoginResponse(token = null, error = responseBody["error"])
            }
        } catch (e: ConnectTimeoutException) {
            LoginResponse(token = null, error = "Connection timed out")
        } catch (e: Exception) {
            LoginResponse(token = null, error = e.message)
        }
    }

    override suspend fun register(user: User): String {

        return try {
            val response: HttpResponse = api.client.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            if (response.status != HttpStatusCode.Created) {
                val errorResponse: ErrorResponse = response.body()
                throw Exception(errorResponse.error)
            }
            val responseBody = response.body<Map<String, String>>()
            responseBody["message"] ?: "Registration successful"

        } catch (e: ConnectTimeoutException) {
            throw Exception("Connection timed out")
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error")
        }
    }
}