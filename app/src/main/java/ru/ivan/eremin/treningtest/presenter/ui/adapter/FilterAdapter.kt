package ru.ivan.eremin.treningtest.presenter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ivan.eremin.treningtest.databinding.ItemFilterBinding
import ru.ivan.eremin.treningtest.domain.entity.Filter
import ru.ivan.eremin.treningtest.domain.entity.TypeTraining
import ru.ivan.eremin.treningtest.presenter.exteption.getString

class FilterAdapter(private val list: List<Filter>) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    private var onClick: ((TypeTraining?) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(
            item = list[position],
            onClick = {
                onClick?.invoke(it)
            }
        )
    }

    fun setOnClickListener(onClick: (TypeTraining?) -> Unit) {
        this.onClick = onClick
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: ItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Filter, onClick: (TypeTraining?) -> Unit) {
            binding.name.text = item.typeTraining.getString(binding.root.context)
            binding.checkFilter.isChecked = item.isSelected

            binding.checkFilter.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onClick(item.typeTraining)
                } else {
                    onClick(null)
                }
            }

            binding.container.setOnClickListener {
                binding.checkFilter.isChecked = !binding.checkFilter.isChecked
            }
        }
    }
}