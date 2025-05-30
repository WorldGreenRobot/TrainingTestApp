package ru.ivan.eremin.treningtest.presenter.view

interface SkeletonView {
    fun isSkeleton(): Boolean
    fun setSkeletonOrNormal(skeleton: Boolean)
}