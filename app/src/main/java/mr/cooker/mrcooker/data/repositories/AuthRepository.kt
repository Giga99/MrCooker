package mr.cooker.mrcooker.data.repositories

import mr.cooker.mrcooker.data.firebase.FirebaseDB
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseDB: FirebaseDB
) {

    suspend fun login(email: String, password: String) = firebaseDB.login(email, password)

    suspend fun register(username: String, email: String, password: String) =
        firebaseDB.register(username, email, password)

    fun signOut() = firebaseDB.signOut()

    suspend fun resetPassword(email: String) = firebaseDB.resetPassword(email)

    fun checkPrevLogging(): Boolean = firebaseDB.checkPrevLogging()

    suspend fun deleteAccount() = firebaseDB.deleteAccount()
}