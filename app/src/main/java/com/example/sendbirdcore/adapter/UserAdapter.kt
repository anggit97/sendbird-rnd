package com.example.sendbirdcore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sendbirdcore.databinding.ListItemUserBinding
import com.example.sendbirdcore.model.User

/**
 * Created by Anggit Prayogo on 13/06/23.
 */
class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var userList = mutableListOf<User>()
    private lateinit var listener: OnItemClickListener

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setItems(list: MutableList<User>) {
        userList.clear()
        userList.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ListItemUserBinding, val listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: User) {
            with(binding) {
                binding.btnChat.setOnClickListener {
                    listener.onItemClick(item)
                }
                tvUser.text = item.nickname
                tvUserId.text = item.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int = userList.size
}

interface OnItemClickListener {
    fun onItemClick(user: User)
}