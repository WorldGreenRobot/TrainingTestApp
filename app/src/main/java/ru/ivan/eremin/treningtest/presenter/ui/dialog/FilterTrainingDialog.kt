package ru.ivan.eremin.treningtest.presenter.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.ivan.eremin.treningtest.R
import ru.ivan.eremin.treningtest.databinding.DialogTrainingFilterBinding
import ru.ivan.eremin.treningtest.domain.entity.Filter
import ru.ivan.eremin.treningtest.domain.entity.TypeTraining
import ru.ivan.eremin.treningtest.presenter.exteption.toPx
import ru.ivan.eremin.treningtest.presenter.ui.adapter.FilterAdapter
import ru.ivan.eremin.treningtest.presenter.ui.adapter.MarginItemDecoration

class FilterTrainingDialog : DialogFragment() {

    private var _binding: DialogTrainingFilterBinding? = null
    private val binding get() = _binding!!

    private var onClick: ((TypeTraining?) -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTrainingFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filters = arguments?.getSerializable(FILTER_FIELD)
        binding.filters.layoutManager = LinearLayoutManager(requireContext())
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_background)
        filters?.let {
            val adapter = FilterAdapter(it as ArrayList<Filter>)
            adapter.setOnClickListener {
                onClick?.invoke(it)
                dismiss()
            }
            binding.filters.addItemDecoration(MarginItemDecoration(requireContext().toPx(4)))
            binding.filters.adapter = adapter

        }
    }

    fun setOnClickFilter(onClick: (TypeTraining?) -> Unit) {
        this.onClick = onClick
    }

    companion object {
        private const val FILTER_FIELD = "filter"
        fun newInstance(
            filter: List<Filter>
        ): FilterTrainingDialog {
            return FilterTrainingDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(FILTER_FIELD, ArrayList(filter))
                }
            }
        }
    }
}