# Ghogho Checkers App - Product Requirements Document (PRD)

## 1. Product Overview
Ghogho Checkers is a local Android mobile game that allows users to play the classic game of Checkers. The app supports both local multiplayer (pass-and-play) and single-player modes against an AI opponent. The game is monetized via Google AdMob banner ads.

## 2. Core Gameplay & Rules

### 2.1 Board and Pieces
*   **Board:** Standard 8x8 grid consisting of alternating light and dark squares.
*   **Pieces:** Each player starts with 12 pieces. 
    *   **Colors:** Player 1 is Black. Player 2 (or CPU) is Red.
    *   **Starting Position:** Pieces are placed on the dark squares of the three rows closest to each player.
*   **Turn Order:** Black always moves first.

### 2.2 Movement and Capturing
*   **Standard Moves:** Regular pieces (Pawns) can only move diagonally forward to an adjacent empty dark square.
*   **Capturing (Jumping):** 
    *   A player can capture an opponent's piece by jumping over it diagonally to an empty square immediately beyond it.
    *   Captured pieces are removed from the board.
*   **Multi-Jumps:** 
    *   If a jump lands a piece in a position where another jump is possible, the player MUST continue jumping during the same turn.
    *   The UI indicates when a player is in the middle of a multi-jump sequence via a "Must continue jumping!" warning.

### 2.3 King Promotion
*   When a regular piece reaches the furthest row on the opposite side of the board, it is promoted to a **King**.
*   **King Movement:** Kings can move and jump diagonally both forward AND backward.
*   **Turn Ending:** If a piece is promoted to a King as the result of a jump, its turn ends immediately. It cannot continue multi-jumping as a King on the same turn it was crowned.

### 2.4 Rule Variations
*   **Forced Jumps Toggle:** 
    *   The game provides an option to toggle "Forced Jumps" on or off.
    *   **When ON (Default):** If any jump is available on the board, the player is forced to take a jump (though they can choose which one if multiple distinct pieces can jump). Regular non-jumping moves are disabled until the jump is taken.
    *   **When OFF:** Players can choose to make a regular move even if a capture is available.

### 2.5 Win/Loss Conditions
*   A player wins when the opponent has no valid moves remaining (either all their pieces are captured, or all remaining pieces are blocked).

## 3. Game Modes

### 3.1 Player vs. Player (PvP)
*   Local pass-and-play on a single device.
*   Players alternate turns manually.

### 3.2 Player vs. CPU (PvE)
*   The human player controls Black (moves first). The CPU controls Red.
*   **AI Engine:** The CPU opponents use a Minimax algorithm with Alpha-Beta pruning to calculate the best move.
*   **Difficulty Levels:**
    *   **Easy:** Minimax search depth of 2. Focuses on immediate gains.
    *   **Medium (Default):** Minimax search depth of 4. Balances speed with tactical foresight.
    *   **Hard:** Minimax search depth of 6. Provides a challenging, highly strategic opponent.
*   The AI will automatically execute forced multi-jumps if they are available.
*   There is an artificial delay (~500ms) added to the CPU's turn to simulate "thinking" and provide a better UX.

## 4. User Interface (UI) Components
Built using Jetpack Compose, the UI consists of a single primary game screen organized vertically:

1.  **Top Status Bar:**
    *   Displays current turn ("Black's Turn" or "Red's Turn").
    *   Displays "Game Over" and the winner ("Black Wins!" or "Red Wins!") when the game concludes.
    *   Displays a red warning text "Must continue jumping!" if the player is mid-way through a multi-jump.
2.  **The Game Board:**
    *   Visual representation of the 8x8 grid.
    *   **Selected Piece:** Highlighted with a distinct background color.
    *   **Valid Moves:** Indicated by small green dot overlays on the destination squares for the currently selected piece.
3.  **Controls & Actions:**
    *   **Restart Game Button:** Resets the board and all game state instantly.
    *   **VS CPU Button:** Toggles the CPU mode ON/OFF.
4.  **Settings Row:**
    *   **Forced Jumps Switch:** A toggle to easily enable/disable forced captures.
    *   **Difficulty Cycler Button:** (Only visible in VS CPU mode) A button that cycles through EASY -> MEDIUM -> HARD difficulty settings.
5.  **Monetization (Ad Banner):**
    *   A Google AdMob banner is anchored to the absolute bottom of the screen.

## 5. Technical Stack & Architecture
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose
*   **Architecture Pattern:** MVVM (Model-View-ViewModel). The `CheckersViewModel` holds the `GameState` as a `StateFlow` and handles all game logic orchestration.
*   **Minimum SDK:** 29 (Android 10)
*   **Target SDK:** 36
*   **Monetization SDK:** Google Play Services Ads (`play-services-ads:23.0.0`)
