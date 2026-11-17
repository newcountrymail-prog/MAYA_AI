package com.maya.assistant.cloud

import com.maya.assistant.ai.MultiModelOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class JobType {
    HEAVY_REASONING,
    LARGE_CODING,
    IMAGE_GENERATION,
    IMAGE_EDITING,
    LONG_RESEARCH,
    DOCUMENT_ANALYSIS,
    CLOUD_TTS_SYNTHESIS
}

enum class JobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    PAUSED,
    FAILED
}

enum class ExecutionTarget {
    LOCAL_FIRST,
    CLOUD_ACCELERATED
}

data class CloudJob(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: JobType,
    val target: ExecutionTarget,
    val status: JobStatus = JobStatus.PENDING,
    val progress: Int = 0, // 0 - 100
    val result: String? = null,
    val logs: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val retryCount: Int = 0
)

class CloudJobManager(
    private val orchestrator: MultiModelOrchestrator
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val activeJobCoroutines = mutableMapOf<String, Job>()

    private val _jobs = MutableStateFlow<List<CloudJob>>(emptyList())
    val jobs: StateFlow<List<CloudJob>> = _jobs.asStateFlow()

    init {
        // Pre-populate with sample finished/pending jobs for dashboard review
        _jobs.value = listOf(
            CloudJob(
                title = "FLUX.1 Neumorphic Wallpapers",
                type = JobType.IMAGE_GENERATION,
                target = ExecutionTarget.CLOUD_ACCELERATED,
                status = JobStatus.COMPLETED,
                progress = 100,
                result = "Rendered 4K 16:9 wallpaper successfully",
                logs = listOf("Job scheduled on Cloud GPU", "FLUX latent diffusion complete", "Saved to local cache")
            ),
            CloudJob(
                title = "Multi-Source Tech Research",
                type = JobType.LONG_RESEARCH,
                target = ExecutionTarget.CLOUD_ACCELERATED,
                status = JobStatus.COMPLETED,
                progress = 100,
                result = "Compiled report with 5 verified citations",
                logs = listOf("Crawling authoritative web endpoints", "Synthesizing deep summary", "Markdown exported")
            )
        )
    }

    fun submitJob(
        title: String,
        type: JobType,
        target: ExecutionTarget = ExecutionTarget.CLOUD_ACCELERATED,
        promptPayload: String = ""
    ): String {
        val job = CloudJob(
            title = title,
            type = type,
            target = target,
            status = JobStatus.PENDING,
            progress = 0,
            logs = listOf("Job created and enqueued for ${target.name}")
        )
        val current = _jobs.value.toMutableList()
        current.add(0, job)
        _jobs.value = current

        startJobExecution(job.id, promptPayload)
        return job.id
    }

    private fun startJobExecution(jobId: String, promptPayload: String) {
        val coroutineJob = coroutineScope.launch {
            updateJobStatus(jobId, JobStatus.RUNNING, 10, "Execution started")

            for (p in 20..80 step 20) {
                delay(400)
                updateJobProgress(jobId, p, "Processing chunk at $p%...")
            }

            // Execute via orchestrator capability
            val capability = when (getJob(jobId)?.type) {
                JobType.HEAVY_REASONING -> "reasoning"
                JobType.LARGE_CODING -> "coding"
                JobType.IMAGE_GENERATION -> "image_generation"
                JobType.IMAGE_EDITING -> "image_editing"
                JobType.LONG_RESEARCH -> "research"
                JobType.DOCUMENT_ANALYSIS -> "documents"
                JobType.CLOUD_TTS_SYNTHESIS -> "voice"
                null -> "conversation"
            }

            val result = orchestrator.routeAndExecute(
                capability = capability,
                prompt = if (promptPayload.isNotBlank()) promptPayload else "Execute job for $capability",
                isFreeModeOnly = true
            )

            when (result) {
                is com.maya.assistant.ai.ExecutionResult.Success -> {
                    updateJobCompleted(jobId, result.content)
                }
                is com.maya.assistant.ai.ExecutionResult.Failure -> {
                    updateJobFailed(jobId, result.error)
                }
            }
        }
        activeJobCoroutines[jobId] = coroutineJob
    }

    fun pauseJob(jobId: String) {
        activeJobCoroutines[jobId]?.cancel()
        activeJobCoroutines.remove(jobId)
        updateJobStatus(jobId, JobStatus.PAUSED, null, "Paused by user")
    }

    fun resumeJob(jobId: String) {
        val job = getJob(jobId) ?: return
        if (job.status == JobStatus.PAUSED) {
            startJobExecution(jobId, "")
        }
    }

    fun retryJob(jobId: String) {
        val job = getJob(jobId) ?: return
        val current = _jobs.value.toMutableList()
        val index = current.indexOfFirst { it.id == jobId }
        if (index >= 0) {
            current[index] = job.copy(
                status = JobStatus.PENDING,
                progress = 0,
                retryCount = job.retryCount + 1,
                logs = job.logs + "Retrying execution (attempt ${job.retryCount + 1})"
            )
            _jobs.value = current
            startJobExecution(jobId, "")
        }
    }

    fun deleteJob(jobId: String) {
        activeJobCoroutines[jobId]?.cancel()
        activeJobCoroutines.remove(jobId)
        _jobs.value = _jobs.value.filter { it.id != jobId }
    }

    private fun getJob(id: String): CloudJob? = _jobs.value.firstOrNull { it.id == id }

    private fun updateJobStatus(id: String, status: JobStatus, progress: Int?, log: String) {
        val current = _jobs.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index >= 0) {
            val old = current[index]
            current[index] = old.copy(
                status = status,
                progress = progress ?: old.progress,
                logs = old.logs + log
            )
            _jobs.value = current
        }
    }

    private fun updateJobProgress(id: String, progress: Int, log: String) {
        val current = _jobs.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index >= 0) {
            val old = current[index]
            current[index] = old.copy(
                progress = progress,
                logs = old.logs + log
            )
            _jobs.value = current
        }
    }

    private fun updateJobCompleted(id: String, result: String) {
        val current = _jobs.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index >= 0) {
            val old = current[index]
            current[index] = old.copy(
                status = JobStatus.COMPLETED,
                progress = 100,
                result = result,
                completedAt = System.currentTimeMillis(),
                logs = old.logs + "Job successfully completed"
            )
            _jobs.value = current
        }
    }

    private fun updateJobFailed(id: String, error: String) {
        val current = _jobs.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index >= 0) {
            val old = current[index]
            current[index] = old.copy(
                status = JobStatus.FAILED,
                logs = old.logs + "Job failed: $error"
            )
            _jobs.value = current
        }
    }
}
