package ru.ivan.eremin.treningtest.presenter.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView
import ru.ivan.eremin.treningtest.R

class LoadingTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialTextView(context, attrs, defStyleAttr), SkeletonView {

    private var oldBackground: Drawable? = null
    private var newBackgroundResId: Int? = null
    private var oldText: CharSequence? = null
    private var drawables: Array<Drawable>? = null

    private var loading: Boolean = false

    fun setLoadingBackground(backgroundResId: Int) {
        newBackgroundResId = backgroundResId
    }

    fun start() {
        if (!loading) {
            oldBackground = this.background
            oldText = this.text
            isEmojiCompatEnabled = false
            this.text = ""
            loading = true
            drawables = this.compoundDrawablesRelative
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            setBackgroundResource(newBackgroundResId ?: R.drawable.skeleton_background)
        }
    }

    @Suppress("MagicNumber")
    fun stop() {
        if (loading) {
            loading = false
            this.background = oldBackground
            this.text = oldText
            isEmojiCompatEnabled = true
            drawables?.let {
                this.setCompoundDrawablesRelativeWithIntrinsicBounds(it[0], it[1], it[2], it[3])
            }
        }
    }

    override fun setSkeletonOrNormal(skeleton: Boolean) {
        if (skeleton) {
            start()
        } else {
            stop()
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (!loading) {
            super.setText(text, type)
        } else {
            oldText = text
        }
    }

    override fun isSkeleton(): Boolean {
        return loading
    }
}