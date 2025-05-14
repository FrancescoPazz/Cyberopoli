package com.unibo.cyberopoli.data.models.game.questions

import com.unibo.cyberopoli.data.models.game.GameDialogData

val ChanceQuestions = listOf(
    GameDialogData.ChanceQuestion(
        title = "Password sicura",
        prompt = "Quale tra queste è la migliore pratica per creare una password sicura?",
        options = listOf(
            "Usare parole comuni separate da trattini",
            "Usare una lunga stringa casuale con lettere, numeri e simboli",
            "Utilizzare sempre la stessa password per ogni account",
            "Aggiornare la password ogni 2 anni"
        ),
        correctIndex = 1,
        points = 1
    ),
    GameDialogData.ChanceQuestion(
        title = "Phishing: cos’è?",
        prompt = "Qual è la definizione corretta di phishing?",
        options = listOf(
            "Invio di email fraudolente per ottenere dati sensibili",
            "Attacco fisico a un server aziendale",
            "Utilizzo di ransomware per criptare file",
            "Intercettazione di comunicazioni via satellite"
        ),
        correctIndex = 0,
        points = 2
    ),
    GameDialogData.ChanceQuestion(
        title = "Brute Force",
        prompt = "In un attacco brute-force, cosa fa l’aggressore?",
        options = listOf(
            "Intercetta le email",
            "Prova sistematicamente tutte le combinazioni di password",
            "Installa un keylogger hardware",
            "Utilizza un virus per criptare i file"
        ),
        correctIndex = 1,
        points = 3
    ),
    GameDialogData.ChanceQuestion(
        title = "Wi-Fi pubblico",
        prompt = "Qual è il rischio principale quando ci si connette a una rete Wi-Fi pubblica non protetta?",
        options = listOf(
            "Consumo eccessivo di batteria",
            "Intercettazione del traffico da parte di malintenzionati",
            "Velocità di connessione troppo alta",
            "Aggiornamenti automatici forzati"
        ),
        correctIndex = 1,
        points = 4
    ),
    GameDialogData.ChanceQuestion(
        title = "Autenticazione a due fattori",
        prompt = "Quale vantaggio principale offre l’autenticazione a due fattori (2FA)?",
        options = listOf(
            "Accesso più rapido ai servizi",
            "Maggiore sicurezza richiedendo due credenziali diverse",
            "Riduce lo spazio occupato dalle password",
            "Aumenta la velocità di connessione"
        ),
        correctIndex = 1,
        points = 5
    ),
    GameDialogData.ChanceQuestion(
        title = "Backup dei dati",
        prompt = "Qual è il metodo più affidabile per proteggere i dati da perdita accidentale?",
        options = listOf(
            "Salvare tutto sul desktop",
            "Eseguire backup regolari su un dispositivo esterno e in cloud",
            "Lasciare i dati sul dispositivo originale",
            "Usare solo USB pubbliche"
        ),
        correctIndex = 1,
        points = 6
    ),
    GameDialogData.ChanceQuestion(
        title = "Social engineering",
        prompt = "Qual è la tecnica principale utilizzata nel social engineering?",
        options = listOf(
            "Sfruttare vulnerabilità di rete",
            "Manipolare psicologicamente la vittima",
            "Criptare i dati con ransomware",
            "Intercettare pacchetti di rete"
        ),
        correctIndex = 1,
        points = 2
    ),
    GameDialogData.ChanceQuestion(
        title = "Patch di sicurezza",
        prompt = "Perché è importante installare tempestivamente le patch di sicurezza?",
        options = listOf(
            "Per migliorare le prestazioni del sistema",
            "Per correggere vulnerabilità note prima che diventino exploit",
            "Per aumentare lo spazio libero su disco",
            "Per cambiare l'interfaccia grafica"
        ),
        correctIndex = 1,
        points = 3
    ),
    GameDialogData.ChanceQuestion(
        title = "Attacco DDoS",
        prompt = "Cosa caratterizza un attacco di tipo DDoS?",
        options = listOf(
            "Compromissione di un singolo host",
            "Diffusione di spyware tramite email",
            "Saturazione di banda tramite numerose richieste",
            "Criptazione dei file dell'utente"
        ),
        correctIndex = 2,
        points = 4
    ),
    GameDialogData.ChanceQuestion(
        title = "VPN e privacy",
        prompt = "Una VPN (Virtual Private Network) serve principalmente a:",
        options = listOf(
            "Velocizzare la connessione internet",
            "Creare un tunnel cifrato per proteggere il traffico",
            "Bloccare automaticamente ogni pubblicità",
            "Aumentare la memoria RAM del dispositivo"
        ),
        correctIndex = 1,
        points = 5
    )
)
