package com.xtc.nsfw.sdk

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.DeadObjectException
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Message
import android.os.ParcelFileDescriptor
import android.os.RemoteException
import android.util.Log
import android.webkit.MimeTypeMap
import com.xtc.log.LogUtil
import com.xtc.nsfw.aidl.INsfwAidlInterface
import com.xtc.nsfw.aidl.INsfwCallBackAidlInterface
import com.xtc.nsfw.aidl.NSFWUpload
import com.xtc.system.account.WatchAccountBase
import com.xtc.system.account.constant.NotificationFlag
import java.io.File
import java.io.FileNotFoundException
import java.util.LinkedList
import java.util.Locale

/**
 * 鉴黄 SDK 工具类：负责与鉴黄服务建立绑定连接，并通过任务队列下发鉴黄请求。
 */
object NsfwSDKUtil {

    /** 鉴黄服务声明的 action。 */
    const val ACTION_BIND_SIDE_CHANNEL = "com.xtc.nsfw.NSFWService"

    private const val TAG = "NsfwSDKUtil"
    private const val MODULE_SWITCH_NSFW = 2786
    private const val PACKAGE_NAME = "com.xtc.xws"
    private const val ACTION_NAME = "com.xtc.nsfw.NSFWService"
    private const val CLASS_NAME = "com.xtc.nsfw.service.NSFWService"
    private const val SIDE_CHANNEL_RETRY_BASE_INTERVAL_MS = 1000
    private const val SIDE_CHANNEL_RETRY_MAX_COUNT = 6

    private val lock = Any()

    @Volatile
    private var nsfwCallback: INsfwCallBackAidlInterface? = null

    @Volatile
    private var sideChannelManager: SideChannelManager? = null

    /** 把任务放入侧信道队列，首次调用时懒创建连接管理器。 */
    private fun pushTask(context: Context, task: Task) {
        synchronized(lock) {
            if (sideChannelManager == null) {
                sideChannelManager = SideChannelManager(context.applicationContext)
            }
            sideChannelManager?.queueTask(task)
        }
    }

    /** 把一段逻辑放到侧信道线程执行，首次调用时懒创建连接管理器。 */
    private fun postToBackground(context: Context, action: () -> Unit) {
        synchronized(lock) {
            if (sideChannelManager == null) {
                sideChannelManager = SideChannelManager(context.applicationContext)
            }
            sideChannelManager?.post(action)
        }
    }

    /** 解绑鉴黄服务。 */
    private fun unbindNsfwService(context: Context) {
        try {
            LogUtil.d(TAG, "unbind()")
            synchronized(lock) {
                if (sideChannelManager == null) {
                    sideChannelManager = SideChannelManager(context.applicationContext)
                }
                val componentName = ComponentName(PACKAGE_NAME, CLASS_NAME)
                sideChannelManager?.handleServiceDisconnected(componentName)
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, e)
        }
    }

    /** 提交一个文件进行鉴黄。 */
    fun nsfw(context: Context, file: File, fileName: String, md5: String) {
        postToBackground(context) {
            if (!isNsfwServiceInstalled(context)) {
                LogUtil.d(TAG, "nsfw() --> 鉴黄服务 没有安装 ，不支持本地鉴黄")
                return@postToBackground
            }
            LogUtil.d(TAG, "nsfw() -->file: " + file + "\n fileName: " + fileName + "\n md5: " + md5.toLowerCase(Locale.US))
            if (!file.exists()) {
                LogUtil.e(TAG, "文件不存在，不调用鉴黄接口")
            }
            val mimeType = getMimeType(file)
            var descriptor: ParcelFileDescriptor? = null
            try {
                descriptor = ParcelFileDescriptor.open(file, NotificationFlag.NOTIFICATION_FLAG_CUSTOM)
            } catch (e: FileNotFoundException) {
                LogUtil.e(TAG, e.message)
            }
            if (descriptor == null) {
                LogUtil.e(TAG, "nsfw() --> pfd为null，不调用鉴黄接口")
                return@postToBackground
            }
            pushTask(context, NsfwTask(context.packageName, descriptor, fileName, mimeType, md5.toLowerCase(Locale.US)))
        }
    }

