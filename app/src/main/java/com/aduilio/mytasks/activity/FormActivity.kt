package com.aduilio.mytasks.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aduilio.mytasks.R
import com.aduilio.mytasks.databinding.ActivityFormBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.extension.hasValue
import com.aduilio.mytasks.extension.value
import com.aduilio.mytasks.service.TaskService
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import android.view.View

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private val taskService: TaskService by viewModels()
    private var taskId: Long? = null
    private var taskCompleted: Boolean = false // Usada para preservar o estado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.extras?.getLong("id")?.let {
            getTask(it)
        }
        initComponents()
    }

    private fun getTask(id: Long) {
        taskService.getById(id).observe(this) { response ->
            if (!response.error) {
                response.value?.let { populateForm(it) }
            }
        }
    }


    private fun populateForm(task: Task) {
        taskId = task.id?.toLong()
        binding.etTitle.setText(task.title)
        binding.etDescription.setText(task.description)
        binding.etDate.setText(formatDate(task.date))
        binding.etTime.setText(formatTime(task.time))
        taskCompleted = task.completed

        if (task.completed) {
            // Se a tarefa estiver completa, mostra o selo verde e esconde o vermelho
            binding.tvStatusCompleted.visibility = View.VISIBLE
            binding.tvStatusNotCompleted.visibility = View.GONE
        } else {
            // Se não estiver completa, mostra o selo vermelho e esconde o verde
            binding.tvStatusNotCompleted.visibility = View.VISIBLE
            binding.tvStatusCompleted.visibility = View.GONE
        }
    }


    private fun formatDate(date: LocalDate?): String {
        return date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
    }

    private fun formatTime(time: LocalTime?): String {
        return time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initComponents() {
        binding.btSave.setOnClickListener {
            if (validate()) {
                val date = if (binding.etDate.hasValue()) LocalDate.parse(binding.etDate.value(), DateTimeFormatter.ofPattern("dd/MM/yyyy")) else null
                val time = if (binding.etTime.hasValue()) LocalTime.parse(binding.etTime.value(), DateTimeFormatter.ofPattern("HH:mm")) else null

                // Ao salvar, passa o valor guardado de 'taskCompleted'
                val task = Task(
                    id = taskId?.toInt(),
                    title = binding.etTitle.value(),
                    description = binding.etDescription.value(),
                    date = date,
                    time = time,
                    completed = taskCompleted
                )

                if (taskId == null) {
                    taskService.create(task).observe(this) { response ->
                        if (response.error) showAlert(R.string.create_error) else finish()
                    }
                } else {
                    taskService.update(task).observe(this) { response ->
                        if (response.error) showAlert(R.string.update_error) else finish()
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        binding.layoutTitle.error = null
        binding.layoutDate.error = null
        binding.layoutTime.error = null

        if (!binding.etTitle.hasValue()) {
            binding.layoutTitle.error = getString(R.string.title_required)
            isValid = false
        }

        if (binding.etDate.hasValue()) {
            try {
                LocalDate.parse(binding.etDate.value(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: DateTimeParseException) {
                isValid = false
                binding.layoutDate.error = getString(R.string.invalid_date_format)
            }
        }

        if (binding.etTime.hasValue()) {
            try {
                LocalTime.parse(binding.etTime.value(), DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: DateTimeParseException) {
                isValid = false
                binding.layoutTime.error = getString(R.string.invalid_time_format)
            }
        }

        return isValid
    }

    private fun showAlert(message: Int) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setNeutralButton(android.R.string.ok, null)
            .create()
            .show()
    }
}
