package ru.ivan.eremin.treningtest.presenter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.ivan.eremin.treningtest.R
import ru.ivan.eremin.treningtest.databinding.ItemTrainingBinding
import ru.ivan.eremin.treningtest.databinding.ItemTrainingSkeletonBinding
import ru.ivan.eremin.treningtest.domain.entity.Workout
import ru.ivan.eremin.treningtest.presenter.exteption.getString
import ru.ivan.eremin.treningtest.presenter.ui.entity.Training

class WorkoutAdapter : ListAdapter<Training, ViewHolder>(DiffCallback()) {

    private var onClickItem: ((Workout) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return when (viewType) {
            DATA_HOLDER -> DataViewHolder(
                ItemTrainingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            SKELETON_HOLDER -> SkeletonViewHolder(
                ItemTrainingSkeletonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw Exception("Illegal type holder")
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        when (holder) {
            is DataViewHolder -> {
                if (item is Training.Success) {
                    holder.bind(
                        item = getItem(position),
                        onClickItem = {
                            onClickItem?.invoke(it)
                        }
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Training.Skeleton -> SKELETON_HOLDER
            is Training.Success -> DATA_HOLDER
            else -> throw Exception("Illegal type holder")
        }
    }

    fun setOnClickListener(onClickItem: (Workout) -> Unit) {
        this.onClickItem = onClickItem
    }

    inner class SkeletonViewHolder(binding: ItemTrainingSkeletonBinding) : ViewHolder(binding.root)

    inner class DataViewHolder(private val binding: ItemTrainingBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Training, onClickItem: (Workout) -> Unit) {

            if (item is Training.Success) {
                binding.titleTraining.text = item.data.title
                if (item.data.description.isNotEmpty()) {
                    binding.descriptionTraining.visibility = View.VISIBLE
                } else {
                    binding.descriptionTraining.visibility = View.GONE
                }
                binding.descriptionTraining.text = item.data.description
                binding.typeTraining.text = item.data.type.getString(binding.root.context)
                binding.durationTraining.text = binding.root.context.resources.getQuantityString(
                    R.plurals.minutes,
                    item.data.duration,
                    item.data.duration
                )
                binding.container.setOnClickListener {
                    onClickItem(item.data)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Training>() {
        override fun areItemsTheSame(
            oldItem: Training,
            newItem: Training
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Training,
            newItem: Training
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val DATA_HOLDER = 1
        private const val SKELETON_HOLDER = 2
    }
}