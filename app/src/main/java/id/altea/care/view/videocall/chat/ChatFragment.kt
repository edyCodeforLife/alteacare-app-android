package id.altea.care.view.videocall.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.jakewharton.rxbinding3.widget.textChanges
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.Status
import com.twilio.chat.CallbackListener
import com.twilio.chat.Message
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.ext.*
import id.altea.care.core.helper.RxBus
import id.altea.care.core.helper.util.Constant
import id.altea.care.databinding.*
import id.altea.care.view.common.bottomsheet.FileChooserChatBottomSheet
import id.altea.care.view.common.bottomsheet.TypeFileSource
import id.altea.care.view.common.enums.StatusChat
import id.altea.care.view.videocall.VideoCallActivity
import id.altea.care.view.videocall.VideoCallVM
import id.altea.care.view.videocall.chat.item.ChatLeftItem
import id.altea.care.view.videocall.chat.item.ChatLoadingInitialItem
import id.altea.care.view.videocall.chat.item.ChatRightItem
import io.sentry.Sentry
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

class ChatFragment : BaseFragmentVM<FragmentChatBinding, VideoCallVM>(), PickiTCallbacks {

    private val itemAdapter = ItemAdapter<IItem<*>>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private lateinit var identity: String
    private var imagePath: File? = null
    private var isChatConnected = false
    private val pickIt by lazy { PickiT(requireContext(), this, requireActivity()) }
    private var fetchConfiguration: FetchConfiguration? = null
    private var positionAdapter: Int? = 0

    private var hasSendMessage: Boolean = false

    private val doctorName by lazy {
        requireArguments().getString(EXTRA_DOCTOR_NAME)
    }

    private val typeVideo by lazy {
        requireArguments().getSerializable(TYPE_VIDEO) as VideoView
    }

    override fun observeViewModel(viewModel: VideoCallVM) {
        observe(viewModel.message, ::onReceivedNewMessage)
        observe(viewModel.senderMessageCallbackEvent, ::onUpdateStatusMessage)
        observe(viewModel.senderMessageProgressEvent, { onUpdateProgressMessage() })
        observe(viewModel.statusDownloadLiveData, ::getStatusDownload)
        observe(viewModel.progressDownloadLiveData, ::getProgressDownload)

        viewModel.chatConnectionStatus
            .subscribe(
                {
                    requireActivity().runOnUiThread {
                        if (it.first) {
                            // todo if error twillio client
                        }
                        if (it.second) {
                            isChatConnected = true
                            hideLoadingInitialChat()
                        }
                    }
                }, {
                    Sentry.captureMessage(it.toString())
                    Timber.e(it)
                }
            )
            .disposedBy(disposable)
    }

    override fun bindViewModel(): VideoCallVM {
        return ViewModelProvider(
            requireActivity().viewModelStore,
            viewModelFactory
        ).get(VideoCallVM::class.java)
    }

    override fun getUiBinding(): FragmentChatBinding {
        return FragmentChatBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        fetchConfiguration = FetchConfiguration.Builder(requireContext()).build()
        identity = requireArguments().getString(EXTRA_IDENTITY).orEmpty()
        viewBinding?.run {
            chatRecycler.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastAdapter
            }
        }

