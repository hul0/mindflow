package com.hul0.mindflow.data

import android.content.Context
import android.net.Uri
import com.hul0.mindflow.model.UserProfile
import com.hul0.mindflow.utils.CsvHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository for managing UserProfile data.
 * It abstracts the data source and provides a clean API for the ViewModel.
 *
 * @param userProfileDao The DAO for user profile data.
 */
class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    /**
     * Gets the user profile as a Flow.
     * @return A Flow emitting the UserProfile.
     */
    fun getProfile(): Flow<UserProfile?> = userProfileDao.getProfile()

    /**
     * Inserts or updates the user profile.
     * @param userProfile The profile to save.
     */
    suspend fun saveProfile(userProfile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(userProfile)
    }

    /**
     * Exports the user profile data to a CSV file.
     * @param context The application context.
     * @param uri The URI where the CSV file will be saved.
     * @param userProfile The current user profile from the ViewModel state to be exported.
     */
    suspend fun exportProfileToCsv(context: Context, uri: Uri, userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            CsvHelper.exportToCsv(context, uri, userProfile)
        }
    }

    /**
     * Imports user profile data from a CSV file.
     * @param context The application context.
     * @param uri The URI of the CSV file to import.
     */
    suspend fun importProfileFromCsv(context: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            val profile = CsvHelper.importFromCsv(context, uri)
            profile?.let {
                // Save the imported profile to the database.
                // The UI will update automatically via the Flow in the ViewModel.
                userProfileDao.insertOrUpdateProfile(it)
            }
        }
    }
}
