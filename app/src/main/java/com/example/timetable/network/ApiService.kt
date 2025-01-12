package com.example.timetable.network

import com.example.timetable.model.ApiResponse
import com.example.timetable.model.Message
import com.example.timetable.model.Schedule
import com.example.timetable.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // === AUTHENTICATION ===
    // Logowanie użytkownika
    @POST("/auth/login")
    suspend fun loginUser(
        @Body user: User
    ): Response<ApiResponse<Any>>

    // Wylogowanie użytkownika
    @POST("/auth/logout")
    suspend fun logoutUser(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Any>>

    // === MESSAGES ===
    // Pobieranie wszystkich wiadomości konkretnego użytkownika
    @GET("/messages/user")
    suspend fun getMessages(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Message>>>

    // Pobieranie jednej wiadomości
    @GET("/messages/{id}")
    suspend fun getMessage(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Message>>

    // === SCHEDULE ===
    // Pobieranie zajęć na konkretny dzień
    @GET("/schedules/{day}")
    suspend fun getSchedulesForDay(
        @Header("Authorization") token: String,
        @Path("day") day: String
    ): Response<ApiResponse<List<Schedule>>>

    // Pobieranie jednego harmonogramu
    @GET("/schedules/{id}")
    suspend fun getSchedule(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Schedule>>
}


