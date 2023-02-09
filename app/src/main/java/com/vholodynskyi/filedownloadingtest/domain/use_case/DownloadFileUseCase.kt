package com.vholodynskyi.filedownloadingtest.domain.use_case

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vholodynskyi.filedownloadingtest.common.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor() {

    private val downloadsDirectory = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS
    )

    operator fun invoke(fileUrl: String, progress: MutableLiveData<Float>): Flow<Resource<String>> = flow {
        var inputStream: BufferedInputStream? = null
        var outputStream: FileOutputStream? = null
        var connection: HttpURLConnection? = null

        if (fileUrl.isBlank()) {
            emit(Resource.Error<String>("Url is incorrect"))
            return@flow
        }

        try {
            emit(Resource.Loading<String>())

            val downloadTarget = targetFile(fileName(fileUrl))

            connection = URL(fileUrl).openConnection() as HttpURLConnection

            inputStream = BufferedInputStream(connection.inputStream)
            outputStream = FileOutputStream(downloadTarget.path)

            val buffer = ByteArray(4096)
            var downloadedFileSize = 0L
            var currentRead = 0

            while (currentRead != -1 && currentCoroutineContext().isActive) {
                downloadedFileSize += currentRead
                outputStream.write(buffer, 0, currentRead)
                currentRead = inputStream.read(buffer, 0, buffer.size)


                // for testing only
                progress.postValue(((downloadedFileSize * 100)/connection.contentLength).toFloat())
                Log.d("DownloadingTest", "chunk ${progress.value}")

               delay(1000)
            }

            emit(Resource.Success<String>(downloadTarget.path))
        } catch (e: IOException) {
            emit(Resource.Error<String>(e.localizedMessage ?: "Couldn't reach server"))
        } finally {
            withContext(NonCancellable) {
                Log.d("DownloadingTest", "NonCancellable")
                connection?.disconnect()
                inputStream?.close()
                outputStream?.close()
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun fileName(fileUrl: String): String = fileUrl.substring(
        fileUrl.lastIndexOf("/") + 1, fileUrl.length
    ) + "${System.currentTimeMillis()}"

    private fun targetFile(fileName: String): File = File(downloadsDirectory, fileName)

}