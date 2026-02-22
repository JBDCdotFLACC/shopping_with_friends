package com.example.shoppingwithfriends.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(@Assisted appContext: Context, @Assisted workerParams: WorkerParameters,
                                             private val syncRepository: SyncRepository):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val whatever = syncRepository.getNextPendingOp()
        return Result.success()
    }
}