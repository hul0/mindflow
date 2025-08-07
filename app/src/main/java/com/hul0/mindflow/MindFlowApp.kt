package com.hul0.mindflow

import android.app.Application
import com.hul0.mindflow.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Custom Application class to manage global state and dependencies, like the database.
 */
class MindFlowApp : Application() {
    // The applicationScope is for coroutines that should live as long as the app.
    val applicationScope = CoroutineScope(SupervisorJob())

    // Use 'by lazy' to initialize the database only when it's first accessed.
    // This is a robust way to handle singleton instances.
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
}
