package ru.ivan.eremin.treningtest.presenter.ui.training

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView.FullscreenButtonClickListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.ivan.eremin.treningtest.R
import ru.ivan.eremin.treningtest.databinding.FragmentTrainingBinding
import ru.ivan.eremin.treningtest.domain.entity.Workout
import ru.ivan.eremin.treningtest.presenter.constants.BundleFields
import ru.ivan.eremin.treningtest.presenter.ui.base.BaseFragment

@UnstableApi
@AndroidEntryPoint
class TrainingFragment : BaseFragment() {

    private val viewModel: TrainingViewModel by viewModels()

    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!
    private var exoPlayer: ExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null
    private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
    private var videoTrackGroup: Tracks.Group? = null
    private var speedButton: TextView? = null
    private var qualityButton: TextView? = null
    private var isFullScreen = false
    private val speeds = arrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    private val speedLabels =
        arrayOf("0.5x", "0.75x", "Normal (1.0x)", "1.25x", "1.5x", "1.75x", "2.0x")
    private var originalLayoutParams: ViewGroup.LayoutParams? = null
    private var normalParent: ViewGroup? = null
    private var originalPlayerViewIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        val workout = requireArguments().getSerializable(BundleFields.WORKOUT) as Workout
        binding.toolbar.title = workout.title
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.playerView.setFullscreenButtonClickListener(
            fullScreenButtonListener
        )

        showDescription(workout.description)
        repeatOnStart {
            viewModel.state.collect {
                binding.swipeRefresh.isRefreshing = it.showRefresh
                showSkeleton(it.showSkeleton)
                initializePlayer(it.data?.link.orEmpty())
                if (it.error != null) {
                    val snackbar = Snackbar.make(view, it.error, Snackbar.LENGTH_LONG)
                    snackbar.setAction(getString(R.string.update)) {
                        viewModel.refresh()
                    }
                    snackbar.show()
                }
                it.data?.let {
                    showDuration(it.duration)
                }
            }
        }

