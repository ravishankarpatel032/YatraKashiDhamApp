package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tour_bookings")
data class TourBooking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tourName: String,
    val tourPrice: Int,
    val userName: String,
    val userPhone: String,
    val travelDate: String,
    val personsCount: Int,
    val bookingTimestamp: Long = System.currentTimeMillis()
)
