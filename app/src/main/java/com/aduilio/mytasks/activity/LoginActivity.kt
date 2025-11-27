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
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        initComponents()
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
                        login()
                    } else {
                        val message =
                            task.exception?.message ?: ContextCompat.getString(this, R.string.account_created_fail)
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
                        startActivity(Intent(this, MainActivity::class.java))
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
}