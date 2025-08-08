package com.hul0.mindflow.utils

import android.content.Context
import android.net.Uri
import com.hul0.mindflow.model.UserProfile
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception

/**
 * A helper object for handling CSV import and export operations for the UserProfile.
 * This has been updated to match the streamlined UserProfile data class.
 */
object CsvHelper {

    // Defines the header row for the CSV file, matching the current UserProfile properties.
    private val HEADER = arrayOf(
        "id", "fullName", "dateOfBirth", "gender", "occupation", "location",
        "emergencyContactName", "emergencyContactPhone", "allergies", "medicalConditions",
        "medicationInfo", "stressors", "copingMechanisms", "dietType", "exerciseFrequency",
        "sleepTarget", "waterIntakeTarget", "morningPerson", "hobbies", "musicPreferences",
        "filmPreferences", "bookPreferences", "petPeeves", "personalGoals", "favoriteQuote",
        "lifeMotto", "proudestMoment"
    )

    /**
     * Exports a UserProfile object to a CSV file at the given URI.
     * @param context The application context.
     * @param uri The destination URI for the CSV file.
     * @param profile The UserProfile data to export.
     */
    fun exportToCsv(context: Context, uri: Uri, profile: UserProfile) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = CSVWriter(OutputStreamWriter(outputStream))
                writer.writeNext(HEADER)
                writer.writeNext(profile.toCsvRow())
                writer.close()
            }
        } catch (e: Exception) {
            // In a real app, handle this exception gracefully (e.g., show a toast to the user)
            e.printStackTrace()
        }
    }

    /**
     * Imports a UserProfile from a CSV file at the given URI.
     * @param context The application context.
     * @param uri The source URI of the CSV file.
     * @return A UserProfile object parsed from the CSV, or null on failure.
     */
    fun importFromCsv(context: Context, uri: Uri): UserProfile? {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = CSVReader(InputStreamReader(inputStream))
                // Skip header by reading it first
                val header = reader.readNext()
                if (header == null || !header.contentEquals(HEADER)) {
                    // Optional: Add a check to ensure the CSV structure is what you expect.
                    // You could throw an exception or return null if the header is incorrect.
                    return null
                }

                val line = reader.readNext()
                reader.close()
                return line?.toUserProfile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Converts a UserProfile object to a string array for CSV writing.
     */
    private fun UserProfile.toCsvRow(): Array<String> {
        return arrayOf(
            id.toString(),
            fullName,
            dateOfBirth,
            gender,
            occupation,
            location,
            emergencyContactName,
            emergencyContactPhone,
            allergies,
            medicalConditions,
            medicationInfo,
            stressors,
            copingMechanisms,
            dietType,
            exerciseFrequency,
            sleepTarget.toString(),
            waterIntakeTarget.toString(),
            morningPerson,
            hobbies,
            musicPreferences,
            filmPreferences,
            bookPreferences,
            petPeeves,
            personalGoals,
            favoriteQuote,
            lifeMotto,
            proudestMoment
        )
    }

    /**
     * Converts a string array (from a CSV row) to a UserProfile object.
     */
    private fun Array<String>.toUserProfile(): UserProfile? {
        // Basic validation to prevent crashes if the CSV is malformed
        if (this.size < HEADER.size) return null

        return UserProfile(
            id = this[0].toIntOrNull() ?: 1,
            fullName = this[1],
            dateOfBirth = this[2],
            gender = this[3],
            occupation = this[4],
            location = this[5],
            emergencyContactName = this[6],
            emergencyContactPhone = this[7],
            allergies = this[8],
            medicalConditions = this[9],
            medicationInfo = this[10],
            stressors = this[11],
            copingMechanisms = this[12],
            dietType = this[13],
            exerciseFrequency = this[14],
            sleepTarget = this[15].toFloatOrNull() ?: 8.0f,
            waterIntakeTarget = this[16].toFloatOrNull() ?: 2.0f,
            morningPerson = this[17],
            hobbies = this[18],
            musicPreferences = this[19],
            filmPreferences = this[20],
            bookPreferences = this[21],
            petPeeves = this[22],
            personalGoals = this[23],
            favoriteQuote = this[24],
            lifeMotto = this[25],
            proudestMoment = this[26]
        )
    }
}
