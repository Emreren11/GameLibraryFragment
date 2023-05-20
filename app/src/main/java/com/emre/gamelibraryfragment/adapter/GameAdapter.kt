package com.emre.gamelibraryfragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.emre.gamelibraryfragment.databinding.RecyclerRowBinding
import com.emre.gamelibraryfragment.model.Games
import com.emre.gamelibraryfragment.view.ListFragmentDirections

class GameAdapter(val gameList: List<Games>): RecyclerView.Adapter<GameAdapter.GameHolder>() {

    class GameHolder(val binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameHolder(binding)
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    override fun onBindViewHolder(holder: GameHolder, position: Int) {
        holder.binding.recyclerTextView.text = gameList[position].name
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToDetailFragment().setInfo("old").setId(gameList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}