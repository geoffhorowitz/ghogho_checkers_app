package com.ghogho.checkers.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ghogho.checkers.model.Piece
import com.ghogho.checkers.model.PlayerColor
import com.ghogho.checkers.ui.theme.PieceBlack
import com.ghogho.checkers.ui.theme.PieceRed

@Composable
fun CheckersPiece(
    piece: Piece,
    modifier: Modifier = Modifier
) {
    val baseColor = if (piece.color == PlayerColor.RED) PieceRed else PieceBlack
    val highlightColor = if (piece.color == PlayerColor.RED) Color(0xFFFF8A80) else Color(0xFF757575)

    Box(modifier = modifier.padding(4.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2

            // Base piece
            drawCircle(
                color = baseColor,
                radius = radius,
                center = center
            )

            // Inner styling/ridges to make it look like a checkers piece
            drawCircle(
                color = highlightColor,
                radius = radius * 0.8f,
                center = center,
                style = Stroke(width = 4f)
            )

            drawCircle(
                color = highlightColor,
                radius = radius * 0.6f,
                center = center,
                style = Stroke(width = 2f)
            )

            // If it's a King, draw a crown or a marker
            if (piece.isKing) {
                // Draw a simple crown using a star-like center
                val crownColor = Color(0xFFFFD700) // Gold
                drawCircle(
                    color = crownColor,
                    radius = radius * 0.3f,
                    center = center
                )
                drawCircle(
                     color = baseColor,
                     radius = radius * 0.2f,
                     center = center
                )
            }
        }
    }
}
