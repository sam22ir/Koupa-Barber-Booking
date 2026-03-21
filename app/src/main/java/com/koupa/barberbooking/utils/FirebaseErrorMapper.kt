package com.koupa.barberbooking.utils

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException

/**
 * Maps Firebase exceptions to user-friendly Arabic error messages.
 */
object FirebaseErrorMapper {
    fun mapAuthException(e: Exception): String {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException ->
                "رقم الهاتف غير صالح"
            is FirebaseTooManyRequestsException ->
                "محاولات كثيرة جداً. حاول مرة أخرى بعد بضع دقائق."
            is FirebaseAuthMissingActivityForRecaptchaException ->
                "خطأ في التحقق. يرجى المحاولة مرة أخرى."
            is FirebaseNetworkException ->
                "لا يوجد اتصال بالإنترنت. تحقق من شبكتك."
            is FirebaseException ->
                "خطأ في المصادقة: ${e.localizedMessage}"
            else ->
                "خطأ غير متوقع: ${e.localizedMessage}"
        }
    }

    fun mapOtpException(e: Exception): String {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException ->
                "رمز التحقق غير صحيح"
            is FirebaseTooManyRequestsException ->
                "محاولات كثيرة جداً. حاول لاحقاً."
            is FirebaseNetworkException ->
                "لا يوجد اتصال بالإنترنت. تحقق من شبكتك."
            is FirebaseException ->
                "خطأ في التحقق: ${e.localizedMessage}"
            else ->
                "خطأ غير متوقع: ${e.localizedMessage}"
        }
    }
}
