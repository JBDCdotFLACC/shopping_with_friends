# Shopping With Friends

A collaborative shopping list application that allows users to manage shopping lists and share them with friends.  
Users will be able to collaborate and edit a single list in real time, to make a fun and efficient shopping experience.

## Architecture

The project follows modern Android development practices and is built using **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** pattern.

### Tech Stack
- **UI:** Jetpack Compose for a fully declarative UI.
- **Dependency Injection:** Hilt (Dagger) for dependency management.
- **Asynchronous Programming:** Kotlin Coroutines and Flow for reactive data handling.
- **Local Database:** Room for offline data persistence.
- **Backend:** Firebase Authentication and Cloud Firestore for real-time data sync.
- **Background Work:** WorkManager for reliable data synchronization.
- **Testing:** MockK and Turbine for unit testing ViewModels and Repositories.

### Project Structure
- `auth/`: Logic related to user authentication and account management.
- `data/`: The data layer containing repositories, local (Room) and remote (Firestore) data sources.
- `features/`: Feature-based UI modules (Home, Edit List, Friend Search, etc.).
- `hilt/`: Dependency Injection modules.
- `ui/`: Common UI components and theming.

### Offline First & Sync
The app implements an offline-first strategy. All user actions are first persisted in the local Room database and then synchronized with Firebase in the background using `WorkManager`. This ensures the app remains functional even without a stable internet connection.

## Testing
The project includes a suite of unit tests for core logic:
- `ViewModel` tests using `StandardTestDispatcher` and `Turbine` to verify StateFlow emissions.
- `Repository` tests mocking Firestore and Room interactions.
- Mocking of static Android components where necessary to support local JVM testing.


