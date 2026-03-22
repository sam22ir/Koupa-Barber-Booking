package com.koupa.barberbooking.utils

/**
 * Utility to format Algerian phone numbers to E.164 (+213XXXXXXXXX).
 * Accepts:
 *   - 05XXXXXXXX / 06XXXXXXXX / 07XXXXXXXX  (10‑digit national format)
 *   - +213XXXXXXXXX  (already international)
 *   - 213XXXXXXXXX   (international without +)
 */
object AlgerianPhoneFormatter {

    fun format(raw: String): String {
        val digits = raw.trim().replace("\\s".toRegex(), "")
        return when {
            // Already in full international format
            digits.startsWith("+213") && digits.length == 13 -> digits
            // International without +
            digits.startsWith("213") && digits.length == 12 -> "+$digits"
            // National format 0X…
            digits.startsWith("0") && digits.length == 10 -> "+213${digits.substring(1)}"
            // Bare 9‑digit (without leading 0)
            digits.length == 9 -> "+213$digits"
            else -> digits // return as-is, validation will catch it
        }
    }

    fun isValid(raw: String): Boolean {
        val formatted = format(raw)
        // +213 followed by exactly 9 digits starting with 5, 6, or 7
        return formatted.matches(Regex("^\\+213[567]\\d{8}$"))
    }

    fun errorMessage(raw: String): String? {
        return if (!isValid(raw)) {
            "أدخل رقم هاتف جزائري صحيح (مثال: 0555123456 أو +213555123456)"
        } else null
    }
}
