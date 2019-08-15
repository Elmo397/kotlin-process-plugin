@file:JvmName("Main")

package org.jetbrains.kotlin.process.bot.git

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.process.botutil.errorMessage
import org.jetbrains.kotlin.process.plugin.ui.merge.MergePullRequestDialog
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

const val SERVICE_NAME = "git merge branch"
const val schedule = "*/30 * * * * *" //TODO: change cron

lateinit var project: Project
lateinit var branchName: String

fun main() {
    try {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.start()

        val job = JobBuilder
            .newJob(IssuesJob::class.java)
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(schedule))
            .startNow()
            .build()

        println("is started: ${scheduler.isStarted}")
        println("name: ${scheduler.schedulerName}")

        MergePullRequestDialog(false).show() //TODO: You should work in scheduler, but you don't work there ¯\_(ツ)_/¯
        //scheduler.scheduleJob(job, trigger)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class IssuesJob : Job {
    override fun execute(context: JobExecutionContext?) {
        try {
            MergePullRequestDialog(false).show()
        } catch (e: Throwable) {
            e.printStackTrace()
            val errorMessage = e.errorMessage(SERVICE_NAME)
//            notifications.send(config.adminUserId, true, SERVICE_NAME, errorMessage).execute()
        }
    }
}