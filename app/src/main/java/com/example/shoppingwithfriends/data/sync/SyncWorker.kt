package com.example.shoppingwithfriends.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.OpType
import com.example.shoppingwithfriends.data.source.local.SyncState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(@Assisted appContext: Context, @Assisted workerParams: WorkerParameters,
                                             private val syncRepository: SyncRepository,
                                             private val fireBaseFireStore : FirebaseFirestore):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val pendingOpsList = syncRepository.getPendingOps().filter { it.state != SyncState.SYNCED }
        Log.i("wxyz", pendingOpsList.toString())
        for(op in pendingOpsList){
            if(op.payloadJson == null) continue
            val payload = Json.decodeFromString<LocalShoppingList>(op.payloadJson)
            when(op.type){
                OpType.CREATE_LIST -> {
                    Log.i("wxyz", "create list")
                    try{
                        fireBaseFireStore.collection("lists")
                            .document(payload.id)
                            .set(payload)
                            .await()
                        syncRepository.markDone(op.id)
                        Log.i("wxyz", "insert")
                    }
                    catch (e : Exception){
                        Log.i("wxyz", e.message.toString())
                        syncRepository.markFailure(op.id)
                    }
                }
                OpType.UPDATE_LIST_NAME -> {
                    Log.i("wxyz", "update list name")
                    try{
                        fireBaseFireStore.collection("lists")
                            .document(payload.id)
                            .update("name", payload.name)
                            .await()
                        syncRepository.markDone(op.id)
                        Log.i("wxyz", "update")
                    }
                    catch (e: Exception){
                        Log.i("wxyz", e.message.toString())
                        syncRepository.markFailure(op.id)
                    }
                }
                else -> {
                    Log.i("wxyz", op.type.toString())
                }
            }

        }
        return Result.success()
    }
}