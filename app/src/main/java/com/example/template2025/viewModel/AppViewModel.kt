package com.example.template2025.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.dataStore.AppDataStore
import com.example.template2025.model.AdminRepository
import com.example.template2025.model.AdminSessionExpiredException
import com.example.template2025.model.ApiClient
import com.example.template2025.model.PosadaRepository
import com.example.template2025.model.LoginCredentials
import com.example.template2025.model.Posada
import com.example.template2025.model.Reserva
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

data class PosadaState(
    val isLoading: Boolean = false,
    val posadas: List<Posada> = emptyList(),
    val error: String? = null
)

data class ReservaState(
    val isLoading: Boolean = false,
    val reservas: List<Reserva> = emptyList(),
    val error: String? = null
)

data class ReservaDetailState(
    val isLoading: Boolean = false,
    val reserva: Reserva? = null,
    val error: String? = null
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val dataStore = AppDataStore(app)
    private val adminRepository = AdminRepository()
    private val posadaRepository = PosadaRepository()
    private val _auth = MutableStateFlow(AuthState())
    val auth: StateFlow<AuthState> = _auth.asStateFlow()

    private val _posadaState = MutableStateFlow(PosadaState())
    val posadaState: StateFlow<PosadaState> = _posadaState.asStateFlow()

    private val _reservaState = MutableStateFlow(ReservaState())
    val reservaState: StateFlow<ReservaState> = _reservaState.asStateFlow()

    private val _reservaDetailState = MutableStateFlow(ReservaDetailState())
    val reservaDetailState: StateFlow<ReservaDetailState> = _reservaDetailState.asStateFlow()

    private val _navigateToAuth = MutableStateFlow(false)
    val navigateToAuth: StateFlow<Boolean> = _navigateToAuth.asStateFlow()


    init {
        ApiClient.initialize(app)

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

    fun logout(sessionExpired: Boolean = false) {
        viewModelScope.launch {
            dataStore.clearSession()
            if (sessionExpired) {
                _auth.value = _auth.value.copy(error = "Sesión expirada. Por favor, inicie sesión de nuevo.")
            }
            _navigateToAuth.value = true
        }
    }

    fun onAuthNavigationComplete() {
        _navigateToAuth.value = false
    }

    fun clearError() {
        _auth.value = _auth.value.copy(error = null)
    }

    fun getPosadas() {
        viewModelScope.launch {
            // Set loading state to true and clear previous errors
            _posadaState.value = _posadaState.value.copy(isLoading = true, error = null)

            val result = posadaRepository.getPosadas()

            if (result.isSuccess) {
                _posadaState.value = _posadaState.value.copy(
                    isLoading = false,
                    posadas = result.getOrThrow()
                )
            } else {
                _posadaState.value = _posadaState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al cargar los datos."
                )
            }
        }
    }

    fun getReservas() {
        viewModelScope.launch {
            _reservaState.value = _reservaState.value.copy(isLoading = true, error = null)
            try {
                val result = adminRepository.getAllReservas()
                result.onSuccess { response ->
                    _reservaState.value = _reservaState.value.copy(
                        isLoading = false,
                        reservas = response.data
                    )
                }.onFailure {
                    _reservaState.value = _reservaState.value.copy(
                        isLoading = false,
                        error = it.message
                    )
                }
            } catch (_: AdminSessionExpiredException) {
                logout(sessionExpired = true)
            }
        }
    }

    fun getReservasByPosada(posadaId: Int) {
        viewModelScope.launch {
            _reservaState.value = _reservaState.value.copy(isLoading = true, error = null)
            try {
                val result = adminRepository.getPosadaReservas(posadaId)
                result.onSuccess { response ->
                    _reservaState.value = _reservaState.value.copy(
                        isLoading = false,
                        reservas = response.data
                    )
                }.onFailure {
                    _reservaState.value = _reservaState.value.copy(
                        isLoading = false,
                        error = it.message
                    )
                }
            } catch (_: AdminSessionExpiredException) {
                logout(sessionExpired = true)
            }
        }
    }

    fun fetchReservaById(id: Int) {
        viewModelScope.launch {
            _reservaDetailState.value = ReservaDetailState(isLoading = true)
            try {
                val result = adminRepository.getReservaById(id)
                result.onSuccess { reserva ->
                    _reservaDetailState.value = ReservaDetailState(reserva = reserva)
                }.onFailure { exception ->
                    _reservaDetailState.value = ReservaDetailState(error = exception.message)
                }
            } catch (_: AdminSessionExpiredException) {
                logout(sessionExpired = true)
            }
        }
    }

    fun cancelReserva(id: Int, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = adminRepository.cancelReserva(id)
                result.onSuccess {
                    getReservas()
                    onResult(true, "Reserva cancelada exitosamente.")
                }.onFailure { exception ->
                    onResult(false, exception.message ?: "Ocurrió un error.")
                }
            } catch (e: AdminSessionExpiredException) {
                logout(sessionExpired = true)
                onResult(false, "Tu sesión ha expirado.")
            }
        }
    }
}