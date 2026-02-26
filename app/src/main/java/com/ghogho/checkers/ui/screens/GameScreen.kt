package com.ghogho.checkers.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ghogho.checkers.ui.components.BannerAd
import com.ghogho.checkers.ui.components.CheckersBoard
import com.ghogho.checkers.viewmodel.CheckersViewModel
import com.ghogho.checkers.model.PlayerColor

@Composable
fun GameScreen(
    viewModel: CheckersViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Spread out to push ad to bottom
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f) // Take up available space above ad
        ) {
            // Top status bar
            if (state.isGameOver) {
                val winnerText = if (state.winner == PlayerColor.BLACK) "Black Wins!" else "Red Wins!"
                Text(
                    text = "Game Over: $winnerText",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                val turnText = if (state.currentPlayer == PlayerColor.BLACK) "Black's Turn" else "Red's Turn"
                Text(
                    text = turnText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
    
            Spacer(modifier = Modifier.height(32.dp))
    
            // Multi-jump message
            if (state.multiJumpSquare != null) {
                Text(
                    text = "Must continue jumping!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
    
            // The Board
            CheckersBoard(
                gameState = state,
                onSquareTapped = { row, col -> viewModel.onSquareTapped(row, col) },
                modifier = Modifier.fillMaxWidth()
            )
    
            Spacer(modifier = Modifier.height(32.dp))
    
            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { viewModel.restartGame() }) {
                    Text(text = "Restart Game")
                }
                Button(
                    onClick = { viewModel.toggleCpuMode() },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (state.vsCpu) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (state.vsCpu) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(text = if (state.vsCpu) "VS CPU (ON)" else "VS CPU (OFF)")
                }
            }
    
            Spacer(modifier = Modifier.height(16.dp))
    
            // Rules Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Forced Jumps")
                    androidx.compose.material3.Switch(
                        checked = state.settings.forceJumps,
                        onCheckedChange = { viewModel.toggleForceJumps() },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                if (state.vsCpu) {
                    Button(
                        onClick = { 
                            val nextDiff = when(state.settings.difficulty) {
                                com.ghogho.checkers.viewmodel.Difficulty.EASY -> com.ghogho.checkers.viewmodel.Difficulty.MEDIUM
                                com.ghogho.checkers.viewmodel.Difficulty.MEDIUM -> com.ghogho.checkers.viewmodel.Difficulty.HARD
                                com.ghogho.checkers.viewmodel.Difficulty.HARD -> com.ghogho.checkers.viewmodel.Difficulty.EASY
                            }
                            viewModel.setDifficulty(nextDiff)
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text(text = "Diff: ${state.settings.difficulty.name}")
                    }
                }
            }
        }
        
        // AdMob Banner at the very bottom
        BannerAd()
    }
}
