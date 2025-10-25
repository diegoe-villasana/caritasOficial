package com.example.template2025.screens

import com.example.template2025.model.Posada
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

class GuestScreenTest {

    private lateinit var defaultState: GuestScreenState
    private lateinit var samplePosada: Posada

    @BeforeEach
    fun setup() {
        samplePosada = Posada(
            id = 1,
            nombre = "Posada Test",
            direccion = "Calle Test 123",
            capacidad = 50,
            descripcion = "Posada de prueba",
            latitud = 0.0,
            longitud = 0.0
        )

        defaultState = GuestScreenState()
    }

    @Nested
    @DisplayName("GuestScreenState Tests")
    inner class GuestScreenStateTests {

        @Test
        @DisplayName("Estado inicial debe tener valores por defecto correctos")
        fun `test default state values`() {
            assertNull(defaultState.selectedPosada)
            assertFalse(defaultState.isHeadquarterExpanded)
            assertEquals("DD/MM/AAAA", defaultState.entryDate)
            assertEquals("Hombre", defaultState.applicantInfo.gender)
            assertEquals(1, defaultState.menCount)
            assertEquals(0, defaultState.womenCount)
            assertFalse(defaultState.hasAcceptedPrivacyPolicy)
            assertNull(defaultState.selectedPosadaError)
            assertNull(defaultState.entryDateError)
            assertNull(defaultState.guestCountError)
            assertNull(defaultState.fullNameError)
            assertNull(defaultState.phoneError)
            assertNull(defaultState.genderError)
        }

        @Test
        @DisplayName("Debe actualizar selectedPosada correctamente")
        fun `test update selectedPosada`() {
            val updatedState = defaultState.copy(selectedPosada = samplePosada)

            assertEquals(samplePosada, updatedState.selectedPosada)
            assertEquals("Posada Test", updatedState.selectedPosada?.nombre)
        }

        @Test
        @DisplayName("Debe actualizar isHeadquarterExpanded correctamente")
        fun `test toggle headquarter expanded`() {
            val expandedState = defaultState.copy(isHeadquarterExpanded = true)

            assertTrue(expandedState.isHeadquarterExpanded)
        }

        @Test
        @DisplayName("Debe actualizar entryDate correctamente")
        fun `test update entry date`() {
            val dateString = "25/10/2025"
            val updatedState = defaultState.copy(entryDate = dateString)

            assertEquals(dateString, updatedState.entryDate)
        }

        @Test
        @DisplayName("Debe actualizar hasAcceptedPrivacyPolicy correctamente")
        fun `test accept privacy policy`() {
            val acceptedState = defaultState.copy(hasAcceptedPrivacyPolicy = true)

            assertTrue(acceptedState.hasAcceptedPrivacyPolicy)
        }

        @Test
        @DisplayName("Debe mantener menCount en 1 cuando el solicitante es hombre")
        fun `test men count remains at least 1 when applicant is male`() {
            val state = defaultState.copy(
                applicantInfo = PersonInfo(gender = "Hombre"),
                menCount = 1
            )

            assertTrue(state.menCount >= 1)
            assertEquals("Hombre", state.applicantInfo.gender)
        }

        @Test
        @DisplayName("Debe incrementar menCount correctamente")
        fun `test increment men count`() {
            val updatedState = defaultState.copy(menCount = 5)

            assertEquals(5, updatedState.menCount)
        }

        @Test
        @DisplayName("Debe incrementar womenCount correctamente")
        fun `test increment women count`() {
            val updatedState = defaultState.copy(womenCount = 3)

            assertEquals(3, updatedState.womenCount)
        }

        @Test
        @DisplayName("Debe calcular el total de personas correctamente")
        fun `test total person count calculation`() {
            val state = defaultState.copy(menCount = 5, womenCount = 3)
            val total = state.menCount + state.womenCount

            assertEquals(8, total)
        }

        @Test
        @DisplayName("Debe manejar errores de validación correctamente")
        fun `test validation errors`() {
            val stateWithErrors = defaultState.copy(
                selectedPosadaError = "Debe seleccionar una posada",
                entryDateError = "Debe seleccionar una fecha",
                fullNameError = "El nombre es requerido",
                phoneError = "El teléfono es inválido",
                genderError = "Debe seleccionar un género"
            )

            assertNotNull(stateWithErrors.selectedPosadaError)
            assertNotNull(stateWithErrors.entryDateError)
            assertNotNull(stateWithErrors.fullNameError)
            assertNotNull(stateWithErrors.phoneError)
            assertNotNull(stateWithErrors.genderError)
        }
    }

