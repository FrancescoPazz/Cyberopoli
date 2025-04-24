package com.unibo.cyberopoli

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.dsl.module

val supabaseModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://yptaguzpvdprmhxrleni.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlwdGFndXpwdmRwcm1oeHJsZW5pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDQ4MzM5NTQsImV4cCI6MjA2MDQwOTk1NH0.eE9pjnKYxPA-NT3PGFiw2N1k1JNSytyqC7h5VI25dI8"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}