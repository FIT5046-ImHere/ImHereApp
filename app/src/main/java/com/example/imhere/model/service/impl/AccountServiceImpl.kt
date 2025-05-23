package com.example.imhere.model.service.impl

import com.example.imhere.model.AuthUser
import com.example.imhere.model.UserProfile
import com.example.imhere.model.UserProfileType
import com.example.imhere.model.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AccountService {

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUserProfile: Flow<UserProfile?>
        get() = callbackFlow {
            val uid = auth.currentUser?.uid
            if (uid == null) {
                close()
            } else {
                val docRef = firestore.collection("users").document(uid)
                val listener = docRef.addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && snapshot.exists()) {
                        val data = snapshot.data

                        if (data != null) {
                            trySend(snapshot.toObject(UserProfile::class.java))
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
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { AuthUser(it.uid) } ?: AuthUser())
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
        auth.currentUser!!.delete().await()
    }

    override suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()
    }

    override suspend fun createUserProfile(uid: String, profile: UserProfile) {
        firestore.collection("users").document(uid).set(profile).await()
    }

    override suspend fun updateUserProfile(uid: String, profile: UserProfile) {
        firestore.collection("users").document(uid)
            .set(profile, SetOptions.merge()).await()
    }

    override suspend fun fetchUserProfile(uid: String): UserProfile? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(UserProfile::class.java)
        } else {
            null
        }

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
                type = UserProfileType.STUDENT, // Default to student; adjust if needed
                birthDate = Date() // Placeholder, update if needed
            )
            createUserProfile(user.uid, profile)
        }
    }
}