    /** 判断鉴黄服务是否已安装。 */
    private fun isNsfwServiceInstalled(context: Context): Boolean {
        val intent = Intent(ACTION_BIND_SIDE_CHANNEL)
        val componentName = ComponentName(PACKAGE_NAME, CLASS_NAME)
        val resolveInfos = context.packageManager.queryIntentServices(intent, 0)
        for (resolveInfo in resolveInfos) {
            val serviceInfo = resolveInfo.serviceInfo
            if (serviceInfo != null && componentName == ComponentName(serviceInfo.packageName, serviceInfo.name)) {
                return true
            }
        }
        return false
    }

    /** 构造绑定鉴黄服务的 Intent。 */
    private fun initNSFWServiceIntent(): Intent {
        val intent = Intent()
        intent.action = ACTION_NAME
        intent.setPackage(PACKAGE_NAME)
        intent.setClassName(PACKAGE_NAME, CLASS_NAME)
        return intent
    }
    /** 判断一批文件是否支持本地鉴黄。 */
    fun isSupportNSFW(context: Context, fileList: List<File>, result: (List<Boolean>) -> Unit) {
        postToBackground(context) {
            if (WatchAccountBase.queryModuleSwitchByBoolean(context, MODULE_SWITCH_NSFW, false)) {
                if (!isNsfwServiceInstalled(context)) {
                    LogUtil.d(TAG, "isSupportNSFW() -> 鉴黄服务 没有安装 ，不支持本地鉴黄")
                    val resultList = ArrayList<Boolean>()
                    for (ignored in fileList) {
                        resultList.add(false)
                    }
                    LogUtil.d(TAG, "isSupportNSFW() -> resultList:$resultList")
                    result(resultList)
                    return@postToBackground
                }
                pushTask(context, JudgeIsSupportNsfwListTask(context, fileList, result))
                return@postToBackground
            }
            LogUtil.d(TAG, "isSupportNSFW() -> 鉴黄的全网开关是关闭的 ，不支持本地鉴黄")
            val resultList = ArrayList<Boolean>()
            for (ignored in fileList) {
                resultList.add(false)
            }
            LogUtil.d(TAG, "isSupportNSFW() -> resultList:$resultList")
            result(resultList)
        }
    }

    /** 判断单个文件是否支持本地鉴黄。 */
    fun isSupportNSFW(context: Context, file: File, result: (Boolean) -> Unit) {
        postToBackground(context) {
            if (WatchAccountBase.queryModuleSwitchByBoolean(context, MODULE_SWITCH_NSFW, false)) {
                if (!isNsfwServiceInstalled(context)) {
                    LogUtil.d(TAG, "isSupportNSFW() -> 鉴黄服务 没有安装 ，不支持本地鉴黄")
                    result(false)
                    return@postToBackground
                }
                pushTask(context, JudgeIsSupportNsfwTask(context, file, result))
                return@postToBackground
            }
            LogUtil.d(TAG, "isSupportNSFW() -> 鉴黄的全网开关是关闭的 ，不支持本地鉴黄")
            result(false)
        }
    }

    /** 获取文件扩展名（小写），非法或不存在时返回 null。 */
    private fun getExtension(file: File?): String? {
        if (file == null || !file.exists() || file.isDirectory) {
            LogUtil.d(TAG, "getExtension() file:" + file?.absolutePath + " 不存在")
            return null
        }
        val fileName = file.name
        if (fileName != "" && !fileName.contains(".")) {
            val index = fileName.indexOf(".")
            if (index != -1) {
                return fileName.substring(index + 1).toLowerCase(Locale.US)
            }
            return null
        }
        LogUtil.d(TAG, "getExtension() fileName:$fileName ，文件名非法")
        return null
    }

