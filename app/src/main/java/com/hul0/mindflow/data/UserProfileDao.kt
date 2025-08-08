package com.hul0.mindflow.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hul0.mindflow.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the UserProfile entity.
 * This interface defines the database interactions for user profiles.
 */
@Dao
interface UserProfileDao {

    /**
     * Retrieves the user profile from the database.
     * As we only have one user, it fetches the profile with id 1.
     * @return A Flow that emits the UserProfile.
     */
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfile?>

    /**
     * Inserts or updates a user profile in the database.
     * If a profile with the same id already exists, it will be replaced.
     * @param userProfile The UserProfile object to be inserted or updated.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(userProfile: UserProfile)

    /**
     * Retrieves the user profile for data export.
     * This is a non-Flow version for one-time data fetching.
     * @return The UserProfile object or null if it doesn't exist.
     */
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileForExport(): UserProfile?
}
