# Say Hi To P.E.D.R.O.! 👋

Incontra il tuo nuovo Personal Exercise Data Recording & Organizer companion! Con P.E.D.R.O., monitorare e ottimizzare le tue attività fisiche non è mai stato così semplice e divertente. Che tu stia camminando, guidando, o semplicemente prendendoti una pausa, P.E.D.R.O. è qui per registrare ogni momento, tenendo traccia dei tuoi progressi e fornendoti report personalizzati con grafici e statistiche dettagliate.

Grazie al riconoscimento automatico delle attività e alle notifiche proattive, non dovrai mai preoccuparti di perdere un passo. Vuoi vedere i tuoi progressi? Accedi alla visualizzazione calendario e scopri un mondo di informazioni pronte a motivarti e guidarti verso una vita più sana e attiva!

E non finisce qui: P.E.D.R.O. è anche progettato per condividere e confrontare le tue attività con amici o colleghi. Che aspetti? Scarica P.E.D.R.O. e scopri quanto può fare per la tua routine quotidiana. 💪📊

P.E.D.R.O. - Il tuo nuovo assistente per un'attività fisica consapevole e organizzata!

## Struttura progetto
Il progetto prevede l'utilizzo del pattern MVVM - Model View ViewModel:
- Model: contiene i dati e la logica di business dell'app
- View: UI dell'app, implementata attraverso Jetpack Compose
- ViewModel: ponte tra i primi due, ogni classe in questo package estende ViewModel, si occupa di
esporre i dati alla View e gestire le interazioni dell'utente modificando i dati in model di conseguenza

### Classi
- Repository: è la classe responsabile della gestione dei dati. Si occupa di recuperare e inviare dati da/verso varie fonti
(come un database locale o un'API di rete) e fornisce questi dati al ViewModel.
La logica del Repository è isolata dal ViewModel per mantenere il codice più pulito e modulare.

- Use Case: rappresenta un'operazione specifica dell'applicazione (ad esempio, "GetSongsUseCase" per recuperare le canzoni).
I Use Case sono utili per isolare la logica di business e rendere il codice del ViewModel più pulito,
in quanto si occupano di orchestrare i dati e le operazioni per rispondere alle richieste dell'utente.

### Flusso dei dati
- View (composable): La View cattura l'interazione dell'utente e chiama un metodo nel ViewModel.
- ViewModel: Il ViewModel riceve la richiesta e chiama il Use Case corrispondente.
- Use Case: Il Use Case interagisce con il Repository per ottenere i dati richiesti o per eseguire operazioni.
  Lo Use Case è responsabile di orchestrare le chiamate e applicare logiche di business specifiche. Ad esempio, potresti voler filtrare, ordinare o trasformare i dati prima di restituirli al ViewModel.
  Se dovessi implementare una logica di caching, di convalida dei dati o di trasformazione, potresti farlo nello Use Case, mantenendo il Repository focalizzato solo sul recupero dei dati.
- Repository: Il Repository gestisce la logica di accesso ai dati, interagendo con le fonti appropriate e restituisce il risultato al Use Case.
- ViewModel: Riceve il risultato dal Use Case e lo passa alla View, dove viene visualizzato tramite LiveData o State.

### Struttura cartelle
com.example.app
├── data
│   ├── model
│   │   └── Song.kt //Definisce la classe Song che rappresenta un brano musicale.
│   ├── repository
│   │   └── SongRepository.kt //Gestisce l'accesso ai dati (API, in tal caso chiamo il service in remote, database, etc.) e fornisce i Song al ViewModel o al Use Case.
│   └── remote
│   │   └── ApiService.kt //Simula un servizio API che recupera i dati. In un caso reale, qui chiameresti un’API REST.
│   └── local
│       └── SongDao.kt //database locale
├── domain
│   └── usecase
│       └── GetSongsUseCase.kt //Definisce un Use Case per recuperare la lista di canzoni dal Repository.
├── ui
│   ├── view
│   │   ├── MainScreen.kt //UI composable
│   │   └── SongListScreen.kt //UI Composable
│   └── viewmodel
│       └── SongViewModel.kt // Gestisce i dati per la UI e interagisce con il Use Case.
└── di
└── AppModule.kt

In questa struttura di progetto basata sul pattern MVVM, le cartelle principali che rappresentano View, Model, e ViewModel sono le seguenti:
- cartella ui -> view
- cartella data/model -> model
- cartella ui/viewmodel -> viewmodel

I componenti extra ma utili:
- Use Case (domain/usecase): Pur non facendo parte delle tre categorie MVVM tradizionali, i Use Case rappresentano le operazioni di business e fanno parte del dominio logico dell'applicazione.
- Repository (data/repository): Anche se non è direttamente parte di View, Model o ViewModel, il Repository funge da intermediario per l'accesso ai dati, recuperandoli da fonti locali o remote.
- Local (data/local): Si occupa dell'accesso al database locale tramite DAO. Questa è una parte del Model in quanto gestisce le operazioni dirette sui dati.
