package com.example.template2025.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.dataStore.AppDataStore
import com.example.template2025.model.AdminRepository
import com.example.template2025.model.LoginCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userType: String? = null,
    val error: String? = null
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val dataStore = AppDataStore(app)
    private val adminRepository = AdminRepository()
    private val _auth = MutableStateFlow(AuthState())
    val auth: StateFlow<AuthState> = _auth.asStateFlow()

    init {
        observeLoginFlag()
    }

    private fun observeLoginFlag() {
        viewModelScope.launch {
            dataStore.isLoggedInFlow.collect { logged ->
                _auth.value = AuthState(
                    isLoading = false,
                    isLoggedIn = logged,
                    userType = dataStore.userTypeFlow.firstOrNull() ?: ""
                )
            }
        }
    }

    fun login(credentials: LoginCredentials, onResult: (success: Boolean, error: String?) -> Unit) {
        viewModelScope.launch {
            _auth.value = _auth.value.copy(isLoading = true, error = null)

            var success = false
            var errorMessage: String? = null

            try {
                when (credentials) {
                    is LoginCredentials.Admin -> {
                        val result = adminRepository.login(credentials.username, credentials.password)
                        if (result.isSuccess) {
                            val res = result.getOrThrow()
                            success = res.success
                            if (success) {
                                dataStore.setLoggedIn(true)
                                dataStore.setUserType("admin")

                                res.token?.let { token ->
                                    dataStore.setToken(token)
                                }
                            } else {
                                errorMessage = "Usuario o contraseña incorrectos."
                                dataStore.clearSession()
                            }
                        } else {
                            success = false
                            errorMessage = result.exceptionOrNull()?.message ?: "Ocurrió un error"
                            dataStore.clearSession()
                        }
                        result.isSuccess && result.getOrThrow().success
                    }
                    is LoginCredentials.Guest -> true
                }
            } catch (e: Exception) {
                success = false
                errorMessage = e.message ?: "Ocurrió un error crítico."
                dataStore.clearSession()
            }

            _auth.value = _auth.value.copy(isLoading = false, error = errorMessage)
            onResult(success, errorMessage)
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.clearSession()
        }
    }

    fun clearError() {
        _auth.value = _auth.value.copy(error = null)
    }

}