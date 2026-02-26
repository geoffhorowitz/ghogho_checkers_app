package com.ghogho.checkers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghogho.checkers.ai.MinimaxAI
import com.ghogho.checkers.model.BoardState
import com.ghogho.checkers.model.GameEngine
import com.ghogho.checkers.model.Move
import com.ghogho.checkers.model.PlayerColor
import com.ghogho.checkers.model.Square
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
enum class Difficulty {
    EASY, MEDIUM, HARD
}

data class GameSettings(
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val forceJumps: Boolean = true
)

data class GameState(
    val board: BoardState = BoardState.createInitialBoard(),
    val currentPlayer: PlayerColor = PlayerColor.BLACK, // Black starts
    val selectedSquare: Square? = null,
    val validMovesForSelected: List<Move> = emptyList(),
    val multiJumpSquare: Square? = null, // If a player is in the middle of a multi-jump
    val winner: PlayerColor? = null,
    val isGameOver: Boolean = false,
    val vsCpu: Boolean = false, // Set this later when we add AI mode
    val aiIsThinking: Boolean = false,
    val settings: GameSettings = GameSettings()
)

class CheckersViewModel : ViewModel() {

    private val engine = GameEngine()
    private val ai = MinimaxAI()
    
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        checkWinCondition()
    }

    fun toggleCpuMode() {
        val state = _uiState.value
        if (state.board.squares.flatten().all { it.piece == null } == false) {
             // Restart game to properly apply CPU mode from the start
             _uiState.update { GameState(vsCpu = !state.vsCpu, settings = state.settings) }
        } else {
             _uiState.update { it.copy(vsCpu = !it.vsCpu) }
        }
    }
    
    fun setDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(settings = it.settings.copy(difficulty = difficulty)) }
    }
    
    fun toggleForceJumps() {
        _uiState.update { it.copy(settings = it.settings.copy(forceJumps = !it.settings.forceJumps)) }
    }

    fun onSquareTapped(row: Int, col: Int) {
        val state = _uiState.value
        if (state.isGameOver || state.aiIsThinking) return
        if (state.vsCpu && state.currentPlayer == PlayerColor.RED) return // CPU is Red

        val tappedSquare = state.board.getSquare(row, col) ?: return

        if (state.multiJumpSquare != null && tappedSquare != state.multiJumpSquare) {
            return
        }

        if (state.selectedSquare == tappedSquare) {
            if (state.multiJumpSquare == null) {
                _uiState.update { it.copy(selectedSquare = null, validMovesForSelected = emptyList()) }
            }
            return
        }

        val move = state.validMovesForSelected.find { it.to.row == row && it.to.col == col }
        if (move != null) {
            executeMove(move)
            return
        }

        if (tappedSquare.piece != null && tappedSquare.piece.color == state.currentPlayer) {
            val allValidMoves = engine.getValidMoves(state.board, state.currentPlayer, state.settings.forceJumps)
            val movesForThisPiece = allValidMoves.filter { it.from.row == tappedSquare.row && it.from.col == tappedSquare.col }

            if (movesForThisPiece.isNotEmpty()) {
                _uiState.update { 
                    it.copy(
                        selectedSquare = tappedSquare,
                        validMovesForSelected = movesForThisPiece
                    ) 
                }
            } else {
                 _uiState.update { 
                    it.copy(
                        selectedSquare = null,
                        validMovesForSelected = emptyList()
                    ) 
                }
            }
        }
    }

    private fun executeMove(move: Move) {
        val state = _uiState.value
        var newBoard = state.board.applyMove(move)
        
        if (move.captured != null) {
            val landedSquare = newBoard.getSquare(move.to.row, move.to.col)
            val wasPawn = state.selectedSquare?.piece?.isKing == false
            val isNowKing = landedSquare?.piece?.isKing == true
            
            if (landedSquare != null && !(wasPawn && isNowKing)) {
                 val furtherJumps = engine.getMultiJumps(newBoard, landedSquare)
                 if (furtherJumps.isNotEmpty()) {
                     _uiState.update {
                         it.copy(
                             board = newBoard,
                             selectedSquare = landedSquare,
                             validMovesForSelected = furtherJumps,
                             multiJumpSquare = landedSquare
                         )
                     }
                     // If it's the CPU's turn, it needs to continue jumping automatically
                     if (_uiState.value.vsCpu && _uiState.value.currentPlayer == PlayerColor.RED) {
                         executeAiTurn()
                     }
                     return
                 }
            }
        }

        val nextPlayer = if (state.currentPlayer == PlayerColor.BLACK) PlayerColor.RED else PlayerColor.BLACK
        
        _uiState.update {
            it.copy(
                board = newBoard,
                currentPlayer = nextPlayer,
                selectedSquare = null,
                validMovesForSelected = emptyList(),
                multiJumpSquare = null
            )
        }
        
        checkWinCondition()

        // Trigger AI Turn if applicable
        if (_uiState.value.vsCpu && _uiState.value.currentPlayer == PlayerColor.RED && !_uiState.value.isGameOver) {
            executeAiTurn()
        }
    }

    private fun executeAiTurn() {
        _uiState.update { it.copy(aiIsThinking = true) }
        viewModelScope.launch {
            delay(500) // Small delay to feel human
            val state = _uiState.value
            
            // Calculate best move on background thread
            val bestMove = withContext(Dispatchers.Default) {
                if (state.multiJumpSquare != null) {
                    // CPU is in a multi-jump, just take the first valid jump for now
                    engine.getMultiJumps(state.board, state.multiJumpSquare).first()
                } else {
                    val depth = when(state.settings.difficulty) {
                        Difficulty.EASY -> 2
                        Difficulty.MEDIUM -> 4
                        Difficulty.HARD -> 6
                    }
                    ai.getBestMove(state.board, PlayerColor.RED, depth = depth, forceJumps = state.settings.forceJumps)
                }
            }
            
            _uiState.update { it.copy(aiIsThinking = false) }
            
            if (bestMove != null) {
                // We need to set up the selected state correctly to simulate UI selection
                _uiState.update {
                     it.copy(selectedSquare = bestMove.from)
                }
                delay(200) // Small animation delay representing the selection
                executeMove(bestMove)
            }
        }
    }

    private fun checkWinCondition() {
        val state = _uiState.value
        val hasMoves = engine.hasValidMoves(state.board, state.currentPlayer, state.settings.forceJumps)
        
        if (!hasMoves) {
            val winner = if (state.currentPlayer == PlayerColor.BLACK) PlayerColor.RED else PlayerColor.BLACK
            _uiState.update {
                it.copy(isGameOver = true, winner = winner)
            }
        }
    }

    fun restartGame() {
        _uiState.update {
            GameState(
                board = BoardState.createInitialBoard(),
                currentPlayer = PlayerColor.BLACK,
                selectedSquare = null,
                validMovesForSelected = emptyList(),
                multiJumpSquare = null,
                winner = null,
                isGameOver = false,
                vsCpu = it.vsCpu,
                settings = it.settings
            )
        }
    }
}
