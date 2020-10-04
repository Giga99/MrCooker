package mr.cooker.mrcooker.other

import java.lang.Exception

class EventFirebaseAuth(private val content: Exception?) {

    var throwable = false
    var exception = content

    fun throwException() {
        throw exception!!;
    }
}