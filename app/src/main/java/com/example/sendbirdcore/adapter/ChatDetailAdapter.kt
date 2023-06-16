package com.example.sendbirdcore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sendbirdcore.databinding.ItemChatBinding
import com.example.sendbirdcore.databinding.ItemChatRegularBinding
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.SendingStatus
import com.sendbird.android.user.User

/**
 * Created by Anggit Prayogo on 14/06/23.
 */
class ChatDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listItems: MutableList<BaseMessage> = mutableListOf()
    private var currentUser: User? = null

    fun setCurrentUser(user: User) {
        currentUser = user
    }

    fun setItems(list: List<BaseMessage>) {
        listItems = list.toMutableList()
        notifyDataSetChanged()
    }

    fun addItem(message: BaseMessage) {
        listItems.add(message)
        notifyItemInserted(listItems.size - 1)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_USER) {
            val binding = ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return UserViewHolder(binding)
        }

        val binding = ItemChatRegularBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OtherViewHolder(binding)
    }

    class UserViewHolder(val parent: ItemChatBinding) : RecyclerView.ViewHolder(parent.root) {
        fun bind(item: BaseMessage) {
            with(parent) {
                tvMessage.text = item.message
                tvUser.text = item.sender?.nickname
                tvTime.text = item.createdAt.toString()
                Glide.with(itemView.context)
                    .load(item.sender?.profileUrl)
                    .into(parent.ivUserAvatar)
            }
        }
    }

    class OtherViewHolder(val parent: ItemChatRegularBinding) :
        RecyclerView.ViewHolder(parent.root) {
        fun bind(item: BaseMessage) {
            with(parent) {
                tvMessage.text = item.message
                tvTime.text = item.createdAt.toString()
                item.sendingStatus.let {
                    when (it) {
                        SendingStatus.SUCCEEDED -> {
                            parent.icCheckList.isVisible = true
                        }

                        SendingStatus.PENDING -> {
                            parent.icCheckList.isVisible = false
                        }

                        SendingStatus.FAILED -> {
                            parent.icCheckList.isVisible = false
                        }

                        SendingStatus.SCHEDULED -> {
                            parent.icCheckList.isVisible = false
                        }

                        SendingStatus.NONE -> {
                            parent.icCheckList.isVisible = false
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listItems[position]
        if (holder is UserViewHolder) {
            holder.bind(item)
        } else if (holder is OtherViewHolder) {
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (listItems[position].sender?.userId == currentUser?.userId) {
            return VIEW_TYPE_USER
        }
        return VIEW_TYPE_OTHER
    }

    override fun getItemCount(): Int = listItems.size

    companion object {
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_OTHER = 2
    }
}