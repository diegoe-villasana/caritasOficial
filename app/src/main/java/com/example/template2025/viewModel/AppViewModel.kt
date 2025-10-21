package com.example.template2025.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.dataStore.AppDataStore
import com.example.template2025.model.AdminRepository
import com.example.template2025.model.AdminSessionExpiredException
import com.example.template2025.model.ApiClient
import com.example.template2025.model.PosadaRepository
import com.example.template2025.model.LoginCredentials
import com.example.template2025.model.Posada
import com.example.template2025.model.Reserva
import com.example.template2025.model.ReservationRepository
import com.example.template2025.screens.GuestScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import com.example.template2025.model.CheckReservationResponse
import com.example.template2025.screens.Country

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
            } catch (_: AdminSessionExpiredException) {
                logout(sessionExpired = true)
                onResult(false, "Tu sesión ha expirado.")
            }
        }
    }

    fun updateReservaEstado(reservaId: Int, nuevoEstado: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = adminRepository.updateReservaEstado(reservaId, nuevoEstado)
            result.fold(
                onSuccess = {
                    fetchReservaById(reservaId)
                    getReservas()
                    onResult(true, "Estado actualizado correctamente.")
                },
                onFailure = { exception ->
                    onResult(false, exception.message ?: "Error desconocido.")
                }
            )
        }
    }
}

// 1. Estados para la UI: Carga, Éxito, Error
sealed interface ReservationUiState {
    object Idle : ReservationUiState // Estado inicial
    object Loading : ReservationUiState // Estado de carga
    data class Success(val reservationId: String?, val qrCodeUrl: String?, val qr_token: String?) : ReservationUiState // Éxito
    data class Error(val message: String) : ReservationUiState // Error
}

class GuestViewModel : ViewModel() {

    // 2. Instancia del Repositorio
    private val repository = ReservationRepository()


    private var posadasList: List<Posada> = emptyList()


    private var posadasCapacity: Int = 0


    // 3. Estado de la pantalla de formulario (el que ya tenías en GuestScreen)
    var formState by mutableStateOf(GuestScreenState())
        private set

    // 4. Estado del proceso de reserva (para mostrar carga, error, etc.)
    var reservationState by mutableStateOf<ReservationUiState>(ReservationUiState.Idle)
        private set

    // 5. Funciones para que la UI actualice el estado del formulario
    fun onFormStateChange(newState: GuestScreenState) {
        if (newState.selectedPosada?.id != formState.selectedPosada?.id) {
            val selectedPosadaData = posadasList.find { it.id == newState.selectedPosada?.id }
            posadasCapacity = selectedPosadaData?.capacidadDisponible ?: 0
        }
        formState = newState
    }

    private fun validateForm(): Boolean {
        formState = formState.copy(
            selectedPosadaError = null,
            entryDateError = null,
            guestCountError = null,
            fullNameError = null,
            phoneError = null
        )
        val currentState = formState
        var isValid = true


        // 3. Validar que haya al menos un huésped
        if (currentState.menCount + currentState.womenCount == 0) {
            formState = formState.copy(guestCountError = "Debe haber al menos un huésped")
            isValid = false
        }

        // 4. Validar el nombre del solicitante
        if (currentState.applicantInfo.fullName.isBlank()) {
            formState = formState.copy(fullNameError = "El nombre no puede estar vacío")
            isValid = false
        }

        // 5. Validar el teléfono del solicitante (vacío y longitud)
        if (currentState.applicantInfo.phone.isBlank()) {
            formState = formState.copy(phoneError = "El teléfono no puede estar vacío")
            isValid = false
        } else if (currentState.applicantInfo.phone.length != 10) {
            formState = formState.copy(phoneError = "El teléfono debe tener 10 dígitos")
            isValid = false
        }

        // 6. Validar el género del solicitante
        if (currentState.applicantInfo.gender.isBlank()) {
            formState = formState.copy(genderError = "Debes seleccionar un género")
            isValid = false
        }

        return isValid
    }

