package com.unibo.cyberopoli.data.models.game.questions

import com.unibo.cyberopoli.data.models.game.GameDialogData

val HackerStatements = listOf(
    GameDialogData.HackerQuestion(
        title = "Password debole",
        content = "Hanno scoperto la tua password '123456'. Perdi 2 punti di sicurezza.",
        points = -2
    ),
    GameDialogData.HackerQuestion(
        title = "Wi-Fi pubblico non sicuro",
        content = "Un hacker ha iniettato malware mentre eri connesso ad una rete Wi-Fi pubblica senza VPN. Perdi 3 punti.",
        points = -3
    ),
    GameDialogData.HackerQuestion(
        title = "Phishing riuscito",
        content = "Hai cliccato su un link sospetto in una email. I tuoi dati bancari sono stati rubati! Perdi 4 punti.",
        points = -4
    ),
    GameDialogData.HackerQuestion(
        title = "Ransomware",
        content = "I tuoi file sono stati criptati da un ransomware. Paghi il riscatto di 2 punti per recuperarli.",
        points = -2
    ),
    GameDialogData.HackerQuestion(
        title = "Social Engineering",
        content = "Hai risposto a una telefonata di un falso tecnico e hai condiviso le tue credenziali. Perdi 3 punti.",
        points = -3
    ),
    GameDialogData.HackerQuestion(
        title = "Account compromesso",
        content = "Il tuo account social è stato hackerato e usato per truffe. Perdi 2 punti e il tuo profilo.",
        points = -2
    ),
    GameDialogData.HackerQuestion(
        title = "Malware",
        content = "Un malware ha infettato il tuo computer. Perdi 3 punti e devi reinstallare il sistema.",
        points = -3
    ),
)
