package com.example.shoppingwithfriends.data.sync

import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import javax.inject.Inject

class SyncWorkManager @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleSync() {
        Log.i("wxyz", "in scheduled sync")
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "sync_pending_changes",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}