package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import com.udacity.asteroidradar.domain.Asteroid


class AsteroidAdapter(private val callback: AsteroidClick) :
    ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(AsteroidDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: AsteroidItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            AsteroidViewHolder.LAYOUT,
            parent,
            false
        )

        return AsteroidViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.binding.asteroid = getItem(position)
        holder.binding.asteroidCallback = callback
    }


    class AsteroidViewHolder(val binding: AsteroidItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.asteroid_item
        }
    }
}

class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }
}

class AsteroidClick(val block: (Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = block(asteroid)
}