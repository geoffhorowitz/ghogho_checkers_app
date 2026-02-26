package com.ghogho.checkers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ghogho.checkers.model.BoardState
import com.ghogho.checkers.model.Square
import com.ghogho.checkers.ui.theme.CustomDarkSquare
import com.ghogho.checkers.ui.theme.CustomLightSquare
import com.ghogho.checkers.ui.theme.HighlightSelectedPiece
import com.ghogho.checkers.ui.theme.HighlightValidMove
import com.ghogho.checkers.viewmodel.GameState

@Composable
fun CheckersBoard(
    gameState: GameState,
    onSquareTapped: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // The board should always be square
    Box(
        modifier = modifier
            .aspectRatio(1f) // 1:1 aspect ratio
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until BoardState.SIZE) {
                Row(modifier = Modifier.weight(1f)) {
                    for (col in 0 until BoardState.SIZE) {
                        val square = gameState.board.getSquare(row, col)
                        if (square != null) {
                            BoardSquare(
                                row = row,
                                col = col,
                                square = square,
                                isSelected = gameState.selectedSquare?.row == row && gameState.selectedSquare?.col == col,
                                isValidMove = gameState.validMovesForSelected.any { it.to.row == row && it.to.col == col },
                                onSquareTapped = onSquareTapped,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoardSquare(
    row: Int,
    col: Int,
    square: Square,
    isSelected: Boolean,
    isValidMove: Boolean,
    onSquareTapped: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBlackSquare = (row + col) % 2 != 0
    val backgroundColor = if (isBlackSquare) CustomDarkSquare else CustomLightSquare

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { onSquareTapped(row, col) }
    ) {
        // Draw the piece if it exists on this square
        if (square.piece != null) {
            CheckersPiece(
                piece = square.piece,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Draw highlights on top
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HighlightSelectedPiece)
            )
        }

        if (isValidMove) {
            // Little green dot in the center
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(HighlightValidMove)
                )
            }
        }
    }
}
