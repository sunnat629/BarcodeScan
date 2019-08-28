package dev.sunnat629.barcodescan.di.components

import dev.sunnat629.barcodescan.BSApplication
import dev.sunnat629.barcodescan.ui.BarcodeCaptureActivity
import dev.sunnat629.barcodescan.ui.MainActivity
import dev.sunnat629.barcodescan.viewmodels.BarcodeCViewModel
import dev.sunnat629.barcodescan.viewmodels.MainViewModel

interface AppComponentDefault {
    fun inject(app: BSApplication)
    fun inject(activity: MainActivity)
    fun inject(activity: BarcodeCaptureActivity)
    fun inject(viewModel: MainViewModel)
    fun inject(viewModel: BarcodeCViewModel)
}