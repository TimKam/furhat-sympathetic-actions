package furhatos.app.sympathetic_actions.flow

val introduction = """
    Hi there.
    Let's play a game.
    It's a two-player game and goes over several turns.

    Each turn goes like this:

    One player has 100 virtual coins at their disposal.
    Let's call this player the 'proposer'.
    They can split the coins in any way between themselves and the other player.
    They cannot split a single coin into more parts, though.

    The other player can either accept or reject the offer. We call this player the 'responder'.
    If the responder accepts the offer, both players will receive the coins as split by the proposer.
    If the responder rejects the offer, nobody will receive any coins.

    First, I will play the role of the 'proposer' and you will be the 'responder'.

    Then, we will take turns and change roles.

    In total, we will play 6 rounds: 3 times I make the offer, and 3 times you make the offer.

    Depending on the amount of coins you receive during the game, you will rewarded with more or less sweets for participating in the experiment.


    In front of you, you find the instructions in text form.
    Let's play!
""".trimIndent()