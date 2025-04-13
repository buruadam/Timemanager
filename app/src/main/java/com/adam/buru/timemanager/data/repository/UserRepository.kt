package com.adam.buru.timemanager.data.repository

import com.adam.buru.timemanager.data.model.ErrorResponse
import com.adam.buru.timemanager.data.model.User
import com.adam.buru.timemanager.data.remote.KtorClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

interface UserRepository {
    suspend fun getUserById(id: Int, token: String): User
}

class UserRepositoryImpl(private val api: KtorClient) : UserRepository {

    private companion object {
        private const val BASE_URL = "/api/users"
    }

    override suspend fun getUserById(id: Int, token: String): User {
        return try {
            val response: HttpResponse = api.client.get("$BASE_URL/$id") {
                header("Authorization", "Bearer $token")
            }
            when (response.status) {
                HttpStatusCode.OK -> response.body()
                else -> {
                    val errorResponse: ErrorResponse = response.body()
                    throw Exception(errorResponse.error)
                }
            }

        } catch (e: ConnectTimeoutException) {
            throw Exception("Connection timed out")
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error")
        }
    }
}