        viewModel.setInitData(
            TrainingDetailInitData(
                trainingId = workout.id
            )
        )
    }

    private fun showDuration(duration: Int) {
        binding.durationTraining.text = requireContext().resources.getQuantityString(
            R.plurals.minutes,
            duration,
            duration
        )
    }

    private fun showDescription(description: String) {
        binding.description.text = description
    }

    private fun showSkeleton(isSkeleton: Boolean) {
        binding.durationTraining.setSkeletonOrNormal(isSkeleton)
        binding.description.setSkeletonOrNormal(isSkeleton)
    }

    private fun initializePlayer(url: String) {
        if (exoPlayer != null || url.isBlank()) return
        val context = requireContext()
        val trackSelector = DefaultTrackSelector(context)

        exoPlayer = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()

        binding.playerView.player = exoPlayer

        val source = if (url.contains("m3u8")) {
            getHlsMediaSource(url)
        } else {
            getProgressiveMediaSource(url)
        }

        exoPlayer?.setMediaSource(source)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true

        qualityButton = binding.playerView.findViewById(R.id.exo_quality_button)
        qualityButton?.setOnClickListener {
            showQualitySelectionDialog()
        }

        speedButton = binding.playerView.findViewById(R.id.exo_speed_button)
        speedButton?.setOnClickListener {
            showSpeedSelectionDialog()
        }

        exoPlayer?.addListener(playerListener)
    }

    private val fullScreenButtonListener = FullscreenButtonClickListener {
        if (it) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            openFullScreen()
        } else {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            exitFullScreen()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            openFullScreen()
            binding.playerView.setFullscreenButtonState(true)
            //requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exitFullScreen()
            binding.playerView.setFullscreenButtonState(false)
        }
    }

    private fun exitFullScreen() {
        if (!isFullScreen) return
        isFullScreen = false

        binding.fullscreenPlayerHost.removeView(binding.playerView)
        binding.fullscreenPlayerHost.visibility = View.GONE
        binding.detailVideo.visibility = View.VISIBLE

        if (normalParent != null && originalPlayerViewIndex != -1) {
            normalParent?.addView(binding.playerView, originalPlayerViewIndex, originalLayoutParams)
        } else if (normalParent != null) {
            normalParent?.addView(binding.playerView, originalLayoutParams)
        }

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        WindowInsetsControllerCompat(requireActivity().window, binding.playerView).show(
            WindowInsetsCompat.Type.systemBars()
        )
        val params = binding.playerView.layoutParams as ConstraintLayout.LayoutParams
        params.width = 0
        params.height = 0
        params.dimensionRatio = "16:9"
        binding.playerView.layoutParams = params
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.show()
        binding.durationTraining.visibility = View.VISIBLE
        binding.description.visibility = View.VISIBLE
    }

    private fun openFullScreen() {
        if (isFullScreen) return
        isFullScreen = true

        originalLayoutParams = binding.playerView.layoutParams

        if (binding.playerView.parent is ViewGroup) {
            normalParent = binding.playerView.parent as ViewGroup
            originalPlayerViewIndex = normalParent?.indexOfChild(binding.playerView) ?: -1
            normalParent?.removeView(binding.playerView)

        }

        binding.fullscreenPlayerHost.addView(
            binding.playerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        binding.fullscreenPlayerHost.visibility = View.VISIBLE
        binding.detailVideo.visibility = View.GONE

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, binding.playerView).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        val params = binding.playerView.layoutParams as FrameLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.playerView.layoutParams = params
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.hide()
        binding.durationTraining.visibility = View.GONE
        binding.description.visibility = View.GONE
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            val currentSpeed = playbackParameters.speed
            val formattedSpeed = "%.2fx".format(currentSpeed).replace(",", ".")
            speedButton?.text = formattedSpeed.replace("1.00x", "1.0x").replace("0.50x", "0.5x")
        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            updateAvailableQualities(tracks)
            updateQualityText()
        }
    }

    private fun showSpeedSelectionDialog() {
        val currentSpeed = exoPlayer?.playbackParameters?.speed
        var checkedItem = speeds.indexOfFirst { it == currentSpeed }
        if (checkedItem == -1) {
            checkedItem = speeds.indexOf(1.0f)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.change_playback_speed))
            .setSingleChoiceItems(speedLabels, checkedItem) { dialog, which ->
                val selectedSpeed = speeds[which]
                exoPlayer?.playbackParameters =
                    PlaybackParameters(selectedSpeed, 1.0f)
                dialog.dismiss()
            }
            .show()
    }

    private fun updateAvailableQualities(tracks: Tracks) {
        videoTrackGroup = null
        for (group in tracks.groups) {
            if (group.type == C.TRACK_TYPE_VIDEO) {
                videoTrackGroup = group
                break
            }
        }
    }

    private fun showQualitySelectionDialog() {
        val currentVideoTrackGroup = videoTrackGroup
        if (currentVideoTrackGroup == null || currentVideoTrackGroup.length == 0) {
            return
        }

        val qualityOptions = mutableListOf<String>()
        qualityOptions.add("Auto")

        val formatsForSelection = mutableListOf<Format>()

        for (i in 0 until currentVideoTrackGroup.length) {
            val format: Format = currentVideoTrackGroup.getTrackFormat(i)
            val label = "${format.height}p (${format.bitrate / 1000} kbps)"
            qualityOptions.add(label)
            formatsForSelection.add(format)
        }

        var checkedItemIndex = 0
        val currentTrackSelectionParameters = exoPlayer?.trackSelectionParameters
        val videoOverride =
            currentTrackSelectionParameters?.overrides?.get(currentVideoTrackGroup.mediaTrackGroup)

        if (videoOverride != null) {
            val selectedTrackIndexInOverride = videoOverride.trackIndices.firstOrNull()
            val selectedMediaTrackGroup = videoOverride.mediaTrackGroup

            if (selectedTrackIndexInOverride != null && selectedMediaTrackGroup == currentVideoTrackGroup.mediaTrackGroup) {
                val currentSelectedFormat =
                    selectedMediaTrackGroup.getFormat(selectedTrackIndexInOverride)
                val indexInOurList = formatsForSelection.indexOf(currentSelectedFormat)
                if (indexInOurList != -1) {
                    checkedItemIndex = indexInOurList + 1
                }
            }
        } else {
            for (group in exoPlayer?.currentTracks?.groups.orEmpty()) {
                if (group.type == C.TRACK_TYPE_VIDEO) {
                    for (i in 0 until group.length) {
                        if (group.isTrackSelected(i)) {
                            val activeFormat = group.getTrackFormat(i)
                            val indexInOurList = formatsForSelection.indexOf(activeFormat)
                            if (indexInOurList != -1) {
                                checkedItemIndex = indexInOurList
                                break
                            }
                        }
                    }
                }
                if (checkedItemIndex != 0) break
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.change_quality_video))
            .setSingleChoiceItems(
                qualityOptions.toTypedArray(),
                checkedItemIndex
            ) { dialog, which ->
                if (which == 0) {
                    exoPlayer?.trackSelectionParameters = exoPlayer?.trackSelectionParameters
                        ?.buildUpon()
                        ?.clearOverride(currentVideoTrackGroup.mediaTrackGroup)
                        ?.build()!!
                } else {
                    val selectedFormatIndexInFormatsForSelection = which
                    val selectedFormat =
                        formatsForSelection[selectedFormatIndexInFormatsForSelection - 1]

                    val trackIndexInMediaTrackGroup =
                        currentVideoTrackGroup.mediaTrackGroup.indexOf(selectedFormat)

                    if (trackIndexInMediaTrackGroup != C.INDEX_UNSET) {
                        val override = TrackSelectionOverride(
                            currentVideoTrackGroup.mediaTrackGroup,
                            trackIndexInMediaTrackGroup
                        )
                        exoPlayer?.trackSelectionParameters = exoPlayer?.trackSelectionParameters
                            ?.buildUpon()
                            ?.setOverrideForType(override)
                            ?.build()!!
                    }
                }
                updateQualityText()
                dialog.dismiss()
            }
            .show()
    }

    private fun updateQualityText() {
        val currentVideoTrackGroup = videoTrackGroup
        if (currentVideoTrackGroup == null || currentVideoTrackGroup.length == 0) {
            qualityButton?.text = "N/A"
            return
        }

        var currentQualityLabel = "Auto"

        val currentTrackSelectionParameters = exoPlayer?.trackSelectionParameters
        val videoOverride: TrackSelectionOverride? =
            currentTrackSelectionParameters?.overrides?.get(currentVideoTrackGroup.mediaTrackGroup)

        if (videoOverride != null) {
            val selectedTrackIndexInOverride = videoOverride.trackIndices.firstOrNull()
            if (selectedTrackIndexInOverride != null) {
                val currentSelectedFormat =
                    currentVideoTrackGroup.mediaTrackGroup.getFormat(selectedTrackIndexInOverride)
                currentQualityLabel = "${currentSelectedFormat.height}p"
            }
        }
        qualityButton?.text = currentQualityLabel
    }

    private fun getHlsMediaSource(url: String): MediaSource {
        return HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))
    }

    private fun getProgressiveMediaSource(url: String): MediaSource {
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onResume() {
        super.onResume()
        updateQualityText()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.playWhenReady = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        _binding = null
    }

    private fun releasePlayer() {
        exoPlayer?.stop()
        exoPlayer = null
        trackSelector = null
    }
}
