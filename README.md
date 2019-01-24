# Furhat - Agent-Human Ultimatum Game
*Author: [Timotheus Kampik](https://github.com/TimKam/)*

In this repository, we provide a [Furhat](https://www.furhatrobotics.com/) program--a so-called skill--that allows users to play a variant of the [ultimatum game](https://en.wikipedia.org/wiki/Ultimatum_game) with a *Furhat* robot.

We adjusted the original ultimatum game as follows:

* It is played between a human and an autonomous agent (Furhat). Depending on the configuration, the *agent* might act rationally from the perspective of classical economic theory, or might be more generous ("sympathetic") to the human.

* The players play a set of six games and take turns. First, the *agent* is the *proposer* and the human the *responder*. After each game, the players switch roles.


## Configuration
At the beginning of each game session, the Furhat skill will ask for the game mode to be set.
The following modes are available:

* ``"rational"`` (code word: ``apple``): The agent acts purely rationally, assuming the human player does the same. I.e., the agent always offers a split of (``agent: 99, human: 1``) and accepts any offer that provides it with any money (``agent: > 0, human: < 100``).

* ``"sympathetic"`` (code word: ``banana``): The agent player does not act purely rationally, but makes some concessions to the user: i.e., when splitting the money, the agent makes an initial concession to the human player, "hoping for good will", and in later rounds adjust its behavior to the human player's behavior in the previous round.

* ``"explainable"`` (code word: ``lemon``): The agent player acts *sympathetically* and in addition explains its sympathetic behavior to the user.


## Running the Furhat Skill
Follow the instructions in the Furhat documentation to run the skill.

Notes:

* You need to import the ``furhat-sympathetic-actions`` folder you find in the root directory of this repository.

* We use the [Gen2 Legacy](https://docs.furhat.io/legacy/) version of the Furhat SDK to be compatible with the 1st generation Furhat hardware.


## Interaction
When the skill is running and a user enters the robot's attention zone, Furhat will provide an explanation on how to interact with it.
You find detailed instructions on how to interact with Furhat (used for a preliminary HCI study) in the [the instructions file](./Instructions.md).


## License
This work is licensed under the BSD 2-Clause License, see [the license file](./LICENSE).


## Acknowledgements
This work was partially supported by the Wallenberg AI, Autonomous Systems and Software Program (WASP) funded by the Knut and Alice Wallenberg Foundation.
