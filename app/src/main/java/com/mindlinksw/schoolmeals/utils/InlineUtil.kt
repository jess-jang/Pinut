package com.mindlinksw.schoolmeals.utils

inline fun tryCatch(action: () -> Unit) {
    try {
        action()
    }
    catch (e: Exception) {
        Logger.e("Exception", e.message)
    }
}