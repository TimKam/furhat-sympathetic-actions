package furhatos.app.sympathetic_actions.nlu

import furhatos.nlu.*
import furhatos.util.Language

class StartGameIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Start")
    }
}

class NextTurnIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Next", "Next turn")
    }
}