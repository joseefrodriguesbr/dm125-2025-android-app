package com.aduilio.mytasks.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aduilio.mytasks.databinding.ActivityPhoneAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneAuthBinding
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSendCode.setOnClickListener {
            sendVerificationCode()
        }

        binding.btnVerifyCode.setOnClickListener {
            verifyCode()
        }
    }

    private fun sendVerificationCode() {
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um número de telefone.", Toast.LENGTH_SHORT).show()
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Número de telefone para verificar
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout
            .setActivity(this) // Activity para resolver recaptcha, se necessário
            .setCallbacks(callbacks) // Callbacks para o resultado
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        // Informa ao usuário que o código foi enviado
        Toast.makeText(this, "Enviando código...", Toast.LENGTH_SHORT).show()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Login instantâneo em alguns casos (ex: dispositivo já verificado)
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // Falha na verificação
            Toast.makeText(applicationContext, "Falha na verificação: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // O SMS foi enviado. Salva o ID da verificação para usar depois.
            storedVerificationId = verificationId

            // Mostra os campos para inserir o código
            binding.layoutVerificationCode.visibility = View.VISIBLE
            binding.btnVerifyCode.visibility = View.VISIBLE
            binding.layoutPhoneNumber.visibility = View.GONE
            binding.btnSendCode.visibility = View.GONE

            Toast.makeText(applicationContext, "Código enviado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyCode() {
        val code = binding.etVerificationCode.text.toString().trim()
        if (code.isEmpty() || storedVerificationId.isNullOrEmpty()) {
            Toast.makeText(this, "Código inválido ou verificação expirada.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido!
                    Toast.makeText(applicationContext, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    // Falha no login
                    Toast.makeText(applicationContext, "Falha na autenticação do código.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
