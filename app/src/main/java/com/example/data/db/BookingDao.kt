package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SavedPlace
import com.example.data.model.TourBooking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    // Tour Bookings
    @Query("SELECT * FROM tour_bookings ORDER BY bookingTimestamp DESC")
    fun getAllBookings(): Flow<List<TourBooking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: TourBooking)

    @Query("DELETE FROM tour_bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Int)

    // Saved Places (Bookmarks)
    @Query("SELECT * FROM saved_places ORDER BY timestamp DESC")
    fun getAllSavedPlaces(): Flow<List<SavedPlace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPlace(place: SavedPlace)

    @Query("DELETE FROM saved_places WHERE id = :id")
    suspend fun deleteSavedPlaceById(id: String)

    @Query("SELECT * FROM saved_places WHERE id = :id LIMIT 1")
    suspend fun getSavedPlaceById(id: String): SavedPlace?
}
