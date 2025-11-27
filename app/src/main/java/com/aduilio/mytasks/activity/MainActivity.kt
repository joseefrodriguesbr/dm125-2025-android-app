package com.aduilio.mytasks.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.aduilio.mytasks.R
import com.aduilio.mytasks.adapter.ListAdapter
import com.aduilio.mytasks.adapter.TouchCallback
import com.aduilio.mytasks.databinding.ActivityMainBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.listener.ClickListener
import com.aduilio.mytasks.listener.SwipeListener
import com.aduilio.mytasks.service.TaskService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ListAdapter

    private val taskService: TaskService by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()

        askNotificationPermission()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getBoolean("first_run", true)) {
            AlertDialog.Builder(this)
                .setMessage("Aqui vc vai criar suas tarefas.")
                .setNeutralButton(android.R.string.ok, null)
                .create()
                .show()

            preferences.edit { putBoolean("first_run", false) }
        }
    }

    override fun onResume() {
        super.onResume()
        getTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.preferences -> {
                startActivity(Intent(this, PreferenceActivity::class.java))
            }
            // Lógica para o item "Sobre"
            R.id.about -> {
                showAboutDialog()
            }
            R.id.logout -> {
                logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAboutDialog() {
        // Mensagem com a disciplina e o autor, separados por uma quebra de linha
        val message = "Disciplina: DM125-Desenvolvimento de aplicativos em Kotlin para Android com Firebase\n\nAutor: José Enderson Ferreira Rodrigues"

        AlertDialog.Builder(this)
            .setTitle(R.string.about)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()
    }

    private fun initComponents() {
        binding.tvMessage.visibility = View.INVISIBLE

        adapter = ListAdapter(this, binding.tvMessage, object : ClickListener {
            override fun onClick(task: Task) {
                val intent = Intent(this@MainActivity, FormActivity::class.java)
                task.id?.let { id ->
                    intent.putExtra("id", id.toLong())
                }
                startActivity(intent)
            }

            override fun onComplete(id: Long) {
                taskService.complete(id).observe(this@MainActivity) { response ->
                    if (!response.error) {
                        getTasks()
                    }
                }
            }
        })
        binding.rvMain.adapter = adapter

        binding.fabNew.setOnClickListener {
            startActivity(Intent(this, FormActivity::class.java))
        }

        ItemTouchHelper(TouchCallback(object : SwipeListener {
            override fun onSwipe(position: Int) {
                showDeleteConfirmationDialog(position)
            }
        })).attachToRecyclerView(binding.rvMain)

        binding.srlMain.setOnRefreshListener {
            getTasks()
        }
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_confirmation_title)
            .setMessage(R.string.delete_confirmation_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteTask(position)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                adapter.notifyItemChanged(position)
            }
            .setOnCancelListener {
                adapter.notifyItemChanged(position)
            }
            .create()
            .show()
    }

    private fun deleteTask(position: Int) {
        adapter.getItem(position).id?.let { id ->
            taskService.delete(id.toLong()).observe(this@MainActivity) { response ->
                if (response.error) {
                    adapter.notifyItemChanged(position)
                } else {
                    adapter.removeItem(position)
                }
            }
        }
    }

    private fun getTasks() {
        taskService.list().observe(this) { response ->
            binding.srlMain.isRefreshing = false

            if (response.error) {
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = ContextCompat.getString(this, R.string.server_error)
            } else {
                response.value?.let {
                    adapter.setData(it)
                } ?: run {
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.tvMessage.text = ContextCompat.getString(this, R.string.empty_list)
                }
            }
        }
    }

    private fun logout() {
        Firebase.auth.signOut()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.permission)
                    .setMessage(R.string.notification_permission_rationale)
                    .setPositiveButton(
                        android.R.string.ok
                    ) { dialog, which -> null }
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                    .show()
            }
        }
    }
}
