package mr.cooker.mrcooker.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser

class FirebaseDB {
    companion object val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
        currentUser = auth.currentUser!!
    }

    suspend fun register(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        auth.currentUser!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(username).build())
        currentUser = auth.currentUser!!
    }

    fun checkPrevLogging(): Boolean {
        return if(auth.currentUser != null) {
            currentUser = auth.currentUser!!
            true
        } else false
    }
}