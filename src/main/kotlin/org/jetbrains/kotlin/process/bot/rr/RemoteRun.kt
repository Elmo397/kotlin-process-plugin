package org.jetbrains.kotlin.process.bot.rr

import com.intellij.ide.util.PropertiesComponent
import org.jetbrains.kotlin.process.plugin.ui.rr.messagesField
import org.jetbrains.kotlin.process.plugin.ui.rr.writeFoundedBuild
import org.jetbrains.kotlin.process.plugin.ui.rr.writeMessage
import org.jetbrains.kotlin.process.util.stateful.Stateful
import org.jetbrains.kotlin.process.util.stateful.read
import org.jetbrains.kotlin.process.util.stateful.write
import org.jetbrains.teamcity.rest.*
import java.time.Instant

data class State(val lastCheckTime: Long, val recordedBuilds: Map<String, Long>) : Stateful<State>

var success = true

fun checkRemoteRun() {
    val currentTime = System.currentTimeMillis() / 1000
    val ageLimit = currentTime - 24 * 3600 * 1 // one day

    val state = Stateful.read<State>(stateFile) ?: State(0, emptyMap())

    val user = PropertiesComponent.getInstance().getValue("devNick")
    val builds = TeamCityInstanceFactory.guestAuth("https://teamcity.jetbrains.com")
        .builds()
        .fromConfiguration(BuildConfigurationId("Kotlin_dev_AggregateBranch"))
        .withAllBranches()
        .includeCanceled()
        .includeRunning()
        .includeFailed()
        .since(Instant.ofEpochSecond(ageLimit))
        .all()
/*        .filter {
            it.branch.name!!.contains(user!!)
        }*/.toList()

    val newRecordedBuilds = state.recordedBuilds.filterTo(HashMap()) { (_, v) -> v > ageLimit }

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

        newRecordedBuilds[build.id.stringId] = finishTime
    }
    messagesField.text += "\n> Remote run check finished"

    state.copy(lastCheckTime = currentTime, recordedBuilds = newRecordedBuilds).write(stateFile)
}

private fun checkCurrentBranch(build: Build) {
    if(build.state != BuildState.RUNNING && build.status == BuildStatus.FAILURE || build.status == BuildStatus.ERROR) {
        success = false
    }
}

