package mr.cooker.mrcooker.other

import java.lang.Exception

class EventFirebase(content: Exception?) {

    var throwable = false
    var exception = content

    fun throwException() {
        throw exception!!
    }
}