    // 6. Se llama al presionar el botón "Confirmar reserva"
    fun confirmReservation() {
// --- 1. PRIMERA VALIDACIÓN: Capacidad ---
        val totalGuests = formState.menCount + formState.womenCount
        // Comprobamos si hay capacidad y si el número de huéspedes la excede.
        if (posadasCapacity > 0 && totalGuests > posadasCapacity) {
            reservationState = ReservationUiState.Error(
                "Reserva denegada: No hay lugares suficientes.\nDisponibles: $posadasCapacity"
            )
            return // Detiene la ejecución aquí
        }

        // --- 2. SEGUNDA VALIDACIÓN: Campos del formulario ---
        if (validateForm()) {
            // Si el formulario es válido, procedemos con la llamada a la API.
            viewModelScope.launch {
                reservationState = ReservationUiState.Loading

                val result = repository.createReservation(formState)

                // Procesamos el resultado
                result.onSuccess { response ->
                    reservationState = ReservationUiState.Success(
                        reservationId = response.reservationId,
                        qrCodeUrl = response.qrCodeUrl,
                        qr_token = response.qr_token
                    )
                }.onFailure { error ->
                    // Si hubo un error, actualizamos el estado a Error
                    reservationState =
                        ReservationUiState.Error(error.message ?: "Error desconocido")
                }
            }
        }




    }
    fun resetReservationState() {
        reservationState = ReservationUiState.Idle
    }
}

sealed interface CheckReservationUiState {
    object Idle : CheckReservationUiState
    object Loading : CheckReservationUiState
    data class Success(val response: CheckReservationResponse) : CheckReservationUiState
    data class Error(val message: String) : CheckReservationUiState
}

// Estado del formulario
data class CheckReservationFormState(
    val fullName: String = "",
    val phone: String = "",
    val country: Country = Country("México", "+52", "🇲🇽", "MX")
)

class CheckReservationViewModel : ViewModel() {
    private val repository = ReservationRepository()

    var formState by mutableStateOf(CheckReservationFormState())
        private set

    var searchState by mutableStateOf<CheckReservationUiState>(CheckReservationUiState.Idle)
        private set

    fun onFormStateChange(newState: CheckReservationFormState) {
        formState = newState
    }

    fun checkReservation() {
        viewModelScope.launch {
            searchState = CheckReservationUiState.Loading
            val result = repository.checkReservation(
                fullName = formState.fullName,
                phone = formState.country.dialCode + formState.phone,
                dialCode = formState.country.isoCode
            )
            result.onSuccess { response ->
                searchState = CheckReservationUiState.Success(response)
            }.onFailure { error ->
                searchState = CheckReservationUiState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    fun resetSearchState() {
        searchState = CheckReservationUiState.Idle
    }
}

sealed interface ScannerUiState {
    object Idle : ScannerUiState // Waiting to scan
    object Loading : ScannerUiState // API call in progress
    data class Success(val message: String) : ScannerUiState // API call succeeded
    data class Error(val message: String) : ScannerUiState // API call failed
}

class QRScannerViewModel : ViewModel() {
    private val repository = AdminRepository()

    var uiState by mutableStateOf<ScannerUiState>(ScannerUiState.Idle)
        private set

    fun processQrToken(token: String) {
        if (uiState is ScannerUiState.Loading) return

        viewModelScope.launch {
            uiState = ScannerUiState.Loading

            val result = repository.updateReservationStatus(
                qrToken = token,
                newStatus = "checkin"
            )

            result.onSuccess {
                uiState = ScannerUiState.Success("Check-in realizado con éxito.")
            }.onFailure { error ->
                uiState = ScannerUiState.Error(error.message ?: "Error desconocido al procesar el QR.")
            }
        }
    }

    fun resetState() {
        uiState = ScannerUiState.Idle
    }
}