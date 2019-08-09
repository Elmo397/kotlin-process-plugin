package org.jetbrains.kotlin.process.bot.rr

//import org.jetbrains.kotlin.process.notifications.api.send
import com.intellij.ide.util.PropertiesComponent
import org.jetbrains.kotlin.process.util.stateful.Stateful
import org.jetbrains.kotlin.process.util.stateful.read
import org.jetbrains.kotlin.process.util.stateful.write
import org.jetbrains.teamcity.rest.BuildConfigurationId
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import java.time.Instant

data class State(val lastCheckTime: Long, val recordedBuilds: Map<String, Long>) : Stateful<State>

private val BRANCH_PATTERN = """rr/(?:gradle/)?([^/]*)/.*""".toRegex()

fun checkRemoteRun() {
    println("Remote run bot iteration started.")

    val currentTime = System.currentTimeMillis() / 1000
    val ageLimit = currentTime - 24 * 3600 * 1 // one day

    val state = Stateful.read<State>(stateFile) ?: State(0, emptyMap())

    val builds = TeamCityInstanceFactory.guestAuth("https://teamcity.jetbrains.com")
        .builds()
        .fromConfiguration(BuildConfigurationId("Kotlin_dev_AggregateBranch"))
        .withAllBranches()
        .includeFailed()
        .since(Instant.ofEpochSecond(ageLimit))
        .all()
        .filter {
            it.id.stringId !in state.recordedBuilds
        }
        .filter {
            (it.finishDateTime?.toEpochSecond() ?: 0) >= (state.lastCheckTime - 30 * 60)
        }.toList()

    val newRecordedBuilds = state.recordedBuilds.filterTo(HashMap()) { (_, v) -> v > ageLimit }

    for (build in builds) {
        println("Found completed build ${build.id.stringId} (branch ${build.branch.name})")

        val branchName = build.branch.name ?: continue
        val match = BRANCH_PATTERN.matchEntire(branchName) ?: continue
        val finishTime = build.finishDateTime?.toEpochSecond() ?: continue
        val branchPrefix = match.groupValues[1]

        println("Found branch prefix $branchPrefix")

        val user = PropertiesComponent.getInstance().getValue("devNick")

        if (user == null) {
            println("Cannot find user for branch prefix $branchPrefix")
            continue
        }

//        if (user.botConfiguration["rr.disable"] == "true") {
//            System.out.println("RemoteRun bot is disabled for user ${user.fullName}")
//            continue
//        }

        val message = """
            Build for branch $branchName just finished.
            Status: ${build.statusText}
            Link: ${build.getHomeUrl()}
        """.trimIndent()

//        val sendResponse = notifications.send(user.id, false, SERVICE_NAME, message).execute()
//        System.out.println("Sent a notification to user ${user.fullName}")

//        if (!sendResponse.isSuccessful) {
//            System.err.println("Cannot send a notification to ${user.fullName}")
//        }

        newRecordedBuilds[build.id.stringId] = finishTime
    }

    state.copy(lastCheckTime = currentTime, recordedBuilds = newRecordedBuilds).write(stateFile)
}