package ru.ivan.eremin.treningtest.presenter.ui.trainings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.ivan.eremin.treningtest.R
import ru.ivan.eremin.treningtest.databinding.FragmentTrainingsBinding
import ru.ivan.eremin.treningtest.presenter.constants.BundleFields
import ru.ivan.eremin.treningtest.presenter.exteption.toPx
import ru.ivan.eremin.treningtest.presenter.ui.adapter.MarginItemDecoration
import ru.ivan.eremin.treningtest.presenter.ui.adapter.WorkoutAdapter
import ru.ivan.eremin.treningtest.presenter.ui.base.BaseFragment
import ru.ivan.eremin.treningtest.presenter.ui.dialog.FilterTrainingDialog
import ru.ivan.eremin.treningtest.presenter.ui.entity.Training

@AndroidEntryPoint
class TrainingsFragment : BaseFragment() {

    private val viewModel: TrainingsViewModel by viewModels()

    private var _binding: FragmentTrainingsBinding? = null
    private val binding get() = _binding!!

    private var workoutAdapter: WorkoutAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrainingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        createMenu()
        repeatOnStart {
            viewModel.state.collect {
                showTraining(it.data.orEmpty())
                binding.swipeRefresh.isRefreshing = it.showRefresh
                if (it.error != null) {
                    val snackbar = Snackbar.make(view, it.error, Snackbar.LENGTH_INDEFINITE)
                    snackbar.setAction(getString(R.string.update)) {
                        viewModel.refresh()
                    }
                    snackbar.show()

                }
            }
        }
    }

    private fun initUi() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        binding.trainings.layoutManager = LinearLayoutManager(requireContext())
        workoutAdapter = WorkoutAdapter()
        binding.trainings.adapter = workoutAdapter
        binding.trainings.addItemDecoration(
            MarginItemDecoration(requireContext().toPx(16))
        )
        workoutAdapter?.setOnClickListener {
            navController.navigate(
                R.id.action_trainingsFragment_to_trainingFragment,
                Bundle().apply {
                    putSerializable(BundleFields.WORKOUT, it)
                }
            )
        }
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
    }

    private fun createMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menuInflater.inflate(R.menu.training_app_bar_menu, menu)
                createSearchView(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        true
                    }

                    R.id.action_filter -> {
                        val dialogFragment =
                            FilterTrainingDialog.newInstance(viewModel.state.value.filters.orEmpty())
                        dialogFragment.setOnClickFilter {
                            viewModel.filter(it)
                        }
                        dialogFragment.show(childFragmentManager, "Filter")
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun createSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.search_title)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.search(query)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.search(newText)
                }
                return true
            }
        })
    }

    private fun showTraining(data: List<Training>) {
        workoutAdapter?.submitList(data)
    }
}