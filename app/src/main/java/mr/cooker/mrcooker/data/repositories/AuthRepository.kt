package mr.cooker.mrcooker.data.repositories

import android.net.Uri
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

    suspend fun checkPrevLogging(): Boolean = firebaseDB.checkPrevLogging()

    suspend fun editAccount(name: String, imgUri: Uri?) = firebaseDB.editAccount(name, imgUri)

    suspend fun uploadProfilePhoto(imageUri: Uri): Uri? = firebaseDB.uploadProfilePhoto(imageUri)

    suspend fun deleteProfilePhoto() = firebaseDB.deleteProfilePhoto()

    suspend fun changePassword(email: String, oldPassword: String, newPassword: String) =
        firebaseDB.changePassword(email, oldPassword, newPassword)

    suspend fun deleteAccount() = firebaseDB.deleteAccount()
}