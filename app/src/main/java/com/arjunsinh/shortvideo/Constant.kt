package com.arjunsinh.shortvideo

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class Constant {


    companion object{
        var tag = "Arjun_Test"
        fun log(type: String, data: String, e: Throwable?) {
            Log.e(tag, "$tag $type : $data", e)
        }

        fun log(type: String, data: String) {
            Log.e(tag, "$tag $type : $data")
        }

        fun log(type: String, params: Map<String, String>) {
            var result = ""
            for ((key, value) in params) {
                result = """
            $result
            $key:$value
            """.trimIndent()
            }
            Log.e(tag, "$tag $type : $result")
        }

        fun checkUpdateAvailble(context: Context, activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
            val appUpdateManager = AppUpdateManagerFactory.create(context)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, activityResultLauncher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                }
            }
        }
    }
}