package com.unibo.cyberopoli.util

import com.unibo.cyberopoli.data.models.game.QuestionPayload

val FallbackQuestions = listOf(
    QuestionPayload(
        title = "Phishing: cos’è?",
        prompt = "Qual è la definizione corretta di phishing?",
        options = listOf(
            "Invio di email fraudolente per ottenere dati sensibili",
            "Attacco fisico a un server aziendale",
            "Utilizzo di ransomware per criptare file",
            "Intercettazione di comunicazioni via satellite"
        ),
        correctIndex = 0,
        eventType = "CHANCE"
    ),
    QuestionPayload(
        title = "Password sicura",
        prompt = "Quale tra queste è la migliore pratica per creare una password sicura?",
        options = listOf(
            "Usare parole comuni separate da trattini",
            "Usare una lunga stringa casuale con lettere, numeri e simboli",
            "Utilizzare sempre la stessa password per ogni account",
            "Aggiornare la password ogni 2 anni"
        ),
        correctIndex = 1,
        eventType = "CHANCE"
    ),
    QuestionPayload(
        title = "Backup dei dati",
        prompt = "Qual è il metodo più affidabile per proteggere i dati da perdita accidentale?",
        options = listOf(
            "Salvare tutto sul desktop",
            "Eseguire backup regolari su un dispositivo esterno e in cloud",
            "Lasciare i dati sul dispositivo originale",
            "Usare solo USB pubbliche"
        ),
        correctIndex = 1,
        eventType = "CHANCE"
    ),
    QuestionPayload(
        title = "Wi-Fi pubblico",
        prompt = "Qual è il rischio principale quando ci si connette a una rete Wi-Fi pubblica non protetta?",
        options = listOf(
            "Consumo eccessivo di batteria",
            "Intercettazione del traffico da parte di malintenzionati",
            "Velocità di connessione troppo alta",
            "Aggiornamenti automatici forzati"
        ),
        correctIndex = 1,
        eventType = "CHANCE"
    ),
    QuestionPayload(
        title = "Autenticazione a due fattori",
        prompt = "Quale vantaggio principale offre l’autenticazione a due fattori (2FA)?",
        options = listOf(
            "Accesso più rapido ai servizi",
            "Maggiore sicurezza richiedendo due credenziali diverse",
            "Riduce lo spazio occupato dalle password",
            "Elimina la necessità di password"
        ),
        correctIndex = 1,
        eventType = "CHANCE"
    ),

    QuestionPayload(
        title = "Man in the Middle",
        prompt = "Che cosa descrive un attacco “Man in the Middle”?",
        options = listOf(
            "Un attaccante che prende il controllo fisico del dispositivo",
            "Un attaccante che si inserisce nella comunicazione tra due parti",
            "Un attacco mirato a rubare identità sui social",
            "Un attacco che sfrutta vulnerabilità hardware"
        ),
        correctIndex = 1,
        eventType = "HACKER"
    ),
    QuestionPayload(
        title = "SQL Injection",
        prompt = "In un contesto web, cos’è una SQL Injection?",
        options = listOf(
            "Un attacco DDoS contro il database",
            "L’inserimento di codice malevolo in una query SQL",
            "La crittografia dei dati di un database",
            "Un meccanismo di backup automatico"
        ),
        correctIndex = 1,
        eventType = "HACKER"
    ),
    QuestionPayload(
        title = "Cross-Site Scripting",
        prompt = "Cosa si intende per attacco XSS (Cross-Site Scripting)?",
        options = listOf(
            "Esecuzione di script malevoli nel browser dell’utente",
            "Scansione di rete per porte aperte",
            "Monitoraggio dei pacchetti di rete",
            "Cifratura dei dati di un sito web"
        ),
        correctIndex = 0,
        eventType = "HACKER"
    ),
    QuestionPayload(
        title = "Brute Force",
        prompt = "In un attacco brute-force, cosa fa l’aggressore?",
        options = listOf(
            "Intercetta le email",
            "Prova sistematicamente tutte le combinazioni di password",
            "Installa un keylogger hardware",
            "Utilizza un virus per criptare i file"
        ),
        correctIndex = 1,
        eventType = "HACKER"
    ),
    QuestionPayload(
        title = "Social Engineering",
        prompt = "Qual è l’obiettivo principale di un attacco di social engineering?",
        options = listOf(
            "Diffondere malware tramite email di massa",
            "Manipolare le persone per fargli rivelare informazioni riservate",
            "Bloccare un servizio con un DDoS",
            "Criptare i dati dell’utente"
        ),
        correctIndex = 1,
        eventType = "HACKER"
    )
)
