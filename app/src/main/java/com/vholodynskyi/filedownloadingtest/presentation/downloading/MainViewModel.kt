package com.vholodynskyi.filedownloadingtest.presentation.downloading

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vholodynskyi.filedownloadingtest.domain.use_case.DownloadFileUseCase
import com.vholodynskyi.filedownloadingtest.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val downloadFileUseCase: DownloadFileUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DownloadingState())
    val state: State<DownloadingState> = _state

    private val fileUrl = "https://rl8r0h01fo.pdcdn1.top/dl2.php?id=60737129&h=7973d78e5006c3382040e49d7e1aff63&u=cache&ext=pdf&n=1-2-3%20magic%203-step%20discipline%20for%20calm%20effective%20and%20happy%20parenting" // DON'T FORGET TO SET YOUR URL
    private val testJob: Job
    private val _progress = MutableLiveData<Float>()
    var progress:LiveData<Float> =_progress

    init {
        testJob = downloadFiles()
    }

    private fun downloadFiles(): Job {
        return downloadFileUseCase(fileUrl, _progress)
            .onEach { result ->
                _state.value = when (result) {
                    is Resource.Error -> DownloadingState(error = result.message ?: "")
                    is Resource.Loading -> DownloadingState(isLoading = true)
                    is Resource.Success -> DownloadingState(file = result.data ?: "")
                }
            }.onCompletion {
                if (it is CancellationException) {
                    _state.value = DownloadingState(error = "Downloading is cancelled")
                }
            }.launchIn(viewModelScope)
    }

    fun cancelDownloading() {

        if (testJob.isActive) {
            testJob.cancel()
        }
    }

}