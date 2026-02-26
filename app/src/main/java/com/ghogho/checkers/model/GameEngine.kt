package com.ghogho.checkers.model

class GameEngine {

    fun getValidMoves(board: BoardState, player: PlayerColor, forceJumps: Boolean = true): List<Move> {
        val allMoves = mutableListOf<Move>()
        val allJumps = mutableListOf<Move>()

        for (row in 0 until BoardState.SIZE) {
            for (col in 0 until BoardState.SIZE) {
                val square = board.getSquare(row, col)
                if (square?.piece?.color == player) {
                    val moves = getMovesForPiece(board, square)
                    for (move in moves) {
                        if (move.captured != null) {
                            allJumps.add(move)
                        } else {
                            allMoves.add(move)
                        }
                    }
                }
            }
        }

        if (forceJumps && allJumps.isNotEmpty()) {
            return allJumps
        }
        return allJumps + allMoves
    }

    private fun getMovesForPiece(board: BoardState, square: Square): List<Move> {
        val moves = mutableListOf<Move>()
        val piece = square.piece ?: return moves

        val directions = mutableListOf<Pair<Int, Int>>()
        
        if (piece.isKing || piece.color == PlayerColor.BLACK) {
            directions.add(Pair(1, -1)) // Down Left
            directions.add(Pair(1, 1))  // Down Right
        }
        
        if (piece.isKing || piece.color == PlayerColor.RED) {
            directions.add(Pair(-1, -1)) // Up Left
            directions.add(Pair(-1, 1))  // Up Right
        }

        for ((r, c) in directions) {
            val newRow = square.row + r
            val newCol = square.col + c

            val targetSquare = board.getSquare(newRow, newCol)
            if (targetSquare != null) {
                if (targetSquare.piece == null) {
                    // Regular move
                    moves.add(Move(square, targetSquare))
                } else if (targetSquare.piece.color != piece.color) {
                    // Potential jump
                    val jumpRow = newRow + r
                    val jumpCol = newCol + c
                    val landingSquare = board.getSquare(jumpRow, jumpCol)
                    
                    if (landingSquare != null && landingSquare.piece == null) {
                        moves.add(Move(square, landingSquare, targetSquare))
                    }
                }
            }
        }

        return moves
    }

    fun hasValidMoves(board: BoardState, player: PlayerColor, forceJumps: Boolean = true): Boolean {
        return getValidMoves(board, player, forceJumps).isNotEmpty()
    }
    
    // Checks if the move results in more jumps (multi-jump scenario)
    fun getMultiJumps(board: BoardState, landingSquare: Square): List<Move> {
        val moves = getMovesForPiece(board, landingSquare)
        return moves.filter { it.captured != null }
    }
}
