/*
 * Created by Igor Stevanovic on 11/17/20 12:17 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/17/20 12:15 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    suspend fun changeEmail(oldEmail: String, password: String, newEmail: String) =
        firebaseDB.changeEmail(oldEmail, password, newEmail)

    suspend fun deleteAccount() = firebaseDB.deleteAccount()
}