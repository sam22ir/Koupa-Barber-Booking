package com.koupa.barberbooking.domain.model

/**
 * Domain model for BarberShop entity.
 * Maps to Supabase 'barbershops' table.
 */
data class BarberShop(
    val id: String = "",
    val ownerId: String,
    val shopName: String,
    val city: String,
    val wilayaCode: Int?,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isActive: Boolean = true,
    val bio: String? = null,
    val services: List<String> = emptyList(),
    val openingFrom: String = "09:00",           // "HH:mm"
    val openingTo: String = "20:00",             // "HH:mm"
    val workingDays: List<String> = emptyList(), // ["sun","mon",…]
    val priceMin: Int = 0,
    val priceMax: Int = 5000,
    val whatsappNumber: String? = null,
    val googleUid: String? = null,
    val profilePhotoUrl: String? = null,
    val distanceKm: Double? = null               // Only for nearby search
)

/**
 * Wilaya codes for Algeria (1-58)
 */
object AlgeriaWilayas {
    val codes = mapOf(
        1 to "Adrar", 2 to "Chlef", 3 to "Laghouat", 4 to "Oum El Bouaghi",
        5 to "Batna", 6 to "Béjaïa", 7 to "Biskra", 8 to "Béchar",
        9 to "Blida", 10 to "Bouira", 11 to "Tamanrasset", 12 to "Tébessa",
        13 to "Tlemcen", 14 to "Tiaret", 15 to "Tizi Ouzou", 16 to "Algiers",
        17 to "Djelfa", 18 to "Jijel", 19 to "Sétif", 20 to "Saïda",
        21 to "Skikda", 22 to "Sidi Bel Abbès", 23 to "Annaba", 24 to "Guelma",
        25 to "Constantine", 26 to "Médéa", 27 to "Mostaganem", 28 to "M'sila",
        29 to "Mascara", 30 to "Ouargla", 31 to "Oran", 32 to "El Bayadh",
        33 to "Illizi", 34 to "Bordj Bou Arréridj", 35 to "Boumerdès",
        36 to "El Tarf", 37 to "Tindouf", 38 to "Tissemsilt", 39 to "El Oued",
        40 to "Khenchela", 41 to "Souk Ahras", 42 to "Tipaza", 43 to "Mila",
        44 to "Aïn Defla", 45 to "Naâma", 46 to "Aïn Témouchent",
        47 to "Ghardaia", 48 to "Relizane", 49 to "Timimoun",
        50 to "Bordj Badji Mokhtar", 51 to "Ouled Djellal", 52 to "Béni Abbès",
        53 to "In Salah", 54 to "In Guezzam", 55 to "Touggourt",
        56 to "Djanet", 57 to "El Meghaier", 58 to "El Meniaa"
    )

    fun getName(code: Int): String = codes[code] ?: "Unknown"
}
