package com.example.template2025.model

sealed class LoginCredentials {
    data class Admin(val username: String, val password: String) : LoginCredentials()
    data class Guest(val phone: String, val password: String) : LoginCredentials()
}