        RxBus.post(
            VideoViewType(typeVideo)
        )
        viewModel?.getLastMessage()
        showLoadingInitialChat()
    }

    override fun onReExecute() {}

    override fun initUiListener() {
        viewBinding?.run {
            chatBtnSend.onSingleClick()
                .subscribe {

                    if (!isChatConnected) {
                        showToast(getString(R.string.chat_screen_message_loading))
                        return@subscribe
                    }

                    if (!hasSendMessage) {
                        viewModel?.sendTrackingLastDoctorChatName(doctorName)
                        hasSendMessage = true
                    }

                    itemAdapter.add(
                        ChatRightItem(
                            messageBody = edtxMessage.text.toString(),
                            mimeType = "text",
                            status = StatusChat.LOADING
                        )
                    )
                    sendChatMessage(fastAdapter.itemCount - 1)
                    edtxMessage.text = null
                }.disposedBy(disposable)

            chatBtnAttachment.onSingleClick().subscribe {
                if (!isChatConnected) {
                    showToast(getString(R.string.chat_screen_message_loading))
                    return@subscribe
                }
                FileChooserChatBottomSheet.newInstance {
                    when (it) {
                        TypeFileSource.GALLERY -> openGalery()
                        else -> openStorage()
                    }
                }.show(childFragmentManager, "file")
            }.disposedBy(disposable)

            chatBtnCamera.onSingleClick().subscribe {
                if (!isChatConnected) {
                    showToast(getString(R.string.chat_screen_message_loading))
                    return@subscribe
                }
                openCamera()
            }.disposedBy(disposable)

            edtxMessage.textChanges()
                .subscribe {
                    chatBtnSend.isVisible = it.toString().isNotEmpty()
                    chatBtnAttachment.isVisible = it.toString().isEmpty()
                    chatBtnCamera.isVisible = it.toString().isEmpty()
                }
                .disposedBy(disposable)

            chatBtnClose.onSingleClick()
                .subscribe {
                    when(typeVideo){
                        VideoView.TWILLIO ->{
                            (requireActivity() as VideoCallActivity).let {
                                it.router.openVideoFragment(it)
                            }
                        }
                        VideoView.JITSI -> {
                            (requireActivity() as VideoCallActivity).let {
                                it.router.openVideoWebViewFragment(it)
                            }
                        }
                    }

                }
                .disposedBy(disposable)


            fastAdapter.addEventHook(object : ClickEventHook<IItem<*>>() {

                override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                    if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemMessageRightBinding) {
                        return listOf(
                            (viewHolder.binding as ItemMessageRightBinding).itemChatImgRight,
                            (viewHolder.binding as ItemMessageRightBinding).itemChatRightPdfMessage
                        )
                    } else if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemMessageLeftBinding) {
                        return listOf(
                            (viewHolder.binding as ItemMessageLeftBinding).itemImgLeftMessage,
                            (viewHolder.binding as ItemMessageLeftBinding).itemChatLeftPdfMessage
                        )
                    }
                    return null
                }

                override fun onClick(
                    v: View,
                    position: Int,
                    fastAdapter: FastAdapter<IItem<*>>,
                    item: IItem<*>
                ) {
                    if (item is ChatRightItem) {
                        when (v.id) {
                            R.id.itemChatImgRight -> {
                                doClickImage(item.message, item.file)
                            }

                            R.id.itemChatRightPdfMessage -> {
                                doClickDocument(item.message, item.file, position)
                            }
                        }
                    } else if (item is ChatLeftItem) {
                        when (v.id) {
                            R.id.itemImgLeftMessage -> {
                                doClickImage(item.message, null)
                            }

                            R.id.itemChatLeftPdfMessage -> {
                                doClickDocument(
                                    item.message,
                                    File(getPathLocal() + item.message.media.fileName),
                                    position
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    private fun doClickDocument(message: Message?, file: File?, position: Int) {
        positionAdapter = position
        if (message?.hasMedia() == true) {
            message.media.getContentTemporaryUrl(object :
                CallbackListener<String>() {
                override fun onSuccess(mediaContentUrl: String?) {
                    if (message.media.type.contains("application") == true) {
                        mediaContentUrl?.apply {
                            if (pathToDownload(message.media.fileName) == false) {
                                viewModel?.startDownload(
                                    mediaContentUrl,
                                    getPathLocal(),
                                    message.media.fileName,
                                    fetchConfiguration!!
                                )
                            } else {
                                file?.let {
                                    (requireActivity() as AppCompatActivity).openDocument(
                                        getfileUri(it)
                                    )
                                }
                            }
                        }
                    }
                }
            })
        } else {
            file?.let { (requireActivity() as AppCompatActivity).openDocument(getfileUri(it)) }
        }
    }

    private fun getfileUri(file: File): Uri {
        return FileProvider.getUriForFile(
            requireContext(),
            Constant.FILE_PROVIDER,
            file
        )

    }

    private fun doClickImage(item: Message?, file: File?) {
        if (item?.hasMedia() == true) {
            item.media.getContentTemporaryUrl(object :
                CallbackListener<String>() {
                override fun onSuccess(mediaContentUrl: String?) {
                    if (item.media.type.contains("image") == true) {
                        mediaContentUrl?.apply {
                            showDialogImage(
                                requireContext(),
                                file,
                                mediaContentUrl
                            )
                        }
                    }
                }
            })
        } else {
            showDialogImage(requireContext(), file, "")
        }
    }

    private fun onReceivedNewMessage(message: Pair<Message,Boolean>?) {
        try {
            message?.let {
                if (it.first.member.identity != identity) {
                    itemAdapter.add(ChatLeftItem(it.first, StatusChat.SUCCESS))
                }else{
                    if (it.second) {
                        itemAdapter.add(
                            ChatRightItem(
                                message = message.first,
                                status = StatusChat.SUCCESS
                            )
                        )
                    }else{}
                }
            }
            viewBinding?.chatRecycler?.scrollToPosition(fastAdapter.itemCount - 1)
        }catch (e : Exception){
            Sentry.captureException(e)
        }

    }


    // Pair<isSuccess, adapterPosition>
    private fun onUpdateStatusMessage(messageUpdate: Pair<Boolean, Int>?) {
        try {
            messageUpdate?.let {
                val items = fastAdapter.getItem(it.second) as ChatRightItem
                if (it.first) {
                    fastAdapter.notifyAdapterItemChanged(
                        it.second,
                        items.apply { status = StatusChat.SUCCESS }
                    )
                } else {
                    fastAdapter.notifyAdapterItemChanged(
                        it.second,
                        items.apply { status = StatusChat.ERROR }
                    )
                    showToast(getString(R.string.str_retry_error))
                }
            }
        }catch (e : Exception){
            Sentry.captureException(e)
        }
    }

    // Triple<isCompleted, sizeUploaded, adapterPosition>
    private fun onUpdateProgressMessage() {
        // todo update progress upload
    }

    private fun sendChatMessage(adapterPosition: Int) {
        val items = fastAdapter.getItem(adapterPosition) as ChatRightItem
        val options = Message.options().withBody(items.messageBody)
        viewModel?.sendMessage(options, false, adapterPosition)
    }

    private fun sendFileMessage(adapterPosition: Int) {
        val items = fastAdapter.getItem(adapterPosition) as ChatRightItem
        val file = items.file!!
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = FileInputStream(file)
        } catch (exeption: Exception) {
            exeption.printStackTrace()
        }
        Message.options()
            .withMedia(fileInputStream, items.mimeType)
            .withMediaFileName(file.name).let {
                viewModel?.sendMessage(it, true, adapterPosition)
            }
    }

    private fun openStorage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra("return-data", true)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        resultStorage.launch(intent)
    }

    private fun openCamera() {
        imagePath = requireActivity().createDefaultImagePath()
        val providerFile =
            FileProvider.getUriForFile(requireContext(), Constant.FILE_PROVIDER, imagePath!!)
        resultCamera.launch(providerFile)
    }

    private fun openGalery() {
        resultGalery.launch(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        )
    }

    private val resultStorage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                result.data?.data?.let {
                    pickIt.getPath(it, Build.VERSION.SDK_INT)
                }
        }

    private val resultCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) imagePath?.let { file ->
                addAdapterFile(file)
            }
        }

    private val resultGalery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it?.data?.data?.let { uri ->
                    requireActivity().getRealPathURI(uri)
                        ?.also { file -> addAdapterFile(file) }
                }

            }
        }

    override fun PickiTonUriReturned() {}

    override fun PickiTonProgressUpdate(progress: Int) {}

    override fun PickiTonStartListener() {}

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (wasSuccessful && path != null) {
            val file = File(path)
            when {
                file.absolutePath.endsWith(".pdf") -> {
                    addAdapterFile(file)
                }
                file.absolutePath.contains(Regex(REGEX_IMAGE)) -> {
                    addAdapterFile(file)
                }
                else -> showToast(getString(R.string.toast_upload_error_file_unsupported))
            }
        } else {
            showToast(getString(R.string.toast_upload_error_file_not_found))
        }
    }

    private fun showLoadingInitialChat() {
        itemAdapter.add(0, ChatLoadingInitialItem())
    }

    private fun hideLoadingInitialChat() {
        if (itemAdapter.getAdapterItem(0) is ChatLoadingInitialItem) {
            itemAdapter.remove(0)
        }
    }

    private fun pathToDownload(nameFile: String): Boolean {
        val filePath =
            File(getPathLocal() + nameFile)
        return filePath.isFile
    }

    private fun getPathLocal(): String {
        return requireActivity().getTempFolder().absolutePath
    }

    private fun getProgressDownload(progress: Int?) {

    }


    private fun addAdapterFile(file: File) {
        if (isFileSizeMaximum(file.length() / 1024)) {
            (requireActivity() as BaseActivity<*>).showErrorSnackbar(getString(R.string.str_file_size))
            return
        }
        itemAdapter.add(
            ChatRightItem(
                mimeType = if (file.absolutePath.contains("pdf")) "application/pdf" else "image/*",
                status = StatusChat.LOADING,
                file = File(file.absolutePath)
            )
        )
        sendFileMessage(fastAdapter.itemCount - 1)
    }

    private fun getStatusDownload(status: Status?) {
        val item = fastAdapter.getItem(positionAdapter!!)
        when (item) {
            is ChatRightItem -> {
                when (status) {
                    Status.DOWNLOADING -> {
                        fastAdapter.notifyAdapterItemChanged(
                            positionAdapter!!,
                            item.also {
                                it.status = StatusChat.LOADING
                            })
                    }
                    Status.COMPLETED -> {
                        fastAdapter.notifyAdapterItemChanged(
                            positionAdapter!!,
                            item.let {
                                it.status = StatusChat.SUCCESS
                            })
                    }
                    else -> {
                    }
                }
            }
            is ChatLeftItem -> {
                when (status) {
                    Status.DOWNLOADING -> {
                        fastAdapter.notifyAdapterItemChanged(
                            positionAdapter!!,
                            item.also {
                                it.status = StatusChat.LOADING
                            })
                    }
                    Status.COMPLETED -> {
                        fastAdapter.notifyAdapterItemChanged(
                            positionAdapter!!,
                            item.also {
                                it.status = StatusChat.SUCCESS
                            })
                    }
                    else -> {
                    }
                }
            }

        }


    }

    private fun isFileSizeMaximum(sizeFile: Long): Boolean {
        return sizeFile >= 10000
    }


    override fun initMenu(): Int = 0

    companion object {
        private const val EXTRA_IDENTITY = "ChatBottom.Identity"
        private const val EXTRA_DOCTOR_NAME= "EXTRA_DOCTOR_NAME"
        private const val REGEX_IMAGE = "([^\\s]+(\\.(?i)(jpe?g|png|gif|bmp))$)"
        private const val TYPE_VIDEO = "ChatBottom.TypeVideo"

        fun createInstance(
            identity: String,
            doctorName: String?,
            typeVideo : VideoView
        ): ChatFragment {
            return ChatFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable(TYPE_VIDEO,typeVideo)
                bundle.putString(EXTRA_IDENTITY, identity)
                bundle.putString(EXTRA_DOCTOR_NAME, doctorName)
                this.arguments = bundle
            }
        }
    }

    enum class VideoView {
        TWILLIO,JITSI
    }
    data class VideoViewType(val videoView : VideoView)
}
