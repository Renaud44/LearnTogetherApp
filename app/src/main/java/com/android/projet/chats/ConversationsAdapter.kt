package com.android.projet.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.User

class ConversationsAdapter(
    private val users: MutableList<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pseudo: TextView = view.findViewById(R.id.text_pseudo)
        val lastMessage: TextView = view.findViewById(R.id.text_last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.pseudo.text = user.pseudo
        holder.lastMessage.text = user.lastMessage ?: ""

        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }
}