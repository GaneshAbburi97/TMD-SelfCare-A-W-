package com.example.tmdapp.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.tmdapp.R
import com.example.tmdapp.TmdViewModel

@Composable
fun SignUpScreen(
    viewModel: TmdViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showOtpDialog by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    val context = LocalContext.current
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("980089860878-jcqefu0gvq1tcmf739q5089h6roltb0h.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    viewModel.loginWithGoogle(idToken, {
                        onNavigateToHome()
                    }, { error ->
                        errorMessage = error
                    })
                } else {
                    errorMessage = "Google sign up failed: No ID Token found"
                }
            } catch (e: ApiException) {
                errorMessage = "Google sign up failed: ${e.statusCode}"
            }
        } else {
            errorMessage = "Google sign up canceled"
        }
    }

    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = { Text("Verify Email") },
            text = {
                Column {
                    Text("Please enter the 6-digit OTP code sent to your email.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("OTP Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (otpCode.length == 6) {
                        viewModel.verifyOtp(email, otpCode, {
                            showOtpDialog = false
                            onNavigateToHome()
                        }, { error ->
                            errorMessage = error
                            showOtpDialog = false
                        })
                    } else {
                        errorMessage = "OTP must be 6 digits"
                    }
                }) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all fields"
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "Invalid email format"
                } else if (password.length < 6) {
                    errorMessage = "Password must be at least 6 characters"
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    viewModel.signUp(name, email, password, onSuccess = {
                        onNavigateToHome()
                    }, onNeedsOtp = {
                        showOtpDialog = true
                    }, onError = { error ->
                        errorMessage = error
                    })
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Create Account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                launcher.launch(googleSignInClient.signInIntent)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Sign Up with Google")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("Already have an account? ")
            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}
