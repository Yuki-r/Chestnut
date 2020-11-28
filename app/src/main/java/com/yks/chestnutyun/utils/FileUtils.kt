package com.yks.chestnutyun.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import timber.log.Timber


/**
 * @Description:
 * @Author:         Yu ki-r
 * @CreateDate:     2020/11/27 23:34
 */
class FileUtils(private val context: Context) {

    fun getFilePathByUri(uri: Uri): String? {
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            return uri.path
        }
        // 以/storage开头的也直接返回
        if (isOtherDocument(uri)) {
            return uri.path
        }
        // 版本兼容的获取！
        var path = getFilePathByUriBelowAPI11(uri)
        if (path != null) {
            Timber.d("getFilePathByUri_BELOWAPI11获取到的路径为：$path")
            return path
        }
        path = getFilePathByUriAPI11To18(uri)
        if (path != null) {
            Timber.d("getFilePathByUri_API11to18获取到的路径为：$path")
            return path
        }
        path = getFilePathByUriAPI19(uri)
        Timber.d("getFilePathByUri_API19获取到的路径为：$path")
        return path
    }







    /**
     *
     * API 11 以下的版本路径的获取
     * @param uri
     * @return
     */
    private fun getFilePathByUriBelowAPI11(uri: Uri): String? {
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            var path: String? = null
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex)
                    }
                }
                cursor.close()
            }
            return path
        }
        return null
    }

    /**
     * API 版本11-18的路径的获取
     *
     * @param contentUri
     * @return
     */
    private fun getFilePathByUriAPI11To18(contentUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null
        val cursorLoader = CursorLoader(context, contentUri, projection, null, null, null)
        val cursor: Cursor ?= cursorLoader.loadInBackground()
        if (cursor != null) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(columnIndex)
            cursor.close()
        }
        return result
    }

    /**
     * API版本19的路径的获取
     *
     * @param uri
     * @return
     */
    private fun getFilePathByUriAPI19(uri: Uri): String? {
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT == uri.scheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return if (split.size > 1) {
                            Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        } else {
                            Environment.getExternalStorageDirectory().toString() + "/"
                        }
                        // This is for checking SD Card
                    }
                } else if (isDownloadsDocument(uri)) {
                    //下载内容提供者时应当判断下载管理器是否被禁用
                    val stateCode: Int = context.packageManager
                        .getApplicationEnabledSetting("com.android.providers.downloads")
                    if (stateCode != 0 && stateCode != 1) {
                        return null
                    }
                    var id = DocumentsContract.getDocumentId(uri)
                    // 如果出现这个RAW地址，我们则可以直接返回!
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    if (id.contains(":")) {
                        val tmp = id.split(":".toRegex()).toTypedArray()
                        if (tmp.size > 1) {
                            id = tmp[1]
                        }
                    }
                    var contentUri: Uri = Uri.parse("content://downloads/public_downloads")
                    Timber.d("测试打印Uri: $uri")
                    try {
                        contentUri = ContentUris.withAppendedId(contentUri, id.toLong())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    var path = getDataColumn(contentUri, null, null)
                    if (path != null) return path
                    // 兼容某些特殊情况下的文件管理器!
                    val fileName = getFileNameByUri(uri)
                    if (fileName != null) {
                        path = Environment.getExternalStorageDirectory().toString()
                            .toString() + "/Download/" + fileName
                        return path
                    }
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(contentUri, selection, selectionArgs)
                }
            }
        }
        return null
    }

    private fun getFileRelativePathByUriAPI18(uri: Uri): String? {
        val projection: Array<String>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection = arrayOf(
                MediaStore.MediaColumns.RELATIVE_PATH
            )
            context.contentResolver.query(uri, projection, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val index: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
                    return cursor.getString(index)
                }
            }
        }
        return null
    }

    private fun getFileNameByUri(uri: Uri): String? {
        var relativePath = getFileRelativePathByUriAPI18(uri)
        if (relativePath == null) relativePath = ""
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        context.contentResolver.query(uri, projection, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return relativePath + cursor.getString(index)
            }
        }
        return null
    }

    private fun getDataColumn(
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                .use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                        return cursor.getString(columnIndex)
                    }
                }
        } catch (iae: IllegalArgumentException) {
            iae.printStackTrace()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isOtherDocument(uri: Uri?): Boolean {
        // 以/storage开头的也直接返回
        if (uri?.path != null) {
            val path: String = uri.path!!
            if (path.startsWith("/storage")) {
                return true
            }
            if (path.startsWith("/external_files")) {
                return true
            }
        }
        return false
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.getAuthority()
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.getAuthority()
    }

}