    /** 根据扩展名推断 MIME 类型，推断失败时返回 file/star。 */
    private fun getMimeType(file: File?): String {
        val extension = getExtension(file)
        if (extension == null) {
            return "file/*"
        }
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "file/*"
        LogUtil.d(TAG, "getMimeType() file:" + file?.absolutePath + " ,extension:" + extension + ",mimeType:" + mimeType)
        return mimeType
    }

    /** 下发到鉴黄服务的任务。 */
    internal interface Task {
        fun send(service: INsfwAidlInterface?)
    }

    /** 鉴黄结果回调，收到解绑标记时主动断开服务。 */
    class NsfwCallBackAidlInterface(private val context: Context) : INsfwCallBackAidlInterface.Stub() {

        override fun onCallbackNSFWResult(bindPkgName: String?, result: NSFWUpload?, unbindService: Boolean) {
            LogUtil.d(TAG, "onCallbackNSFWResult() bindPkgName:$bindPkgName , result:$result ,unbindService:$unbindService")
            if (unbindService) {
                unbindNsfwService(context)
            }
        }
    }

    /** 判断单个文件是否支持鉴黄的任务。 */
    internal class JudgeIsSupportNsfwTask(
        private val context: Context,
        private val file: File,
        private val result: (Boolean) -> Unit
    ) : Task {

        override fun send(service: INsfwAidlInterface?) {
            try {
                val mimeType = getMimeType(file)
                if (service != null) {
                    result(service.isSupportNSFW(mimeType))
                } else {
                    LogUtil.e(TAG, "JudgeIsSupportNsfwTask send() service is null , 返回false")
                    result(false)
                }
            } catch (e: Exception) {
                LogUtil.e(TAG, "JudgeIsSupportNsfwTask send() crashed : ", e)
                result(false)
            }
        }

        override fun toString(): String = "JudgeIsSupportNsfwTask[file:$file, result:$result]"
    }

    /** 判断一批文件是否支持鉴黄的任务。 */
    internal class JudgeIsSupportNsfwListTask(
        private val context: Context,
        private val fileList: List<File>,
        private val result: (List<Boolean>) -> Unit
    ) : Task {

        override fun send(service: INsfwAidlInterface?) {
            try {
                val resultList = ArrayList<Boolean>()
                for (file in fileList) {
                    val mimeType = getMimeType(file)
                    if (service != null) {
                        resultList.add(service.isSupportNSFW(mimeType))
                    } else {
                        val falseList = ArrayList<Boolean>()
                        for (ignored in fileList) {
                            falseList.add(false)
                        }
                        LogUtil.e(TAG, "JudgeIsSupportNsfwTask send() service is null , 返回false列表")
                        result(falseList)
                    }
                }
                LogUtil.d(TAG, "isSupportNSFW() -> resultList:$resultList")
                result(resultList)
            } catch (e: Exception) {
                LogUtil.e(TAG, "JudgeIsSupportNsfwTask send() crashed : ", e)
                val falseList = ArrayList<Boolean>()
                for (ignored in fileList) {
                    falseList.add(false)
                }
                LogUtil.e(TAG, "JudgeIsSupportNsfwTask send() crashed , 返回false列表")
                result(falseList)
            }
        }

        override fun toString(): String = "JudgeIsSupportNsfwListTask[fileList:$fileList, result:$result]"
    }
    /** 通过文件描述符请求鉴黄的任务。 */
    internal class NsfwTask(
        private val packageName: String,
        private val pfd: ParcelFileDescriptor,
        private val fileName: String,
        private val mimeType: String,
        private val md5: String
    ) : Task {

        @Throws(RemoteException::class)
        override fun send(service: INsfwAidlInterface?) {
            if (service == null) {
                return
            }
            try {
                service.getNSFWByParcelFileDescriptor(packageName, pfd, fileName, mimeType, md5)
            } catch (e: Exception) {
                LogUtil.e(TAG, "NsfwTask send() crashed : ", e)
            }
        }

        override fun toString(): String =
            "NsfwTask[packageName:$packageName, pfd:$pfd, fileName:$fileName, mimeType:$mimeType, md5:$md5]"
    }

