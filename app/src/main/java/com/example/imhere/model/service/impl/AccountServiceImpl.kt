package com.example.imhere.model.service.impl

import UserProfileEntity
import android.util.Log
import com.example.imhere.db.UserProfileRepository
import com.example.imhere.db.toEntity
import com.example.imhere.model.AuthUser
import com.example.imhere.model.UserProfile
import com.example.imhere.model.UserProfileType
import com.example.imhere.model.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userProfileRepository: UserProfileRepository
) : AccountService {

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val dbUserProfile: Flow<List<UserProfileEntity>> = userProfileRepository.allSubjects

    override val currentUserProfile: Flow<UserProfile?>
        get() = callbackFlow {
            val uid = auth.currentUser?.uid
            if (uid == null) {
                close()
            } else {
                val docRef = firestore.collection("users").document(uid)
                val listener = docRef.addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && snapshot.exists()) {
                        val data = snapshot.toObject(UserProfile::class.java)
                        if (data != null) {
                            trySend(data)
                            saveToRoom(data.toEntity())
                        } else {
                            trySend(null)
                        }
                    } else {
                        trySend(null)
                    }
                }

                awaitClose { listener.remove() }
            }
        }

    override val currentAuthUser: Flow<AuthUser>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser?.let { AuthUser(it.uid) } ?: AuthUser())
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun createAnonymousAccount() {
        auth.signInAnonymously().await()
    }

    override suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
    }

    override suspend fun signOut() {
        if (auth.currentUser?.isAnonymous == true) {
            auth.currentUser?.delete()
        }
        auth.signOut()
    }

    override suspend fun createUserProfile(uid: String, profile: UserProfile) {
        firestore.collection("users").document(uid).set(profile).await()
        saveToRoom(profile.toEntity())
    }

    override suspend fun updateUserProfile(uid: String, profile: UserProfile) {
        firestore.collection("users").document(uid)
            .set(profile, SetOptions.merge()).await()
        saveToRoom(profile.toEntity())
    }

    override suspend fun fetchUserProfile(uid: String): UserProfile? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        val profile = if (snapshot.exists()) snapshot.toObject(UserProfile::class.java) else null
        profile?.let { saveToRoom(it.toEntity()) }
        return profile
    }

    override suspend fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()
        val user = authResult.user ?: throw Exception("Google sign-in failed")

        val userDoc = firestore.collection("users").document(user.uid).get().await()
        if (!userDoc.exists()) {
            val profile = UserProfile(
                uid = user.uid,
                name = user.displayName ?: "",
                email = user.email ?: "",
                type = UserProfileType.STUDENT,
                birthDate = Date()
            )
            createUserProfile(user.uid, profile)
        }
    }

//    private fun saveToRoom(profile: UserProfileEntity) {
//        CoroutineScope(Dispatchers.IO).launch {
//            userProfileRepository.upsert(profile)
//        }
//    }
    private fun saveToRoom(profile: UserProfileEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("ROOM", "🧠 Upserting to Room: $profile")
            userProfileRepository.upsert(profile)
        }
    }

}
