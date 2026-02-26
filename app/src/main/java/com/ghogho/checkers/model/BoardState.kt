package com.ghogho.checkers.model

enum class PlayerColor {
    RED, BLACK
}

data class Piece(
    val color: PlayerColor,
    val isKing: Boolean = false
)

data class Square(
    val row: Int,
    val col: Int,
    val piece: Piece? = null
)

data class Move(
    val from: Square,
    val to: Square,
    val captured: Square? = null
)

class BoardState(val squares: Array<Array<Square>>) {

    companion object {
        const val SIZE = 8

        fun createInitialBoard(): BoardState {
            val squares = Array(SIZE) { row ->
                Array(SIZE) { col ->
                    val isBlackSquare = (row + col) % 2 != 0
                    val piece = when {
                        !isBlackSquare -> null
                        row < 3 -> Piece(PlayerColor.BLACK)
                        row > 4 -> Piece(PlayerColor.RED)
                        else -> null
                    }
                    Square(row, col, piece)
                }
            }
            return BoardState(squares)
        }
    }

    fun getSquare(row: Int, col: Int): Square? {
        if (row !in 0 until SIZE || col !in 0 until SIZE) return null
        return squares[row][col]
    }

    fun copy(): BoardState {
        val newSquares = Array(SIZE) { row ->
            Array(SIZE) { col ->
                squares[row][col].copy()
            }
        }
        return BoardState(newSquares)
    }

    fun applyMove(move: Move): BoardState {
        val newBoard = copy()
        val movingPiece = newBoard.squares[move.from.row][move.from.col].piece

        // Remove from old location
        newBoard.squares[move.from.row][move.from.col] = newBoard.squares[move.from.row][move.from.col].copy(piece = null)

        // Calculate if it becomes a king
        var isKing = movingPiece?.isKing ?: false
        if (!isKing && movingPiece != null) {
            if (movingPiece.color == PlayerColor.RED && move.to.row == 0) {
                isKing = true
            } else if (movingPiece.color == PlayerColor.BLACK && move.to.row == SIZE - 1) {
                isKing = true
            }
        }

        // Place at new location
        newBoard.squares[move.to.row][move.to.col] = newBoard.squares[move.to.row][move.to.col].copy(piece = movingPiece?.copy(isKing = isKing))

        // Remove captured piece if any
        if (move.captured != null) {
            newBoard.squares[move.captured.row][move.captured.col] = newBoard.squares[move.captured.row][move.captured.col].copy(piece = null)
        }

        return newBoard
    }
}
