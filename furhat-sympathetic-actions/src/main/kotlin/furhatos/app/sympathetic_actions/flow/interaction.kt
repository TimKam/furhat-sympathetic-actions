package furhatos.app.sympathetic_actions.flow

import furhatos.app.sympathetic_actions.nlu.NextTurnIntent
import furhatos.app.sympathetic_actions.nlu.StartGameIntent
import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.nlu.common.Number
import furhatos.app.sympathetic_actions.flow.introduction as introduction

val mode = "explainable" //"sympathetic" // "explainable", "rational"

var roundNumber = 1
var furhatRole = "proposer"
var coinsEarnedHuman : MutableList<Int> = mutableListOf()
var coinsEarnedAgent : MutableList<Int> = mutableListOf()
var tempCoinsShared = 0
var sharingHistoryAgent : MutableList<Int> = mutableListOf()
var sharingHistoryHuman : MutableList<Int> = mutableListOf()


val Start : State = state(Interaction) {

    onEntry {
        furhat.say(introduction)
        furhat.ask("Say the word 'start' when you are ready to go.")
    }

    onResponse<StartGameIntent>{
        furhat.say("Great! Let's start.")
        goto(StartRound)
    }

}

val StartRound : State = state {
    onEntry {
        when (furhatRole) {
            "proposer" ->  {
                val coinsSharedResult = determineCoinsShared(sharingHistoryAgent, sharingHistoryHuman, mode)
                tempCoinsShared = coinsSharedResult.first
                val explanation = coinsSharedResult.second
                furhat.say(/*Round $roundNumber: */"It is my turn to take the role of the proposer. $explanation I give you $tempCoinsShared coins and keep the remaining ones. ")
                furhat.ask("As the responder, do you accept the offer?")
                sharingHistoryAgent.add(tempCoinsShared)
            }
            "responder" -> {
                tempCoinsShared = furhat.askFor<Number>("It is your turn to take the role of the proposer. How many coins do you want to share?")!!.value
                sharingHistoryHuman.add(tempCoinsShared)
                if (determineAcceptance(tempCoinsShared, mode)) {
                    furhat.say("Great, I take them. Now, you receive ${100 - tempCoinsShared} coins and I receive $tempCoinsShared coins.")
                    coinsEarnedAgent.add(tempCoinsShared)
                    coinsEarnedHuman.add((100 - tempCoinsShared))
                } else {
                    furhat.say("Hm... I reject. Neither of us receives their share.")
                    sharingHistoryAgent.add(0)
                }
                furhat.gesture(Gestures.Thoughtful)
                nextRound(furhat)
            }
        }
    }

    onResponse<Yes>{
        furhat.say("Great! Then you receive $tempCoinsShared coins and I receive ${100 - tempCoinsShared} coins. ")
        coinsEarnedAgent.add((100 - tempCoinsShared))
        coinsEarnedHuman.add(tempCoinsShared)
        furhat.gesture(Gestures.Thoughtful)
        nextRound(furhat)
    }

    onResponse<No>{
        furhat.say("Too bad! Then neither of us receives their share.")
        nextRound(furhat)
    }

    onResponse<NextTurnIntent>{
        furhat.say("Great! Let's start round number ${roundNumber}.")
        goto(TransitionState)
    }

}

val TransitionState = state {
    onEntry {
        goto(StartRound)
    }
}

fun nextRound(furhat: Furhat/*, runner: TriggerRunner<*>*/) {
    roundNumber++
    if (furhatRole == "responder") {
        furhatRole = "proposer"
    } else {
        furhatRole = "responder"
    }
    if(roundNumber <= 6) {
        furhat.ask("When you ready for the next turn, say 'next'!")
    } else {
        val coinsEarnedAgentSum = coinsEarnedAgent.toIntArray().sum()
        val coinsEarnedHumanSum = coinsEarnedHuman.toIntArray().sum()
        print("Coins human: $coinsEarnedHumanSum; coins agent: $coinsEarnedAgentSum" )
        furhat.say("Great! The game is over. In total you earned $coinsEarnedHumanSum virtual coins. I earned $coinsEarnedAgentSum virtual coins. Thanks for playing!")
        // runner.goto(Idle)
    }
}

fun determineCoinsShared(historyAgent: List<Int>, historyHuman: List<Int>, mode: String): Pair<Int, String> {
    return when (mode) {
        "rational" -> {
            return Pair(1, "")
        }
        "sympathetic", "explainable" -> {
            if(historyHuman.isEmpty()) {
                return Pair(10, "Because I am nice, ")
            } else {
                when (historyHuman.last()) {
                    0 -> {
                        return Pair(1, "")
                    }
                    in 1..99 -> {
                        return Pair(historyHuman.last(), "")
                    } else -> {
                        return Pair(1, "")
                    }
                }
            }
        }
        "explainable" -> {
            if(historyHuman.isEmpty()) {
                return Pair(10, "Because I am nice, ")
            } else {
                when (historyHuman.last()) {
                    0 -> {
                        return Pair(1, "Although you did not share anything with me last time, ")
                    }
                    in 1..99 -> {
                        return Pair(historyHuman.last(), "Because you have shared the same amount of coins with me the previous time, ")
                    } else -> {
                    return Pair(1, "")
                }
                }
            }
        }
        else -> {
            return Pair(1, "")
        }
    }
}

fun determineAcceptance(tempCoinsShared: Int, mode: String): Boolean {
    when (mode) {
        "rational" -> {
            if (tempCoinsShared >= 1) {
                return true
            }
        }
        "sympathetic", "explainable" -> {
            if (tempCoinsShared >= 1) {
                return true
            }
        }
    }
    return false
}