    /** 服务连接建立事件。 */
    private class ServiceConnectedEvent(val componentName: ComponentName, val iBinder: IBinder)

    /**
     * 侧信道连接管理器：在独立线程上维护与鉴黄服务的绑定，并顺序下发任务。
     */
    private class SideChannelManager(private val mContext: Context) : Handler.Callback, ServiceConnection {

        private val mHandlerThread = HandlerThread(TAG)
        private val mHandler: Handler
        private val listenerRecords: MutableMap<ComponentName, ListenerRecord> = HashMap()

        init {
            mHandlerThread.start()
            mHandler = Handler(mHandlerThread.looper, this)
        }

        /** 入队一个任务。 */
        fun queueTask(task: Task) {
            LogUtil.d(TAG, "QueueTask() task = $task")
            mHandler.obtainMessage(MSG_QUEUE_TASK, task).sendToTarget()
        }

        /** 在侧信道线程上执行一段逻辑。 */
        fun post(action: () -> Unit) {
            mHandler.post(action)
        }

        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                MSG_QUEUE_TASK -> {
                    val task = msg.obj as Task
                    handleQueueTask(task)
                    return true
                }
                MSG_SERVICE_CONNECTED -> {
                    val event = msg.obj as ServiceConnectedEvent
                    handleServiceConnected(event.componentName, event.iBinder)
                    return true
                }
                MSG_SERVICE_DISCONNECTED -> {
                    handleServiceDisconnected(msg.obj as ComponentName)
                    return true
                }
                MSG_RETRY_LISTENER_QUEUE -> {
                    handleRetryListenerQueue(msg.obj as ComponentName)
                    return true
                }
            }
            return false
        }

        private fun handleQueueTask(task: Task) {
            LogUtil.d(TAG, "handleQueueTask() task = $task")
            refreshListenerRecords()
            for (record in listenerRecords.values) {
                record.taskQueue.add(task)
                processListenerQueue(record)
            }
        }

        private fun handleServiceConnected(componentName: ComponentName, binder: IBinder) {
            try {
                LogUtil.d(TAG, "handleServiceConnected() ")
                val record = listenerRecords[componentName]
                if (record != null) {
                    record.service = INsfwAidlInterface.Stub.asInterface(binder)
                    if (nsfwCallback == null) {
                        nsfwCallback = NsfwCallBackAidlInterface(mContext)
                    }
                    try {
                        record.service?.registerListener(mContext.packageName, nsfwCallback)
                    } catch (e: Exception) {
                        LogUtil.e(TAG, "handleServiceConnected()  registerListener crashed : ", e)
                    }
                    record.retryCount = 0
                    processListenerQueue(record)
                }
            } catch (e: Exception) {
                LogUtil.e(TAG, "handleServiceConnected() crashed : ", e)
            }
        }

        fun handleServiceDisconnected(componentName: ComponentName) {
            LogUtil.d(TAG, "handleServiceDisconnected() ")
            val record = listenerRecords[componentName] ?: return
            unbindService(record)
        }

        private fun handleRetryListenerQueue(componentName: ComponentName) {
            LogUtil.d(TAG, "handleRetryListenerQueue() ")
            val record = listenerRecords[componentName] ?: return
            processListenerQueue(record)
        }

        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            LogUtil.d(TAG, "Connected to service $componentName")
            mHandler.obtainMessage(MSG_SERVICE_CONNECTED, ServiceConnectedEvent(componentName, binder)).sendToTarget()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            LogUtil.d(TAG, "Disconnected from service $componentName")
            mHandler.obtainMessage(MSG_SERVICE_DISCONNECTED, componentName).sendToTarget()
        }

        /** 刷新当前可用的鉴黄服务记录。 */
        private fun refreshListenerRecords() {
            val resolveInfos = mContext.packageManager.queryIntentServices(
                Intent().setAction(ACTION_BIND_SIDE_CHANNEL),
                PackageManager.GET_SERVICES
            )
            val currentComponents = HashSet<ComponentName>()
            for (resolveInfo in resolveInfos) {
                val componentName = ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name)
                if (resolveInfo.serviceInfo.permission != null) {
                    Log.w(TAG, "Permission present on component $componentName, not adding listener record.")
                } else {
                    currentComponents.add(componentName)
                }
            }
            for (componentName in currentComponents) {
                if (!listenerRecords.containsKey(componentName)) {
                    LogUtil.d(TAG, "Adding listener record for $componentName")
                    listenerRecords[componentName] = ListenerRecord(componentName)
                }
            }
            val iterator = listenerRecords.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (!currentComponents.contains(entry.key)) {
                    LogUtil.d(TAG, "Removing listener record for ${entry.key}")
                    unbindService(entry.value)
                    iterator.remove()
                }
            }
        }

        /** 确保与某个鉴黄服务的绑定已经建立。 */
        private fun isBound(record: ListenerRecord): Boolean {
            if (record.bound) {
                return true
            }
            record.bound = mContext.bindService(initNSFWServiceIntent(), this, Context.BIND_AUTO_CREATE)
            if (record.bound) {
                record.retryCount = 0
            } else {
                Log.w(TAG, "Unable to bind to listener ${record.componentName}")
                mContext.unbindService(this)
            }
            return record.bound
        }

        /** 断开与某个鉴黄服务的绑定。 */
        private fun unbindService(record: ListenerRecord) {
            if (record.bound) {
                mContext.unbindService(this)
                record.bound = false
            }
            record.service = null
        }

        /** 按指数退避安排重试。 */
        private fun retryListenerQueue(record: ListenerRecord) {
            if (mHandler.hasMessages(MSG_RETRY_LISTENER_QUEUE, record.componentName)) {
                return
            }
            record.retryCount += 1
            if (record.retryCount > SIDE_CHANNEL_RETRY_MAX_COUNT) {
                Log.w(
                    TAG,
                    "Giving up on delivering ${record.taskQueue.size} tasks to ${record.componentName} after ${record.retryCount} retries"
                )
                record.taskQueue.clear()
                return
            }
            val retryDelayMs = (1 shl (record.retryCount - 1)) * SIDE_CHANNEL_RETRY_BASE_INTERVAL_MS
            LogUtil.d(TAG, "Scheduling retry for $retryDelayMs ms")
            val message = mHandler.obtainMessage(MSG_RETRY_LISTENER_QUEUE, record.componentName)
            mHandler.sendMessageDelayed(message, retryDelayMs.toLong())
        }

        /** 顺序下发某个服务记录中排队中的任务。 */
        private fun processListenerQueue(record: ListenerRecord) {
            LogUtil.d(TAG, "Processing component ${record.componentName}, ${record.taskQueue.size} queued tasks")
            if (record.taskQueue.isEmpty()) {
                return
            }
            if (!isBound(record) || record.service == null) {
                retryListenerQueue(record)
                return
            }
            while (true) {
                val task = record.taskQueue.peek() ?: break
                try {
                    LogUtil.d(TAG, "Sending task $task")
                    task.send(record.service)
                    record.taskQueue.remove()
                } catch (e: DeadObjectException) {
                    LogUtil.d(TAG, "Remote service has died: ${record.componentName}")
                } catch (e: RemoteException) {
                    Log.w(TAG, "RemoteException communicating with ${record.componentName}", e)
                }
            }
            if (record.taskQueue.isNotEmpty()) {
                retryListenerQueue(record)
            }
        }

        /** 单个鉴黄服务的绑定记录。 */
        private class ListenerRecord(val componentName: ComponentName) {
            var bound: Boolean = false
            var service: INsfwAidlInterface? = null
            var taskQueue: LinkedList<Task> = LinkedList()
            var retryCount: Int = 0
        }

        companion object {
            private const val MSG_QUEUE_TASK = 0
            private const val MSG_SERVICE_CONNECTED = 1
            private const val MSG_SERVICE_DISCONNECTED = 2
            private const val MSG_RETRY_LISTENER_QUEUE = 3
        }
    }
}