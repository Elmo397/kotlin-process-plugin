@file:JvmName("Main")

package org.jetbrains.kotlin.process.bot.git

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.project.Project
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepositoryManager
import org.eclipse.jgit.api.Git
import org.jetbrains.kotlin.process.botutil.errorMessage
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

const val SERVICE_NAME = "git merge branch"
const val schedule = "0/3 * * * * ?"

lateinit var project: Project
lateinit var branchName: String

lateinit var git: Git
    private set

fun main() {
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

    val vcsRepoManager = VcsRepositoryManager.getInstance(project)
    val brancher = GitBrancher.getInstance(project)
    val repositories = GitRepositoryManager(project, vcsRepoManager).repositories

    brancher.merge(branchName, GitBrancher.DeleteOnMergeOption.NOTHING, repositories)
    println("REBASED") //TODO: DELETE THIS SHIT!!!

    scheduler.scheduleJob(job, trigger)
}

class IssuesJob : Job {
    override fun execute(context: JobExecutionContext?) {
        try {
//            val vcsRepoManager = VcsRepositoryManager.getInstance(project)
//            val brancher = GitBrancher.getInstance(project)
//            val repositories = GitRepositoryManager(project, vcsRepoManager).repositories
//
//            brancher.rebase(repositories, branchName)
//            brancher.merge(branchName, GitBrancher.DeleteOnMergeOption.NOTHING, repositories)
//            println("REBASED") //TODO: DELETE THIS SHIT!!!
        } catch (e: Throwable) {
            e.printStackTrace()
            val errorMessage = e.errorMessage(SERVICE_NAME)
//            notifications.send(config.adminUserId, true, SERVICE_NAME, errorMessage).execute()
        }
    }
}

interface Config {
    val schedule: String
    val adminUserId: Int
    val usersEndPoint: String
    val notificationsEndPoint: String
}
