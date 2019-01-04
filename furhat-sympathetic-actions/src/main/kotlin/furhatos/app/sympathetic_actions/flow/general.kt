package furhatos.app.sympathetic_actions.flow

import furhatos.flow.kotlin.*
import furhatos.util.*

val Idle: State = state {

    init {
        furhat.setVoice(Language.ENGLISH_US, Gender.MALE)
        if (users.count > 0) {
            furhat.attend(users.random)
            goto(Start)
        }
    }

    onEntry {
        furhat.attendNobody()
    }

    onUserEnter {
        furhat.attend(it)
        goto(Start)
    }
}

val Interaction: State = state {

    onUserLeave(instant = true) {
        if (users.count > 0) {
            if (it == users.current) {
                furhat.attend(users.other)
                goto(Start)
            } else {
                furhat.glance(it)
            }
        } else {
            furhat.say("Bye!")
            goto(Idle)
        }
    }

    onUserEnter(instant = true) {
        furhat.glance(it)
    }

    onNoResponse {
        furhat.say("Sorry, I didn't hear you.")
        reentry()
    }

    onResponse { // Catches everything else
        furhat.say("Sorry, I didn't understand you.")
        reentry()
    }

}