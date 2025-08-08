package com.hul0.mindflow.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.UserProfileRepository
import com.hul0.mindflow.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty1

/**
 * ViewModel for the ProfileScreen.
 * It handles the business logic for managing user profile data.
 *
 * @param repository The repository for user profile data.
 */
class ProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    // Holds the current state of the user profile.
    private val _profileState = MutableStateFlow<UserProfile?>(null)
    val profileState: StateFlow<UserProfile?> = _profileState.asStateFlow()

    init {
        // Load the profile when the ViewModel is created.
        viewModelScope.launch {
            repository.getProfile().collect { profile ->
                _profileState.value = profile ?: UserProfile()
            }
        }
    }

    /**
     * Updates a specific field in the user profile state.
     * This function is now aligned with the call from ProfileScreen.
     * @param property The mutable property of UserProfile to update (e.g., UserProfile::fullName).
     * @param value The new value for the property.
     */
    fun onProfileChange(property: KMutableProperty1<UserProfile, *>, value: Any) {
        _profileState.value?.let { currentProfile ->
            val updatedProfile = currentProfile.copy()
            try {
                // This cast is necessary because the property comes in as a generic KMutableProperty1<UserProfile, *>.
                // We trust that the UI layer provides a `value` of the correct type for the given `property`.
                @Suppress("UNCHECKED_CAST")
                (property as KMutableProperty1<UserProfile, Any>).set(updatedProfile, value)
                _profileState.value = updatedProfile
            } catch (e: ClassCastException) {
                // Log this error in a real application. It indicates a mismatch between
                // the data type expected by the property and the `value` provided.
                // For example, trying to set a Float property with a String value.
            }
        }
    }


    /**
     * Saves the current profile state to the database.
     */
    fun saveProfile() {
        viewModelScope.launch {
            _profileState.value?.let {
                repository.saveProfile(it)
            }
        }
    }

    /**
     * Exports the profile data to a CSV file.
     * @param context The application context.
     * @param uri The URI for the output file.
     */
    fun exportProfile(context: Context, uri: Uri) {
        viewModelScope.launch {
            // In a real app, you might get the latest from the DB first
            // but here we export the current UI state.
            _profileState.value?.let {
                repository.exportProfileToCsv(context, uri, it)
            }
        }
    }

    /**
     * Imports profile data from a CSV file.
     * @param context The application context.
     * @param uri The URI of the input file.
     */
    fun importProfile(context: Context, uri: Uri) {
        viewModelScope.launch {
            repository.importProfileFromCsv(context, uri)
            // After import, the repository will save the new profile,
            // and the flow in `init` will automatically update the UI.
        }
    }
}
