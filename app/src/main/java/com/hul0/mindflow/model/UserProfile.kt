package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents all the information for a user profile.
 * This entity will be stored in a Room database table.
 *
 * Note: Properties have been streamlined to match the UI fields in ProfileScreen.
 *
 * @property id The unique identifier for the profile, defaulting to 1 for a single-user app.
 * @property fullName User's full name.
 * @property dateOfBirth User's date of birth.
 * @property gender User's gender.
 * @property occupation User's current occupation.
 * @property location User's city and country.
 * @property emergencyContactName Name of the emergency contact.
 * @property emergencyContactPhone Phone number of the emergency contact.
 * @property allergies Known allergies.
 * @property medicalConditions Any existing medical conditions.
 * @property medicationInfo Information about any medications being taken.
 * @property stressors Common triggers or stressors.
 * @property copingMechanisms Healthy coping mechanisms the user practices.
 * @property dietType User's dietary preferences (e.g., vegetarian, vegan).
 * @property exerciseFrequency How often the user exercises.
 * @property sleepTarget Target hours of sleep per night. Stored as a Float for slider compatibility.
 * @property waterIntakeTarget Daily water intake goal in liters. Stored as a Float for slider compatibility.
 * @property morningPerson Whether the user is a "Morning Person" or a "Night Owl".
 * @property hobbies User's hobbies and interests.
 * @property musicPreferences Favorite music genres.
 * @property filmPreferences Favorite film genres.
 * @property bookPreferences Favorite book genres.
 * @property petPeeves Things that annoy the user.
 * @property personalGoals Short-term and long-term personal goals.
 * @property favoriteQuote A quote that inspires the user.
 * @property lifeMotto The user's personal motto.
 * @property proudestMoment A moment the user is very proud of.
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    var fullName: String = "",
    var dateOfBirth: String = "",
    var gender: String = "",
    var occupation: String = "",
    var location: String = "",
    var emergencyContactName: String = "",
    var emergencyContactPhone: String = "",
    var allergies: String = "",
    var medicalConditions: String = "",
    var medicationInfo: String = "",
    var stressors: String = "",
    var copingMechanisms: String = "",
    var dietType: String = "",
    var exerciseFrequency: String = "",
    var sleepTarget: Float = 8.0f,
    var waterIntakeTarget: Float = 2.0f,
    var morningPerson: String = "Morning Person",
    var hobbies: String = "",
    var musicPreferences: String = "",
    var filmPreferences: String = "",
    var bookPreferences: String = "",
    var petPeeves: String = "",
    var personalGoals: String = "",
    var favoriteQuote: String = "",
    var lifeMotto: String = "",
    var proudestMoment: String = ""
)
