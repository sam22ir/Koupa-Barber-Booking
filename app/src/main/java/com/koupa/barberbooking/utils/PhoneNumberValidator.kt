package com.koupa.barberbooking.utils

/**
 * Phone number validator for Algerian mobile numbers.
 * Supports E.164 format (+213XXXXXXXXX) and local format (0XXXXXXXXX).
 */
object PhoneNumberValidator {
    // E.164 pattern for Algerian mobile: +213 followed by 9 digits starting with 5, 6, or 7
    private val e164Pattern = Regex("""^\+213[567]\d{8}$""")

    /**
     * Normalize phone number to E.164 format.
     * Accepts: 0XXXXXXXXX, +213XXXXXXXXX, 00213XXXXXXXXX, or 9-digit local
     * @return E.164 formatted number or null if invalid
     */
    fun normalizeToE164(phone: String): String? {
        val cleaned = phone.replace(Regex("[\\s\\-()]"), "").trim()
        return when {
            // Already E.164: +213XXXXXXXXX
            cleaned.startsWith("+213") && cleaned.length == 13 -> cleaned
            // International prefix: 00213XXXXXXXXX
            cleaned.startsWith("00213") && cleaned.length == 14 -> "+${cleaned.substring(2)}"
            // Local format: 0XXXXXXXXX (10 digits)
            cleaned.startsWith("0") && cleaned.length == 10 -> "+213${cleaned.substring(1)}"
            // 9 digits without prefix: XXXXXXXXX
            cleaned.length == 9 && cleaned[0] in "567" -> "+213$cleaned"
            else -> null
        }
    }

    /**
     * Check if phone number is a valid Algerian mobile number.
     */
    fun isValidAlgeriaMobile(phone: String): Boolean {
        val normalized = normalizeToE164(phone)
        return normalized != null && e164Pattern.matches(normalized)
    }

    /**
     * Format phone number for display (local format).
     * @param e164Phone E.164 formatted phone number
     * @return Display format: 0XXX XXX XXX
     */
    fun formatForDisplay(e164Phone: String): String {
        if (!e164Phone.startsWith("+213") || e164Phone.length != 13) return e164Phone
        val local = "0${e164Phone.substring(4)}"
        return "${local.substring(0, 4)} ${local.substring(4, 7)} ${local.substring(7)}"
    }
}