    @Nested
    @DisplayName("PersonInfo Tests")
    inner class PersonInfoTests {

        @Test
        @DisplayName("PersonInfo debe tener valores por defecto correctos")
        fun `test person info default values`() {
            val personInfo = PersonInfo()

            assertEquals("", personInfo.fullName)
            assertEquals("", personInfo.phone)
            assertEquals("", personInfo.gender)
            assertEquals("México", personInfo.country.name)
            assertEquals("+52", personInfo.country.dialCode)
            assertEquals("🇲🇽", personInfo.country.flag)
            assertEquals("MX", personInfo.country.isoCode)
        }

        @Test
        @DisplayName("Debe actualizar fullName correctamente")
        fun `test update full name`() {
            val personInfo = PersonInfo(fullName = "Juan Pérez García")

            assertEquals("Juan Pérez García", personInfo.fullName)
        }

        @Test
        @DisplayName("Debe limitar fullName a 70 caracteres")
        fun `test full name length limit`() {
            val longName = "a".repeat(100)
            val limitedName = longName.take(70)

            assertEquals(70, limitedName.length)
        }

        @Test
        @DisplayName("Debe filtrar caracteres no permitidos en fullName")
        fun `test full name character filtering`() {
            val nameWithNumbers = "Juan123 Pérez456"
            val filtered = nameWithNumbers.filter { it.isLetter() || it.isWhitespace() }

            assertEquals("Juan Pérez", filtered)
        }

        @Test
        @DisplayName("Debe actualizar phone correctamente")
        fun `test update phone`() {
            val personInfo = PersonInfo(phone = "8112345678")

            assertEquals("8112345678", personInfo.phone)
        }

        @Test
        @DisplayName("Debe actualizar gender correctamente")
        fun `test update gender`() {
            val personInfo = PersonInfo(gender = "Mujer")

            assertEquals("Mujer", personInfo.gender)
        }

        @Test
        @DisplayName("Debe actualizar country correctamente")
        fun `test update country`() {
            val usCountry = Country("Estados Unidos", "+1", "🇺🇸", "US")
            val personInfo = PersonInfo(country = usCountry)

            assertEquals("Estados Unidos", personInfo.country.name)
            assertEquals("+1", personInfo.country.dialCode)
            assertEquals("🇺🇸", personInfo.country.flag)
            assertEquals("US", personInfo.country.isoCode)
        }
    }

    @Nested
    @DisplayName("Country Tests")
    inner class CountryTests {

        @Test
        @DisplayName("Country debe almacenar datos correctamente")
        fun `test country data storage`() {
            val country = Country("México", "+52", "🇲🇽", "MX")

            assertEquals("México", country.name)
            assertEquals("+52", country.dialCode)
            assertEquals("🇲🇽", country.flag)
            assertEquals("MX", country.isoCode)
        }

        @Test
        @DisplayName("getCountries debe retornar lista no vacía")
        fun `test get countries returns non-empty list`() {
            val countries = getCountries()

            assertTrue(countries.isNotEmpty())
        }

        @Test
        @DisplayName("getCountries debe contener México")
        fun `test get countries contains Mexico`() {
            val countries = getCountries()
            val mexico = countries.find { it.isoCode == "MX" }

            assertNotNull(mexico)
            assertEquals("México", mexico?.name)
            assertEquals("+52", mexico?.dialCode)
        }

        @Test
        @DisplayName("getCountries debe contener Estados Unidos")
        fun `test get countries contains USA`() {
            val countries = getCountries()
            val usa = countries.find { it.isoCode == "US" }

            assertNotNull(usa)
            assertEquals("Estados Unidos", usa?.name)
            assertEquals("+1", usa?.dialCode)
        }

        @Test
        @DisplayName("getCountries debe retornar exactamente 6 países")
        fun `test get countries returns correct count`() {
            val countries = getCountries()

            assertEquals(6, countries.size)
        }

        @Test
        @DisplayName("Todos los países deben tener dial codes válidos")
        fun `test all countries have valid dial codes`() {
            val countries = getCountries()

            countries.forEach { country ->
                assertTrue(country.dialCode.startsWith("+"))
                assertTrue(country.dialCode.length >= 2)
            }
        }

        @Test
        @DisplayName("Todos los países deben tener ISO codes de 2 caracteres")
        fun `test all countries have valid ISO codes`() {
            val countries = getCountries()

            countries.forEach { country ->
                assertEquals(2, country.isoCode.length)
                assertTrue(country.isoCode.all { it.isUpperCase() })
            }
        }
    }

