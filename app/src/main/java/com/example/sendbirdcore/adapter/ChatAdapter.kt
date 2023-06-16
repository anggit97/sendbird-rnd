package com.example.sendbirdcore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sendbirdcore.databinding.ListItemChatBinding
import com.example.sendbirdcore.model.Chat
import com.example.sendbirdcore.model.Verified
import com.example.sendbirdcore.utils.DateTimeUtils
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Anggit Prayogo on 13/06/23.
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var chatList = mutableListOf<Chat>()
    private lateinit var listener: ChatClickListener

    fun setListener(listener: ChatClickListener) {
        this.listener = listener
    }

    fun setItems(list: MutableList<Chat>) {
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateItems(list: MutableList<Chat>){
//        val diffCallback = ChatDiffUtils(this.chatList, list)
//        val diff = DiffUtil.calculateDiff(diffCallback)
//
//        this.chatList.clear()
//        this.chatList.addAll(list)
//
//        diff.dispatchUpdatesTo(this)
        val currentChat = list.firstOrNull() ?: return
        val find = chatList.contains(currentChat)
        if (chatList.contains(currentChat)){
            val index = chatList.indexOf(currentChat)
            chatList[index] = currentChat
            notifyItemChanged(index)
        }
//        this.chatList.find { it.id == currentChat.id }.
    }

    class ViewHolder(
        private val binding: ListItemChatBinding,
        private val listener: ChatClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: Chat) {
            with(binding) {
                binding.root.setOnClickListener {
                    listener.onItemClick(item)
                }
                tvUser.text = item.name
                tvLastMessage.text = item.lastMessage
                ivVerified.isVisible = item.isVerifiedUser == Verified.VERIFIED.value
                tvTime.text = DateTimeUtils.convertEpochTime(item.createdAt)
                tvCountUnreadMessage.text = item.unreadMessageCount.toString()
                Glide.with(binding.root.context)
                    .load(item.profileUrl)
                    .into(binding.ivUserAvatar)
            }
        }
    }

    //create function to parse epoch time to format "dd/MM/yyyy"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = chatList[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int = chatList.size
}

class ChatDiffUtils(
    private val oldList: List<Chat>,
    private val newList: List<Chat>
) : DiffUtil.Callback() {

    enum class PayloadKey {
        VALUE
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        return listOf(PayloadKey.VALUE)
    }
}

interface ChatClickListener {
    fun onItemClick(chat: Chat)
}