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

class Mode1Intent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Mode Apple")
    }
}

class Mode2Intent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Mode Banana")
    }
}

class Mode3Intent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Mode Lemon")
    }
}
