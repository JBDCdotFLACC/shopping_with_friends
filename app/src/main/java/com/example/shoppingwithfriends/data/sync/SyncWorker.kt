package com.example.shoppingwithfriends.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.OpType
import com.example.shoppingwithfriends.data.source.local.PendingOp
import com.example.shoppingwithfriends.data.source.local.SyncState
import com.example.shoppingwithfriends.data.source.local.SyncUpdate
import com.example.shoppingwithfriends.data.source.local.User
import com.example.shoppingwithfriends.data.source.local.FireBaseModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json

@HiltWorker
class SyncWorker @AssistedInject constructor(@Assisted appContext: Context, @Assisted workerParams: WorkerParameters,
                                             private val syncRepository: SyncRepository,
                                             private val fireBaseFireStore : FirebaseFirestore):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val pendingOpsList = syncRepository.getPendingOps().filter { it.state != SyncState.SYNCED }
        for(op in pendingOpsList){
            if(op.payloadJson == null) continue
            when(op.type){
                OpType.CREATE_LIST -> createFirebaseDocument<LocalShoppingList>(op, "lists")
                OpType.CREATE_PRODUCT -> {
                    try{
                        val payload = Json.decodeFromString<LocalProduct>(op.payloadJson)
                        fireBaseFireStore.collection("products")
                            .document(payload.id)
                            .set(payload)
                            .await()
                        syncRepository.markDone(op.id)
                        val task = fireBaseFireStore.collection("products").document(payload.id).set(payload)
                        task.await()
                    }
                    catch (e : Exception){
                        Log.i("wxyz", e.message.toString())
                        syncRepository.markFailure(op.id)
                    }
                }
                OpType.UPDATE_LIST_NAME -> {
                    try{
                        val payload = Json.decodeFromString<SyncUpdate>(op.payloadJson)
                        val safeName = payload.content ?: throw IllegalArgumentException("Name cannot be null")
                        val versionId = payload.versionId ?: throw IllegalArgumentException("VersionId cannot be null")
                        fireBaseFireStore.collection("lists")
                            .document(payload.id)
                            .update("name", safeName, "versionId", versionId)
                            .await()
                        syncRepository.markDone(op.id)
                    }
                    catch (e: Exception){
                        syncRepository.markFailure(op.id)
                    }
                }
                OpType.UPDATE_PRODUCT_NAME ->{
                    try {
                        val payload = Json.decodeFromString<SyncUpdate>(op.payloadJson)
                        val safeName = payload.content ?: throw IllegalArgumentException("Name cannot be null")
                        val safeVersion = payload.versionId ?: throw IllegalArgumentException("Version cannot be null")
                        fireBaseFireStore.collection("products")
                            .document(payload.id)
                            .update("content", safeName, "versionId", safeVersion)
                            .await()
                        syncRepository.markDone(op.id)
                    }
                    catch (e: Exception){
                        Log.i("wxyz", e.message.toString())
                        syncRepository.markFailure(op.id)
                    }
                }
                OpType.DELETE_PRODUCT -> {
                    try{
                        val payload = Json.decodeFromString<SyncUpdate>(op.payloadJson)
                        val safeVersion = payload.versionId ?: throw IllegalArgumentException("Null version")
                        fireBaseFireStore.collection("products")
                            .document(payload.id)
                            .update("isDeleted", true, "versionId", safeVersion)
                            .await()
                        syncRepository.markDone(op.id)
                    }
                    catch (e : Exception){
                        Log.i("wxyz", e.message.toString())
                        syncRepository.markFailure(op.id)
                    }


                }
                OpType.UPDATE_PRODUCT_CHECKED -> {
                    try{
                        val payload = Json.decodeFromString<SyncUpdate>(op.payloadJson)
                        val safeChecked = payload.isChecked ?: throw IllegalArgumentException("Checked cannot be null")
                        val safeVersion = payload.versionId ?: throw IllegalArgumentException("Null Version Number")
                        fireBaseFireStore.collection("products")
                            .document(payload.id)
                            .update("isChecked", safeChecked, "versionId", safeVersion)
                            .await()
                        syncRepository.markDone(op.id)
                    }
                    catch (e : Exception){
                        Log.i("wxyz", e.message.toString())
                        syncRepository.markFailure(op.id)
                    }
                }
                OpType.DELETE_LIST -> {
                    try{
                        val payload = Json.decodeFromString<SyncUpdate>(op.payloadJson)
                        val safeVersion = payload.versionId ?: throw IllegalArgumentException("Null Version")
                        fireBaseFireStore.collection("lists")
                            .document(payload.id)
                            .update("isDeleted", true, "versionId", safeVersion)
                            .await()
                        syncRepository.markDone(op.id)
                    }
                    catch (e : Exception){
                        Log.i("wxyz", e.message.toString())
                        syncRepository.markFailure(op.id)
                    }
                }
                OpType.ADD_USER -> createFirebaseDocument<User>(op, "users")
            }

        }
        return Result.success()
    }
    private suspend inline fun <reified T : FireBaseModel> createFirebaseDocument(op : PendingOp, path : String){
        Log.i("wxyz", op.toString())
        op.payloadJson ?: throw IllegalArgumentException("Name cannot be null")
        try{
            val payload = Json.decodeFromString<T>(op.payloadJson)
            fireBaseFireStore.collection(path)
                .document(payload.id)
                .set(payload)
                .await()
            syncRepository.markDone(op.id)
        }
        catch (e : Exception){
            Log.i("wxyz", e.message.toString())
            syncRepository.markFailure(op.id)
        }
    }

}