package id.altea.care.core.helper

import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock
import timber.log.Timber

interface FetchDownloadListener : FetchListener {
    override fun onAdded(download: Download) {
        Timber.e("ADDED")
    }

    override fun onCancelled(download: Download) {
        Timber.e("CANCEL")
    }

    override fun onCompleted(download: Download) {
        Timber.e("COMPLETE")
    }

    override fun onDeleted(download: Download) {
        Timber.e("DELETE")
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
        Timber.e("BLOCK UPDATE")
    }

    override fun onError(
        download: Download,
        error: Error,
        throwable: Throwable?
    ) {
        Timber.e("ERROR")
    }


    override fun onPaused(download: Download) {
        Timber.e("PAUSE")
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        Timber.e("PROGRESS")
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        Timber.e("QUEUE")
    }

    override fun onRemoved(download: Download) {
        Timber.e("REMOVE")
    }

    override fun onResumed(download: Download) {
        Timber.e("RESUME")
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        Timber.e("START")
    }

    override fun onWaitingNetwork(download: Download) {
        Timber.e("WAITING")
    }
}
