package com.example.chatapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.ui.constact.LEFT_CHAT
import com.example.chatapp.ui.constact.RIGHT_CHAT
import com.example.chatapp.user.User
import kotlinx.android.synthetic.main.chat_left.view.*
import kotlinx.android.synthetic.main.fragment_chat.view.*

class ChatAdapter:RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private lateinit var list :List<Chat>

    class ChatViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if(viewType == RIGHT_CHAT){
            ChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_right,parent,false))
        }
        else{
            ChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_left,parent,false))
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        if (list[position].sent.equals("true")){
            holder.itemView.findViewById<TextView>(R.id.chat_right).text = list[position].text
            if(position == list.size - 1){
                if (list[position].seen.equals("true")){
                    holder.itemView.findViewById<TextView>(R.id.seen_txt).text = "Đã xem"
                }else{
                    holder.itemView.findViewById<TextView>(R.id.seen_txt).text = "Đã gửi"
                }
            }
            else{
                holder.itemView.findViewById<TextView>(R.id.seen_txt).visibility = View.GONE
            }
        }
        else{
            Glide.with(holder.itemView.circle_message.context).load(list[position].image).into(holder.itemView.circle_message)
            holder.itemView.findViewById<TextView>(R.id.chat_left).text = list[position].text
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
        fun setData(list: List<Chat>){
            this.list = list
            notifyDataSetChanged()

        }
    override fun getItemViewType(position: Int): Int {
        return if (list[position].sent.equals("true")){
            RIGHT_CHAT
        }
        else{
            LEFT_CHAT
        }
    }

}