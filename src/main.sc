dependencies {
    implementation("com.justai.jaicf:jaicf-core:1.1.7")
}

import com.justai.jaicf.activator.catchall.CatchAllActivator
import com.justai.jaicf.api.BotApi
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.api.BotResponse
import com.justai.jaicf.api.hasAnnotations
import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.jaicp.logging.log

val secretNumber = generateSecretNumber()

fun mainBot(): BotApi = BotApi(
    model = Scenario {
        state("start") {
            action {
                reactions.say("Давай начнем игру в 'Быки и коровы'! Я задумал 4-значное число с неповторяющимися цифрами.")
                reactions.go("/play")
            }
        }

        state("play") {
            activators {
                regex("^[0-9]{4}$")
                catchall()
            }

            action {
                val guess = request.input.trim()
                if (guess == secretNumber) {
                    reactions.say("Поздравляю, ты угадал число $secretNumber! Игра окончена.")
                    reactions.go("/end")
                } else {
                    val result = evaluateGuess(guess)
                    reactions.say("Ты угадал $result")
                }
            }
        }

        state("end") {
            action {
                reactions.say("Спасибо за игру! Чтобы начать заново, скажи 'начать' или 'новая игра'.")
            }
        }
    }
)

fun generateSecretNumber(): String {
    val digits = mutableListOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    digits.shuffle()
    return digits.take(4).joinToString("")
}

fun evaluateGuess(guess: String): String {
    var bulls = 0
    var cows = 0
    for (i in 0 until 4) {
        if (guess[i] == secretNumber[i]) {
            bulls++
        } else if (secretNumber.contains(guess[i])) {
            cows++
        }
    }
    return if (bulls > 0 && cows > 0) {
        "$bulls быка(ов) и $cows корова(ы)"
    } else if (bulls > 0) {
        "$bulls быка(ов)"
    } else if (cows > 0) {
        "$cows корова(ы)"
    } else {
        "ничего"
    }
}