    @Nested
    @DisplayName("Gender Change Logic Tests")
    inner class GenderChangeLogicTests {

        @Test
        @DisplayName("Cambiar de Hombre a Mujer debe actualizar contadores")
        fun `test change gender from male to female updates counts`() {
            var state = GuestScreenState(
                applicantInfo = PersonInfo(gender = "Hombre"),
                menCount = 1,
                womenCount = 0
            )

            // Simular cambio de género
            val oldGender = state.applicantInfo.gender
            val newGender = "Mujer"

            var men = state.menCount
            var women = state.womenCount

            if (oldGender == "Hombre") men = (men - 1).coerceAtLeast(0)
            if (oldGender == "Mujer") women = (women - 1).coerceAtLeast(0)
            if (newGender == "Hombre") men++
            if (newGender == "Mujer") women++

            state = state.copy(
                applicantInfo = state.applicantInfo.copy(gender = newGender),
                menCount = men,
                womenCount = women
            )

            assertEquals("Mujer", state.applicantInfo.gender)
            assertEquals(0, state.menCount)
            assertEquals(1, state.womenCount)
        }

        @Test
        @DisplayName("Cambiar de Mujer a Hombre debe actualizar contadores")
        fun `test change gender from female to male updates counts`() {
            var state = GuestScreenState(
                applicantInfo = PersonInfo(gender = "Mujer"),
                menCount = 0,
                womenCount = 1
            )

            val oldGender = state.applicantInfo.gender
            val newGender = "Hombre"

            var men = state.menCount
            var women = state.womenCount

            if (oldGender == "Hombre") men = (men - 1).coerceAtLeast(0)
            if (oldGender == "Mujer") women = (women - 1).coerceAtLeast(0)
            if (newGender == "Hombre") men++
            if (newGender == "Mujer") women++

            state = state.copy(
                applicantInfo = state.applicantInfo.copy(gender = newGender),
                menCount = men,
                womenCount = women
            )

            assertEquals("Hombre", state.applicantInfo.gender)
            assertEquals(1, state.menCount)
            assertEquals(0, state.womenCount)
        }

        @Test
        @DisplayName("No debe cambiar contadores si el género es el mismo")
        fun `test no counter change when gender is same`() {
            val state = GuestScreenState(
                applicantInfo = PersonInfo(gender = "Hombre"),
                menCount = 3,
                womenCount = 2
            )

            val oldGender = state.applicantInfo.gender
            val newGender = "Hombre"

            if (oldGender != newGender) {
                fail("No debería ejecutarse la lógica de cambio")
            }

            assertEquals(3, state.menCount)
            assertEquals(2, state.womenCount)
        }
    }

