package com.example.template2025.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.template2025.model.ApiClient
import com.example.template2025.model.VoluntarioRegistroRequest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.util.*

@RunWith(AndroidJUnit4::class)
class RegistrarseVoluntarioTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        mockkObject(ApiClient)
        // Mock del endpoint para devolver éxito genérico
        every { ApiClient.publicApi.registrarVoluntario(any()) } answers { Response.success(Unit) }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun registroVoluntario_flow_fillFields_selectRandomShelter_andClick_registersAndCallsApi() {
        // Datos aleatorios para la prueba
        val randomName = "TestUser-${UUID.randomUUID().toString().take(6)}"
        val randomPhone = "442" + (1000000..9999999).random().toString().take(7) // 10 dígitos aproximados

        // Variable para comprobar que onRegistered fue invocado
        var registered = false

        // Contenido composable bajo prueba
        composeTestRule.setContent {
            RegistroVoluntarioView(
                onRegistered = { registered = true },
                onBack = {}
            )
        }

        // Llenar campos
        composeTestRule.onNodeWithText("Nombre completo").performTextInput(randomName)
        composeTestRule.onNodeWithText("Número de Teléfono").performTextInput(randomPhone)

        // Selección aleatoria de albergue (los textos vienen del composable)
        val shelters = listOf(
            "Posada del Peregrino",
            "Posada del Peregrino \"Divina Providencia\"",
            "Posada del Peregrino Apodaca"
        )
        val chosen = shelters.random()
        composeTestRule.onNodeWithText(chosen).performClick()

        // Hacer click en el botón de registrar
        composeTestRule.onNodeWithText("Registrar como voluntario").performClick()

        // Esperar que la interfaz se estabilice
        composeTestRule.waitForIdle()

        // Verificar que el callback onRegistered fue llamado
        assertTrue("Se esperaba que onRegistered fuera invocado", registered)

        // Verificar que la API fue llamada al menos una vez con objeto que contiene el teléfono usado
        verify {
            ApiClient.publicApi.registrarVoluntario(match {
                it is VoluntarioRegistroRequest &&
                        it.nombre == randomName &&
                        // el campo phone en el request debe contener los dígitos ingresados
                        it.phone.contains(randomPhone.filter { ch -> ch.isDigit() })
            })
        }
    }
}
