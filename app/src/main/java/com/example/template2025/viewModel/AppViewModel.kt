package com.example.template2025.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.dataStore.AppDataStore
import com.example.template2025.modelInn.ReservationRepository
import com.example.template2025.screens.GuestScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import com.example.template2025.modelInn.CheckReservationResponse
import com.example.template2025.screens.Country


data class AuthState(val isLoading: Boolean = true, val isLoggedIn: Boolean = false)


class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val dataStore = AppDataStore(app)

    private val _auth = MutableStateFlow(AuthState())
    val auth: StateFlow<AuthState> = _auth.asStateFlow()

    init { observeLoginFlag() }

    private fun observeLoginFlag() {
        viewModelScope.launch {
            dataStore.isLoggedInFlow
                .catch { /* log error si quieres */ }
                .collect { logged ->
                    _auth.value = AuthState(isLoading = false, isLoggedIn = logged)
                }
        }
    }

    fun login()  { viewModelScope.launch { dataStore.setLoggedIn(true) } }
    fun logout() { viewModelScope.launch { dataStore.setLoggedIn(false) } }
}

// 1. Estados para la UI: Carga, Éxito, Error
sealed interface ReservationUiState {
    object Idle : ReservationUiState // Estado inicial
    object Loading : ReservationUiState // Estado de carga
    data class Success(val reservationId: String?, val qrCodeUrl: String?) : ReservationUiState // Éxito
    data class Error(val message: String) : ReservationUiState // Error
}

class GuestViewModel : ViewModel() {

    // 2. Instancia del Repositorio
    private val repository = ReservationRepository()

    // 3. Estado de la pantalla de formulario (el que ya tenías en GuestScreen)
    var formState by mutableStateOf(GuestScreenState())
        private set

    // 4. Estado del proceso de reserva (para mostrar carga, error, etc.)
    var reservationState by mutableStateOf<ReservationUiState>(ReservationUiState.Idle)
        private set

    // 5. Funciones para que la UI actualice el estado del formulario
    fun onFormStateChange(newState: GuestScreenState) {
        formState = newState
    }

    // 6. Se llama al presionar el botón "Confirmar reserva"
    fun confirmReservation() {
        viewModelScope.launch {
            // Ponemos la UI en estado de carga
            reservationState = ReservationUiState.Loading

            // Llamamos al repositorio con el estado actual del formulario
            val result = repository.createReservation(formState)

            // Procesamos el resultado
            result.onSuccess { response ->
                reservationState = ReservationUiState.Success(
                    reservationId = response.reservationId,
                    qrCodeUrl = response.qrCodeUrl
                )
            }.onFailure { error ->
                // Si hubo un error, actualizamos el estado a Error
                reservationState = ReservationUiState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    // Función para resetear el estado (útil después de mostrar un error)
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
                phone = formState.phone,
                dialCode = formState.country.dialCode
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

