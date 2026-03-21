package com.koupa.barberbooking.data.datasource.remote

import io.github.jan-tennert.supabase.SupabaseClient
import io.github.jan-tennert.supabase.createSupabaseClient
import io.github.jan-tennert.supabase.functions.Functions
import io.github.jan-tennert.supabase.gotrue.Auth
import io.github.jan-tennert.supabase.postgrest.Postgrest
import io.github.jan-tennert.supabase.realtime.Realtime
import com.koupa.barberbooking.BuildConfig

/**
 * Supabase client singleton.
 * Configured with URL and anon key from BuildConfig.
 */
object SupabaseClientFactory {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Functions)
        }
    }
}
