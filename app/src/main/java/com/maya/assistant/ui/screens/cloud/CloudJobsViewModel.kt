package com.maya.assistant.ui.screens.cloud

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.maya.assistant.MayaApplication
import com.maya.assistant.cloud.CloudJob
import com.maya.assistant.cloud.CloudJobManager
import com.maya.assistant.cloud.ExecutionTarget
import com.maya.assistant.cloud.JobType
import kotlinx.coroutines.flow.StateFlow

class CloudJobsViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val cloudJobManager: CloudJobManager = mayaApp.cloudJobManager

    val jobs: StateFlow<List<CloudJob>> = cloudJobManager.jobs

    fun submitJob(title: String, type: JobType, target: ExecutionTarget, promptPayload: String) {
        cloudJobManager.submitJob(title, type, target, promptPayload)
    }

    fun pauseJob(id: String) {
        cloudJobManager.pauseJob(id)
    }

    fun resumeJob(id: String) {
        cloudJobManager.resumeJob(id)
    }

    fun retryJob(id: String) {
        cloudJobManager.retryJob(id)
    }

    fun deleteJob(id: String) {
        cloudJobManager.deleteJob(id)
    }
}
