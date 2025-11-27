package com.aduilio.mytasks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aduilio.mytasks.R
import com.aduilio.mytasks.databinding.ListItemBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.listener.ClickListener

// AJUSTE: Removido o parâmetro "tasks" do construtor, pois a lista é gerenciada internamente.
class ListAdapter(
    private val context: Context,
    private val emptyMessage: TextView,
    private val listener: ClickListener
) : RecyclerView.Adapter<ItemViewHolder>() {

    // A sua lista interna. Perfeito!
    private val items = mutableListOf<Task>()

    // NENHUMA MUDANÇA NECESSÁRIA AQUI. Já está correto.
    // Ele cria o ViewHolder e já passa o listener e o context.
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ItemViewHolder(binding, listener, context)
    }

    // NENHUMA MUDANÇA NECESSÁRIA AQUI. Já está correto.
    // Conecta os dados da lista interna com o ViewHolder.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setData(items[position])
    }

    // O restante do seu código já estava ótimo e não precisa de alterações.
    // Ele lida com a atualização da lista, remoção de itens e a mensagem de lista vazia.

    override fun getItemCount() = items.size

    fun getItem(position: Int) = items[position]

    fun setData(data: List<Task>) {
        items.clear()
        items.addAll(data)
        // notifyDataSetChanged() é funcional, mas para animações melhores no futuro,
        // considere usar DiffUtil para uma atualização mais eficiente. Por agora, está perfeito.
        notifyDataSetChanged()

        checkEmptyList()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)

        checkEmptyList()
    }

    private fun checkEmptyList() {
        if (items.isEmpty()) {
            emptyMessage.visibility = View.VISIBLE
            emptyMessage.text = ContextCompat.getString(
                context, R.string.empty_list
            )
        } else {
            emptyMessage.visibility = View.INVISIBLE
        }
    }
}
