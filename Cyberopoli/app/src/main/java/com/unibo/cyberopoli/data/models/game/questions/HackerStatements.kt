package com.unibo.cyberopoli.data.models.game.questions

import com.unibo.cyberopoli.data.models.game.GameDialogData

val HackerStatements = listOf(
    GameDialogData.HackerStatement(
        title = "Password debole",
        content = "Hanno scoperto la tua password '123456'. Perdi 4 punti di sicurezza.",
        points = 4
    ),
    GameDialogData.HackerStatement(
        title = "Wi-Fi pubblico non sicuro",
        content = "Un hacker ha iniettato malware mentre eri connesso ad una rete Wi-Fi pubblica senza VPN. Perdi 5 punti.",
        points = 5
    ),
    GameDialogData.HackerStatement(
        title = "Phishing riuscito",
        content = "Hai cliccato su un link sospetto in una email. I tuoi dati bancari sono stati rubati! Perdi 7 punti.",
        points = 7
    ),
    GameDialogData.HackerStatement(
        title = "Ransomware",
        content = "I tuoi file sono stati criptati da un ransomware. Paghi il riscatto di 5 punti per recuperarli.",
        points = 5
    ),
    GameDialogData.HackerStatement(
        title = "Social Engineering",
        content = "Hai risposto a una telefonata di un falso tecnico e hai condiviso le tue credenziali. Perdi 3 punti.",
        points = 3
    ),
    GameDialogData.HackerStatement(
        title = "Account compromesso",
        content = "Il tuo account social è stato hackerato e usato per truffe. Perdi 5 punti e il tuo profilo.",
        points = 5
    ),
    GameDialogData.HackerStatement(
        title = "Malware",
        content = "Un malware ha infettato il tuo computer. Perdi 6 punti e devi reinstallare il sistema.",
        points = 6
    ),
)
