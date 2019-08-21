package org.jetbrains.kotlin.process.plugin.rr.model

import com.intellij.ide.util.PropertiesComponent
import org.jetbrains.kotlin.process.plugin.messagesField
import org.jetbrains.kotlin.process.plugin.writeFoundedBuild
import org.jetbrains.kotlin.process.plugin.writeMessage
import org.jetbrains.teamcity.rest.*
import java.time.Instant

var success = true

fun checkRemoteRun() {
    val currentTime = System.currentTimeMillis() / 1000
    val ageLimit = currentTime - 24 * 3600 * 1 // one day

    val user = PropertiesComponent.getInstance().getValue("devNick")
    val builds = TeamCityInstanceFactory.guestAuth("https://teamcity.jetbrains.com") //todo: TeamCity plugin?
        .builds()
        .fromConfiguration(BuildConfigurationId("Kotlin_dev_AggregateBranch"))
        .withAllBranches()
        .includeCanceled()
        .includeRunning()
        .includeFailed()
        .since(Instant.ofEpochSecond(ageLimit))
        .all()
        .filter {
            it.branch.name!!.contains(user!!)
        }.toList()

    messagesField.text = "> Remote run check started\n\n"
    success = true
    for (build in builds) {
        writeFoundedBuild(build)

        val branchName = build.branch.name ?: continue
        val finishTime = build.finishDateTime?.toEpochSecond() ?: continue

        if(branchName == PropertiesComponent.getInstance().getValue("branchName")) {
            checkCurrentBranch(build)
        }
        writeMessage(build, branchName)
    }
    messagesField.text += "\n> Remote run check finished"
}

private fun checkCurrentBranch(build: Build) {
    if(build.state != BuildState.RUNNING && build.status == BuildStatus.FAILURE || build.status == BuildStatus.ERROR) {
        success = false
    }
}

