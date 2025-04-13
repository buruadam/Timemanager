package com.adam.buru.timemanager.data.repository

import com.adam.buru.timemanager.data.model.ErrorResponse
import com.adam.buru.timemanager.data.model.Priority
import com.adam.buru.timemanager.data.model.Task
import com.adam.buru.timemanager.data.remote.KtorClient
import io.ktor.client.call.*
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

interface TaskRepository {
    suspend fun getAllTasks(token: String): List<Task>
    suspend fun getTaskById(id: Int, token: String): Task
    suspend fun createTask(task: Task, token: String): String
    suspend fun updateTask(id: Int, task: Task, token: String): String
    suspend fun deleteTask(id: Int, token: String): String
    suspend fun getTaskCountByUserId(token: String): Int
    suspend fun getDoneTaskCountByUserId(token: String): Int
    suspend fun setTaskCompletion(id: Int, isCompleted: Boolean, token: String): String
    suspend fun getAllPriorities(token: String): List<Priority>
}

class TaskRepositoryImpl(private val api: KtorClient) : TaskRepository {

    private companion object {
        private const val BASE_URL = "/api/tasks"
    }

    override suspend fun getAllTasks(token: String): List<Task> {
        return try {
            val response: HttpResponse = api.client.get(BASE_URL) {
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

    override suspend fun getTaskById(id: Int, token: String): Task {
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

    override suspend fun createTask(task: Task, token: String): String {
        return try {
            val response: HttpResponse = api.client.post(BASE_URL) {
                contentType(ContentType.Application.Json)
                setBody(task)
                header("Authorization", "Bearer $token")
            }
            if (response.status != HttpStatusCode.Created) {
                val errorResponse: ErrorResponse = response.body()
                throw Exception(errorResponse.error)
            }
            val responseBody = response.body<Map<String, String>>()
            responseBody["message"] ?: "Task created successfully"

        } catch (e: ConnectTimeoutException) {
            throw Exception("Connection timed out")
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateTask(id: Int, task: Task, token: String): String {
        return try {
            val response: HttpResponse = api.client.put("$BASE_URL/$id") {
                contentType(ContentType.Application.Json)
                setBody(task)
                header("Authorization", "Bearer $token")
            }
            if (response.status != HttpStatusCode.OK) {
                val errorResponse: ErrorResponse = response.body()
                throw Exception(errorResponse.error)
            }
            val responseBody = response.body<Map<String, String>>()
            responseBody["message"] ?: "Task updated successfully"

        } catch (e: ConnectTimeoutException) {
            throw Exception("Connection timed out")
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error")
        }
    }

    override suspend fun deleteTask(id: Int, token: String): String {
        return try {
            val response: HttpResponse = api.client.delete("$BASE_URL/$id") {
                header("Authorization", "Bearer $token")
            }

            if (response.status != HttpStatusCode.OK) {
                val errorResponse: ErrorResponse = response.body()
                throw Exception(errorResponse.error)
            }
            val responseBody = response.body<Map<String, String>>()
            responseBody["message"] ?: "Task deleted successfully"

        } catch (e: ConnectTimeoutException) {
            throw Exception("Connection timed out")
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error")
        }
    }

    override suspend fun getTaskCountByUserId(token: String): Int {
        val response: HttpResponse = api.client.get("$BASE_URL/count/all") {
            header("Authorization", "Bearer $token")
        }
        return if (response.status == HttpStatusCode.OK) {
            response.body<Map<String, Int>>()["taskCount"] ?: 0
        } else {
            0
        }
    }

    override suspend fun getDoneTaskCountByUserId(token: String): Int {
        val response: HttpResponse = api.client.get("$BASE_URL/count/done") {
            header("Authorization", "Bearer $token")
        }
        return if (response.status == HttpStatusCode.OK) {
            response.body<Map<String, Int>>()["doneTaskCount"] ?: 0
        } else {
            0
        }
    }

    override suspend fun setTaskCompletion(id: Int, isCompleted: Boolean, token: String): String {
        return try {
            val response: HttpResponse = api.client.put("$BASE_URL/completion/$id") {
                contentType(ContentType.Application.Json)
                setBody(isCompleted)
                header("Authorization", "Bearer $token")
            }
            if (response.status != HttpStatusCode.OK) {
                val errorResponse: ErrorResponse = response.body()
                throw Exception(errorResponse.error)
            }
            val responseBody = response.body<Map<String, String>>()
            responseBody["message"] ?: "Task completion status updated successfully"

        } catch (e: ConnectTimeoutException) {
            throw Exception("Connection timed out")
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error")
        }
    }

    override suspend fun getAllPriorities(token: String): List<Priority> {
        return try {
            val response: HttpResponse = api.client.get("$BASE_URL/priorities") {
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