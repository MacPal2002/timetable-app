package com.example.timetable.database

import androidx.room.*

@Dao
interface TimetableDao {
    // Messages
    @Query("SELECT * FROM messages ORDER BY date DESC, time DESC")
    suspend fun getMessages(): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    // Schedules
    @Query("SELECT * FROM schedules WHERE day = :day ORDER BY startTime ASC")
    suspend fun getSchedulesForDay(day: String): List<ScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<ScheduleEntity>)
}