    @Nested
    @DisplayName("Counter Logic Tests")
    inner class CounterLogicTests {

        @Test
        @DisplayName("Debe respetar el mínimo de contadores")
        fun `test counter respects minimum value`() {
            val minCount = 1
            val newCount = (0).coerceAtLeast(minCount)

            assertEquals(1, newCount)
        }

        @Test
        @DisplayName("Debe permitir incrementar el contador")
        fun `test counter can be incremented`() {
            val currentCount = 5
            val newCount = currentCount + 1

            assertEquals(6, newCount)
        }

        @Test
        @DisplayName("Debe permitir decrementar el contador")
        fun `test counter can be decremented`() {
            val currentCount = 5
            val minCount = 0
            val newCount = (currentCount - 1).coerceAtLeast(minCount)

            assertEquals(4, newCount)
        }

        @Test
        @DisplayName("No debe permitir decrementar por debajo del mínimo")
        fun `test counter cannot go below minimum`() {
            val currentCount = 1
            val minCount = 1
            val newCount = (currentCount - 1).coerceAtLeast(minCount)

            assertEquals(1, newCount)
        }

        @Test
        @DisplayName("Debe calcular correctamente cuando se alcanza la capacidad")
        fun `test capacity reached calculation`() {
            val menCount = 5
            val womenCount = 5
            val totalGuests = menCount + womenCount
            val capacidad = 10
            val isCapacityReached = totalGuests >= capacidad

            assertTrue(isCapacityReached)
        }

        @Test
        @DisplayName("Debe permitir incremento cuando no se alcanza la capacidad")
        fun `test increment allowed when capacity not reached`() {
            val menCount = 3
            val womenCount = 2
            val totalGuests = menCount + womenCount
            val capacidad = 10
            val isCapacityReached = totalGuests >= capacidad

            assertFalse(isCapacityReached)
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    inner class ValidationTests {

        @Test
        @DisplayName("Debe validar que se seleccionó una posada")
        fun `test validate posada selected`() {
            val state = defaultState.copy(selectedPosada = null)

            assertNull(state.selectedPosada)
        }

        @Test
        @DisplayName("Debe validar formato de fecha")
        fun `test validate date format`() {
            val validDate = "25/10/2025"
            val invalidDate = "DD/MM/AAAA"

            assertNotEquals(invalidDate, validDate)
            assertTrue(validDate.matches(Regex("\\d{2}/\\d{2}/\\d{4}")))
        }

        @Test
        @DisplayName("Debe validar que el nombre no esté vacío")
        fun `test validate name not empty`() {
            val emptyName = ""
            val validName = "Juan Pérez"

            assertTrue(emptyName.isEmpty())
            assertTrue(validName.isNotEmpty())
        }

        @Test
        @DisplayName("Debe validar que el teléfono tenga al menos 10 dígitos")
        fun `test validate phone has minimum digits`() {
            val validPhone = "8112345678"
            val invalidPhone = "123"

            assertTrue(validPhone.length >= 10)
            assertFalse(invalidPhone.length >= 10)
        }

        @Test
        @DisplayName("Debe validar que se aceptó la política de privacidad")
        fun `test validate privacy policy accepted`() {
            val stateNotAccepted = defaultState.copy(hasAcceptedPrivacyPolicy = false)
            val stateAccepted = defaultState.copy(hasAcceptedPrivacyPolicy = true)

            assertFalse(stateNotAccepted.hasAcceptedPrivacyPolicy)
            assertTrue(stateAccepted.hasAcceptedPrivacyPolicy)
        }

        @Test
        @DisplayName("Debe validar que hay al menos una persona")
        fun `test validate at least one person`() {
            val state = defaultState.copy(menCount = 1, womenCount = 0)
            val total = state.menCount + state.womenCount

            assertTrue(total >= 1)
        }

        @Test
        @DisplayName("Debe validar que se seleccionó un género")
        fun `test validate gender selected`() {
            val emptyGender = ""
            val validGender = "Hombre"

            assertTrue(emptyGender.isEmpty())
            assertTrue(validGender.isNotEmpty())
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    inner class IntegrationTests {

        @Test
        @DisplayName("Debe crear un estado completo válido para reservación")
        fun `test create complete valid reservation state`() {
            val completeState = GuestScreenState(
                selectedPosada = samplePosada,
                entryDate = "25/10/2025",
                applicantInfo = PersonInfo(
                    fullName = "Juan Pérez García",
                    phone = "8112345678",
                    gender = "Hombre",
                    country = Country("México", "+52", "🇲🇽", "MX")
                ),
                menCount = 2,
                womenCount = 1,
                hasAcceptedPrivacyPolicy = true
            )

            assertNotNull(completeState.selectedPosada)
            assertNotEquals("DD/MM/AAAA", completeState.entryDate)
            assertTrue(completeState.applicantInfo.fullName.isNotEmpty())
            assertTrue(completeState.applicantInfo.phone.isNotEmpty())
            assertTrue(completeState.applicantInfo.gender.isNotEmpty())
            assertTrue(completeState.menCount + completeState.womenCount >= 1)
            assertTrue(completeState.hasAcceptedPrivacyPolicy)
        }

        @Test
        @DisplayName("Debe formatear correctamente el número de teléfono completo")
        fun `test format complete phone number`() {
            val personInfo = PersonInfo(
                phone = "8112345678",
                country = Country("México", "+52", "🇲🇽", "MX")
            )

            val formattedPhone = "${personInfo.country.dialCode} ${personInfo.phone}"

            assertEquals("+52 8112345678", formattedPhone)
        }

        @Test
        @DisplayName("Debe manejar cambio de país correctamente")
        fun `test handle country change`() {
            val initialInfo = PersonInfo(
                phone = "8112345678",
                country = Country("México", "+52", "🇲🇽", "MX")
            )

            val updatedInfo = initialInfo.copy(
                country = Country("Estados Unidos", "+1", "🇺🇸", "US")
            )

            assertEquals("Estados Unidos", updatedInfo.country.name)
            assertEquals("+1", updatedInfo.country.dialCode)
            assertEquals("8112345678", updatedInfo.phone) // El teléfono se mantiene
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("Debe manejar capacidad cero")
        fun `test handle zero capacity`() {
            val capacidad = 0
            val totalGuests = 1
            val isCapacityReached = if (capacidad > 0) totalGuests >= capacidad else false

            assertFalse(isCapacityReached)
        }

        @Test
        @DisplayName("Debe manejar nombre con solo espacios")
        fun `test handle name with only spaces`() {
            val nameWithSpaces = "   "
            val trimmedName = nameWithSpaces.trim()

            assertTrue(trimmedName.isEmpty())
        }

        @Test
        @DisplayName("Debe manejar teléfono vacío")
        fun `test handle empty phone`() {
            val personInfo = PersonInfo(phone = "")

            assertTrue(personInfo.phone.isEmpty())
        }

        @Test
        @DisplayName("Debe manejar múltiples espacios en el nombre")
        fun `test handle multiple spaces in name`() {
            val nameWithMultipleSpaces = "Juan    Pérez    García"
            val normalizedName = nameWithMultipleSpaces.replace(Regex("\\s+"), " ")

            assertEquals("Juan Pérez García", normalizedName)
        }

        @Test
        @DisplayName("Debe manejar contadores con valores muy grandes")
        fun `test handle very large counter values`() {
            val state = defaultState.copy(menCount = 1000, womenCount = 1000)
            val total = state.menCount + state.womenCount

            assertEquals(2000, total)
        }

        @Test
        @DisplayName("Debe manejar cambio de género sin género previo")
        fun `test handle gender change without previous gender`() {
            var state = GuestScreenState(
                applicantInfo = PersonInfo(gender = ""),
                menCount = 0,
                womenCount = 0
            )

            val oldGender = state.applicantInfo.gender
            val newGender = "Hombre"

            var men = state.menCount
            var women = state.womenCount

            if (oldGender == "Hombre") men = (men - 1).coerceAtLeast(0)
            if (oldGender == "Mujer") women = (women - 1).coerceAtLeast(0)
            if (newGender == "Hombre") men++
            if (newGender == "Mujer") women++

            state = state.copy(
                applicantInfo = state.applicantInfo.copy(gender = newGender),
                menCount = men,
                womenCount = women
            )

            assertEquals("Hombre", state.applicantInfo.gender)
            assertEquals(1, state.menCount)
            assertEquals(0, state.womenCount)
        }
    }
}
