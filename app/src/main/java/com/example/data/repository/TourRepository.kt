package com.example.data.repository

import com.example.data.db.BookingDao
import com.example.data.model.SavedPlace
import com.example.data.model.TourBooking
import kotlinx.coroutines.flow.Flow

class TourRepository(private val bookingDao: BookingDao) {
    val allBookings: Flow<List<TourBooking>> = bookingDao.getAllBookings()
    val allSavedPlaces: Flow<List<SavedPlace>> = bookingDao.getAllSavedPlaces()

    suspend fun insertBooking(booking: TourBooking) {
        bookingDao.insertBooking(booking)
    }

    suspend fun deleteBooking(id: Int) {
        bookingDao.deleteBookingById(id)
    }

    suspend fun savePlace(place: SavedPlace) {
        bookingDao.insertSavedPlace(place)
    }

    suspend fun unsavePlace(id: String) {
        bookingDao.deleteSavedPlaceById(id)
    }

    suspend fun isPlaceSaved(id: String): Boolean {
        return bookingDao.getSavedPlaceById(id) != null
    }
}
