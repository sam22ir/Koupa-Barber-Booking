package com.koupa.barberbooking.domain.usecase

import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor() {
    
    suspend operator fun invoke(fullName: String, email: String): Result<Boolean> {
        return try {
            // In a real implementation, this would update the user's profile in the database
            // For now, we'll simulate a successful update
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}