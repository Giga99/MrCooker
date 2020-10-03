package mr.cooker.mrcooker.other

import java.lang.Exception

class Event<out T>(private val content: T?) {

    var throwable = false

    fun throwException() {
        if(content is Exception) throw content;
    }
}