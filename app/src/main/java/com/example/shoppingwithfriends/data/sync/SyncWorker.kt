package com.example.shoppingwithfriends.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(@Assisted appContext: Context, @Assisted workerParams: WorkerParameters,
                                             private val syncRepository: SyncRepository,
                                             private val fireBaseFireStore : FirebaseFirestore):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val pendingOpsList = syncRepository.getPendingOps()
        Log.i("wxyz", pendingOpsList.size.toString())
        for(op in pendingOpsList){
            val payload = Json.decodeFromString<LocalShoppingList>(op.payloadJson!!)
            fireBaseFireStore.collection("lists")
                .add(payload) //todo fix this obviously
                .addOnSuccessListener { documentReference ->
                    Log.d("wxyz", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("wxyz", "Error adding document", e)
                }
        }
        return Result.success()
    }
}