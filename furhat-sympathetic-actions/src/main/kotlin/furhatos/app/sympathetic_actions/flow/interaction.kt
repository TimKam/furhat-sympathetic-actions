package furhatos.app.sympathetic_actions.flow

import furhatos.app.sympathetic_actions.nlu.*
import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.nlu.common.Number
import furhatos.app.sympathetic_actions.LOG
import furhatos.app.sympathetic_actions.flow.introduction as introduction

val tellIntro = true

var mode = "explainable" //"sympathetic", "explainable", "rational"

var roundNumber = 1
var furhatRole = "proposer"
var coinsEarnedHuman : MutableList<Int> = mutableListOf()
var coinsEarnedAgent : MutableList<Int> = mutableListOf()
var tempCoinsShared = 0
var sharingHistoryAgent : MutableList<Int> = mutableListOf()
var sharingHistoryHuman : MutableList<Int> = mutableListOf()


val Start : State = state(Interaction) {

    onEntry {
        furhat.ask("Which mode do you play today?")
    }

    onResponse<Mode2Intent>{
        mode = "sympathetic"
        furhat.say("Okay")
        goto(Intro)
    }

    onResponse<Mode3Intent>{
        mode = "explainable"
        furhat.say("Okay")
        goto(Intro)
    }

    onResponse<Mode1Intent>{
        mode = "rational"
        furhat.say("Okay")
        goto(Intro)
    }

}

val Intro : State = state {
    onEntry {
        if (tellIntro) furhat.say(introduction)
        furhat.gesture(Gestures.BigSmile)
        delay(1000)
        goto(PostIntro)
    }
}

val PostIntro : State = state {
    onEntry {
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
                /*println("sharing history agent:")
                println(sharingHistoryAgent)
                println("sharing history human:")
                println(sharingHistoryHuman)
                println("coins earned agent:")
                println(coinsEarnedAgent)
                println("coins earned human:")
                println(coinsEarnedAgent)*/
                val coinsSharedResult = determineCoinsShared(sharingHistoryAgent, sharingHistoryHuman, mode)
                tempCoinsShared = coinsSharedResult.first
                val explanation = coinsSharedResult.second
                sharingHistoryAgent.add(tempCoinsShared)
                // furhat.say(/*Round $roundNumber: */"It is my turn to take the role of the proposer. $explanation I give you ${pluralize(tempCoinsShared, "coin", "coins")} and keep the remaining coins. ")
                delay(200)
                furhat.ask("It is my turn to take the role of the proposer. $explanation I give you ${pluralize(tempCoinsShared, "coin", "coins")} and keep the remaining coins. As the responder, do you accept the offer?")
            }
            "responder" -> {
                delay(200)
                tempCoinsShared = furhat.askFor<Number>("It is your turn to take the role of the proposer. How many coins do you want to share?")!!.value
                sharingHistoryHuman.add(tempCoinsShared)
                if (determineAcceptance(tempCoinsShared, mode)) {
                    furhat.say("Great, I take them. Now, you receive ${pluralize(100 - tempCoinsShared, "coin", "coins")} and I receive ${pluralize(tempCoinsShared, "coin", "coins")}.")
                    coinsEarnedAgent.add(tempCoinsShared)
                    coinsEarnedHuman.add((100 - tempCoinsShared))
                } else {
                    coinsEarnedAgent.add(0)
                    coinsEarnedHuman.add(0)
                    furhat.say("Hm... I reject. Neither of us receives their share.")
                }
                furhat.gesture(Gestures.Thoughtful)
                delay(500)
                nextRound(furhat)
            }
        }
    }

    onResponse<Yes>{
        furhat.say("Great! Then you receive ${pluralize(tempCoinsShared, "coin", "coins")} and I receive ${pluralize(100 - tempCoinsShared, "coin", "coins")}. ")
        coinsEarnedAgent.add((100 - tempCoinsShared))
        coinsEarnedHuman.add(tempCoinsShared)
        furhat.gesture(Gestures.Thoughtful)
        nextRound(furhat)
    }

    onResponse<No>{
        coinsEarnedAgent.add(0)
        coinsEarnedHuman.add(0)
        furhat.say("Too bad! Then neither of us receives their share.")
        delay(200)
        nextRound(furhat)
    }

    onResponse<NextTurnIntent>{
        furhat.say("Great! Let's start round number ${roundNumber}.")
        delay(500)
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
        LOG.info("Sharing history human = $sharingHistoryHuman ; sharing history agent = $sharingHistoryAgent")
        LOG.info("Coins human: $coinsEarnedHuman = $coinsEarnedHumanSum; coins agent: $coinsEarnedAgent = $coinsEarnedAgentSum" )
        furhat.say("Great! The game is over. In total you earned ${pluralize(coinsEarnedHumanSum, "virtual coin", "virtual coins")}. I earned ${pluralize(coinsEarnedAgentSum, "virtual coin", "virtual coins")}. Thanks for playing!")
        furhat.runner.goto(Start)
    }
}

fun determineCoinsShared(historyAgent: List<Int>, historyHuman: List<Int>, mode: String): Pair<Int, String> {
    return when (mode) {
        "rational" -> {
            return Pair(1, "")
        }
        "sympathetic" -> {
            if(historyHuman.isEmpty()) {
                return Pair(10, "")
            } else {
                when (historyHuman.last()) {
                    0 -> {
                        return Pair(1, "")
                    }
                    in 1..99 -> {
                        /* if human rejected previous offer and human's last offer < previous offer:
                        * raise previous offer + 15
                        * otherwise: present same amount of coins as human's last offer
                       */
                        println(coinsEarnedHuman)
                        val previousRejected = coinsEarnedHuman.get(coinsEarnedHuman.size - 2) == 0 && coinsEarnedAgent.get(coinsEarnedAgent.size - 2) == 0
                        if(previousRejected && historyHuman.last() < historyAgent.last()) {
                            return Pair(historyAgent.last() + 15, "")
                        }
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
                        return Pair(1, "Although you did not share anything with me last time, I am nice and ")
                    }
                    in 1..99 -> {
                        /* if human rejected previous offer and human's last offer <= previous offer:
                        * raise previous offer + 15
                        * otherwise: present same amount of coins as human's last offer
                       */
                        val previousRejected = coinsEarnedHuman.get(coinsEarnedHuman.size - 2) == 0 && coinsEarnedAgent.get(coinsEarnedAgent.size - 2) == 0
                        if(previousRejected && historyHuman.last() < historyAgent.last()) {
                            return Pair(historyAgent.last() + 15, "Although you shared a lower amount of coins with me the previous time, I am nice and increase my offer to you; ")
                        }
                        return Pair(historyHuman.last(), "Because you shared the same amount of coins with me the previous time, I pay the favor back and")
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

fun pluralize(number: Int, baseTerm: String, pluralTerm: String): String {
    if (number == 1) {
        return "$number $baseTerm"
    }
    return "$number $pluralTerm"
}
