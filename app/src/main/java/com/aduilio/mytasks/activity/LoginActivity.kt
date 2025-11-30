package com.aduilio.mytasks.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aduilio.mytasks.R
import com.aduilio.mytasks.databinding.ActivityLoginBinding
import com.aduilio.mytasks.extension.hasValue
import com.aduilio.mytasks.extension.value
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Firebase Auth primeiro.
        auth = Firebase.auth

        // Se o usuário já estiver logado, não carrega a UI de login.
        if (auth.currentUser != null) {
            goToMainActivity() // Navega para a tela principal e finaliza esta.
            return // Para a execução do onCreate aqui.
        }

        // Se o usuário não estiver logado, continua para configurar a tela de login.
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()

        // Adiciona o listener para o novo botão de login com telefone.
        binding.btLoginWithPhone.setOnClickListener {
            startActivity(Intent(this, PhoneAuthActivity::class.java))
        }
    }

    private fun initComponents() {
        binding.btLogin.setOnClickListener {
            if (validate()) {
                login()
            }
        }

        binding.btCreateAccount.setOnClickListener {
            if (validate()) {
                createAccount()
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        binding.layoutEmail.error = null
        binding.layoutPassword.error = null

        if (!binding.etEmail.hasValue()) {
            isValid = false
            binding.layoutEmail.error = ContextCompat.getString(this, R.string.empty_email)
        }

        if (!binding.etPassword.hasValue()) {
            isValid = false
            binding.layoutPassword.error = ContextCompat.getString(this, R.string.empty_password)
        }

        return isValid
    }

    private fun createAccount() {
        auth.createUserWithEmailAndPassword(binding.etEmail.value(), binding.etPassword.value())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Após criar a conta, tenta fazer o login automaticamente.
                    login()
                } else {
                    val message =
                        task.exception?.message ?: ContextCompat.getString(this, R.string.account_created_fail)
                    // Agora a referência 'Log' é reconhecida
                    Log.e("auth", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun login() {
        auth.signInWithEmailAndPassword(binding.etEmail.value(), binding.etPassword.value())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goToMainActivity()
                } else {
                    val message =
                        task.exception?.message ?: ContextCompat.getString(this, R.string.login_fail)

                    Log.e("auth", "loginUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    // Adicionada a função auxiliar para centralizar a navegação.
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Limpa o histórico para que o usuário não possa voltar para a tela de login.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // Finaliza a LoginActivity para removê-la da memória.
    }
}
