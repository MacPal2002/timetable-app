package com.example.timetable.database

import androidx.room.*

@Dao
interface TimetableDao {
    // Messages
    @Query("SELECT * FROM messages ORDER BY date DESC, time DESC")
    suspend fun getMessages(): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE id NOT IN (:ids)")
    suspend fun deleteMessagesNotIn(ids: List<String>)

    // Schedules
    @Query("SELECT * FROM schedules WHERE day = :day ORDER BY startTime ASC")
    suspend fun getSchedulesForDay(day: String): List<ScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<ScheduleEntity>)

    @Query("DELETE FROM schedules WHERE day = :day")
    suspend fun deleteSchedulesForDay(day: String)

    @Query("DELETE FROM schedules WHERE day < :today")
    suspend fun deleteOldSchedules(today: String)
}
