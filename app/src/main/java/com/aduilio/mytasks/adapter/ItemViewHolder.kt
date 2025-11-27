package com.aduilio.mytasks.adapter

import android.content.Context
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.aduilio.mytasks.R
import com.aduilio.mytasks.databinding.ListItemBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.listener.ClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ItemViewHolder(
    private val binding: ListItemBinding,
    private val listener: ClickListener,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun setData(task: Task) {
        // Preenche os novos campos
        binding.tvTitle.text = task.title
        binding.tvDescription.text = task.description // Preenche a descrição
        binding.tvDate.text = formatDate(task.date) // Formata e preenche só a data
        binding.tvTime.text = formatTime(task.time) // Formata e preenche só a hora

        updateCardColor(task)
        updateTextAppearance(task.completed)

        val checkBox = binding.cbCompleted
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = task.completed

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != task.completed) {
                task.id?.let { id ->
                    listener.onComplete(id.toLong())
                }
            }
        }

        binding.root.setOnClickListener {
            listener.onClick(task)
        }
    }

    private fun updateTextAppearance(isCompleted: Boolean) {
        if (isCompleted) {
            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private fun updateCardColor(task: Task) {
        val colorResId = if (task.completed) {
            R.color.green
        } else {
            task.date?.let {
                val today = LocalDate.now()
                when {
                    it.isBefore(today) -> R.color.red
                    it.isEqual(today) -> R.color.yellow
                    else -> R.color.blue
                }
            } ?: R.color.blue
        }
        binding.cvTask.setCardBackgroundColor(ContextCompat.getColor(context, colorResId))
    }

    // Função para formatar apenas a DATA
    private fun formatDate(date: LocalDate?): String {
        if (date == null) return ""
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val format = preferences.getString("date_format", "numbers")
        val pattern = if (format == "extended") "dd 'de' MMMM 'de' yyyy" else "dd/MM/yyyy"
        return date.format(DateTimeFormatter.ofPattern(pattern, Locale("pt", "BR")))
    }

    // Função para formatar apenas a HORA
    private fun formatTime(time: java.time.LocalTime?): String {
        if (time == null) return ""
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}
