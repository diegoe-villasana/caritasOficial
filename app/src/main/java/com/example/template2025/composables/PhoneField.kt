package com.example.template2025.composables

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.template2025.screens.Country
import com.example.template2025.screens.getCountries

@Composable
fun PhoneField(
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: Country,
    onCountryChange: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val countries = remember { getCountries() }

    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text("Teléfono") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        leadingIcon = {
            Row(
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedCountry.flag)
                Text(selectedCountry.dialCode)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar país"
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text("${country.flag} ${country.name} (${country.dialCode})") },
                            onClick = {
                                onCountryChange(country)
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}