package furhatos.app.sympathetic_actions

import furhatos.app.sympathetic_actions.flow.*
import furhatos.skills.Skill
import furhatos.flow.kotlin.*

class Sympathetic_ActionsSkill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
