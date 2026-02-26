package com.ghogho.checkers.ai

import com.ghogho.checkers.model.BoardState
import com.ghogho.checkers.model.GameEngine
import com.ghogho.checkers.model.Move
import com.ghogho.checkers.model.PlayerColor
import kotlin.math.max
import kotlin.math.min

class MinimaxAI {

    private val engine = GameEngine()

    // Returns the best move for the given color and the score
    fun getBestMove(board: BoardState, player: PlayerColor, depth: Int, forceJumps: Boolean = true): Move? {
        val validMoves = engine.getValidMoves(board, player, forceJumps)
        if (validMoves.isEmpty()) return null

        var bestMove: Move = validMoves[0]
        var maxEval = Int.MIN_VALUE

        for (move in validMoves) {
            val childBoard = applyMoveFully(board, move, player)
            val eval = minimax(childBoard, depth - 1, Int.MIN_VALUE, Int.MAX_VALUE, false, getOpponent(player), forceJumps)
            if (eval > maxEval) {
                maxEval = eval
                bestMove = move
            }
        }

        return bestMove
    }

    private fun minimax(
        board: BoardState,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMaximizingPlayer: Boolean,
        currentPlayerColor: PlayerColor,
        forceJumps: Boolean
    ): Int {
        var a = alpha
        var b = beta

        if (depth == 0 || !engine.hasValidMoves(board, currentPlayerColor, forceJumps)) {
            // Need to evaluate from the perspective of the player who called `getBestMove` originally.
            // But we know maximizing player is the AI, so we just want staticEval to be positive if AI is winning.
            val aiColor = if (isMaximizingPlayer) currentPlayerColor else getOpponent(currentPlayerColor)
            return evaluateBoard(board, aiColor)
        }

        val validMoves = engine.getValidMoves(board, currentPlayerColor, forceJumps)

        if (isMaximizingPlayer) {
            var maxEval = Int.MIN_VALUE
            for (move in validMoves) {
                val childBoard = applyMoveFully(board, move, currentPlayerColor)
                val eval = minimax(childBoard, depth - 1, a, b, false, getOpponent(currentPlayerColor), forceJumps)
                maxEval = max(maxEval, eval)
                a = max(a, eval)
                if (b <= a) break // Alpha-Beta Pruning
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (move in validMoves) {
                val childBoard = applyMoveFully(board, move, currentPlayerColor)
                val eval = minimax(childBoard, depth - 1, a, b, true, getOpponent(currentPlayerColor), forceJumps)
                minEval = min(minEval, eval)
                b = min(b, eval)
                if (b <= a) break // Alpha-Beta Pruning
            }
            return minEval
        }
    }

    // Applies a move and any resulting forced multi-jumps to return the final state of the turn.
    private fun applyMoveFully(initialBoard: BoardState, initialMove: Move, player: PlayerColor): BoardState {
        var currentBoard = initialBoard.applyMove(initialMove)
        if (initialMove.captured == null) return currentBoard // No captures, turn ends immediately

        var landingSquare = currentBoard.getSquare(initialMove.to.row, initialMove.to.col)!!
        
        // Multi-jump loop
        var jumps = engine.getMultiJumps(currentBoard, landingSquare)
        while (jumps.isNotEmpty()) {
            val jump = jumps[0] // Just take the first jump for the AI during simulation
            currentBoard = currentBoard.applyMove(jump)
            landingSquare = currentBoard.getSquare(jump.to.row, jump.to.col)!!
            if (landingSquare.piece?.isKing == true && initialBoard.getSquare(initialMove.from.row, initialMove.from.col)?.piece?.isKing == false) {
                 break // Crowned king, turn ends
            }
            jumps = engine.getMultiJumps(currentBoard, landingSquare)
        }

        return currentBoard
    }

    private fun evaluateBoard(board: BoardState, player: PlayerColor): Int {
        var score = 0
        for (row in 0 until BoardState.SIZE) {
            for (col in 0 until BoardState.SIZE) {
                val piece = board.getSquare(row, col)?.piece
                if (piece != null) {
                    val pieceValue = if (piece.isKing) 10 else 3
                    if (piece.color == player) {
                        score += pieceValue
                    } else {
                        score -= pieceValue
                    }
                }
            }
        }
        return score
    }

    private fun getOpponent(player: PlayerColor): PlayerColor {
        return if (player == PlayerColor.BLACK) PlayerColor.RED else PlayerColor.BLACK
    }
}
