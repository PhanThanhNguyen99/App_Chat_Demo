package com.example.chatapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentListBinding
import com.example.chatapp.databinding.FragmentListFriendBinding
import com.example.chatapp.databinding.ListfriendItemBinding
import com.example.chatapp.user.User
import com.google.firebase.database.core.Context
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.listfriend_item.view.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ListFriendAdapter: RecyclerView.Adapter<ListFriendAdapter.MyViewHolder>() {
    private lateinit var list :List<User>

    class MyViewHolder(val binding: ListfriendItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListfriendItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        val data2 = sdf.parse(currentDate)

        if (list[position].status.equals("online")){
            holder.binding.statusOn.visibility = View.VISIBLE
            holder.binding.statusOff.visibility = View.GONE
            holder.binding.nameFriend.text = list[position].name
            Glide.with(holder.binding.avatarFriend.context).load(list[position].image).into(holder.binding.avatarFriend)
        }else{
            val data :Date = sdf.parse(list[position].status)
            val diff = (data2.time- data.time)/(60*1000)
            if(diff>60){
                holder.binding.statusOff.visibility =View.GONE
            }else{
                holder.binding.statusOff.visibility =View.VISIBLE
                holder.binding.statusOff.text = "${diff}phút"

            }
            holder.binding.statusOn.visibility = View.GONE
            holder.binding.nameFriend.text = list[position].name
            Glide.with(holder.binding.avatarFriend.context).load(list[position].image).into(holder.binding.avatarFriend)
        }
        holder.binding.listFriendItem.setOnClickListener{
               val action = ListFriendFragmentDirections.actionListFriendFragmentToChatFragment(list[position])
             holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun setData(list: List<User>){
        this.list = list

        notifyDataSetChanged()
    }
}