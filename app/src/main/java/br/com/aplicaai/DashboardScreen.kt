package br.com.aplicaai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    userName: String,
    totalAnswered: Long,
    correctAnswers: Long,
    subjectsStudied: Long,
    accuracyPercent: Float,
    dashboardMessage: String,
    onAbrirTrilha: () -> Unit,
    onAbrirQuestoes: () -> Unit,
    onAbrirEstatisticas: () -> Unit
) {
    val cores = MaterialTheme.colorScheme
    val precisaoLimitada = accuracyPercent.coerceIn(0f, 100f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cores.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aplica",
                color = cores.onBackground,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "AI",
                color = cores.primary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Olá, $userName! 👋",
            color = cores.onBackground,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Continue sua preparação para o ENEM.",
            color = cores.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ResumoCard(
                titulo = "Respondidas",
                valor = totalAnswered.toString(),
                modifier = Modifier.weight(1f)
            )

            ResumoCard(
                titulo = "Acertos",
                valor = correctAnswers.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ResumoCard(
                titulo = "Matérias estudadas",
                valor = subjectsStudied.toString(),
                modifier = Modifier.weight(1f)
            )

            ResumoCard(
                titulo = "Precisão",
                valor = "${precisaoLimitada.toInt()}%",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = cores.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Progresso dos estudos",
                    color = cores.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Precisão das respostas",
                        color = cores.onSurfaceVariant,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "${precisaoLimitada.toInt()}%",
                        color = cores.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(cores.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(precisaoLimitada / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(cores.primary)
                    )
                }
            }
        }

        if (dashboardMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = dashboardMessage,
                color = cores.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Acessos rápidos",
            color = cores.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AtalhoCard(
                titulo = "Minha Trilha",
                descricao = "Continue seus estudos.",
                icone = Icons.AutoMirrored.Filled.MenuBook,
                onClick = onAbrirTrilha,
                modifier = Modifier.weight(1f)
            )

            AtalhoCard(
                titulo = "Questões",
                descricao = "Pratique para melhorar.",
                icone = Icons.AutoMirrored.Filled.Assignment,
                onClick = onAbrirQuestoes,
                modifier = Modifier.weight(1f)
            )
        }

        AtalhoCard(
            titulo = "Estatísticas",
            descricao = "Acompanhe seu desempenho.",
            icone = Icons.Filled.BarChart,
            onClick = onAbrirEstatisticas
        )
    }
}

@Composable
private fun ResumoCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    val cores = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cores.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = titulo,
                color = cores.onSurfaceVariant,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = valor,
                color = cores.onSurface,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AtalhoCard(
    titulo: String,
    descricao: String,
    icone: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cores = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cores.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(cores.primary.copy(alpha = 0.18f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    tint = cores.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = titulo,
                color = cores.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descricao,
                color = cores.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}