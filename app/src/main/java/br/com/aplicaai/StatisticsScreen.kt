package br.com.aplicaai

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.HttpException

@Composable
fun StatisticsScreen(
    token: String,
    onBack: () -> Unit
) {
    val cores = MaterialTheme.colorScheme

    var statistics by remember {
        mutableStateOf<StatisticsData?>(null)
    }

    var message by remember {
        mutableStateOf("Carregando estatísticas...")
    }

    LaunchedEffect(token) {
        try {
            statistics = ApiClient.service.getStatistics(
                "Bearer $token"
            )
            message = ""
        } catch (e: HttpException) {
            message = "Erro da API: ${e.code()}"
        } catch (e: Exception) {
            message = "Falha de conexão: ${e.message}"
        }
    }

    val dados = statistics

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cores.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Estatísticas",
            color = cores.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Acompanhe seu desempenho nas questões.",
            color = cores.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (dados == null) {
            StatsMessageCard(message)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatisticsMetricCard(
                    title = "Respondidas",
                    value = dados.totalAnswered.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatisticsMetricCard(
                    title = "Acertos",
                    value = dados.correctAnswers.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatisticsMetricCard(
                    title = "Erros",
                    value = dados.wrongAnswers.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatisticsMetricCard(
                    title = "Precisão",
                    value = "${dados.accuracy.toInt()}%",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Desempenho por matéria",
                color = cores.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (dados.subjectStatistics.isEmpty()) {
                StatsMessageCard(
                    "Responda algumas questões para ver seu desempenho por matéria."
                )
            } else {
                dados.subjectStatistics.forEach { materia ->
                    val nomeMateria = materia.subject
                        .replace('-', ' ')
                        .replace('_', ' ')
                        .lowercase()
                        .replaceFirstChar { it.titlecase() }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cores.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = nomeMateria,
                                color = cores.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text =
                                    "${materia.totalAnswered} respondidas • " +
                                            "${materia.correctAnswers} acertos • " +
                                            "${materia.wrongAnswers} erros",
                                color = cores.onSurfaceVariant,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Precisão",
                                    color = cores.onSurfaceVariant
                                )

                                Text(
                                    text = "${materia.accuracy.toInt()}%",
                                    color = cores.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            StatisticsProgressBar(
                                progressPercent = materia.accuracy
                            )

                            if (materia.topicStatistics.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(18.dp))

                                Text(
                                    text = "Por assunto",
                                    color = cores.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                materia.topicStatistics.forEach { assunto ->
                                    val nomeAssunto = assunto.topic
                                        .replace('-', ' ')
                                        .replace('_', ' ')
                                        .lowercase()
                                        .replaceFirstChar { it.titlecase() }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = nomeAssunto,
                                        color = cores.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text =
                                            "${assunto.totalAnswered} respondidas • " +
                                                    "${assunto.correctAnswers} acertos • " +
                                                    "${assunto.accuracy.toInt()}% de precisão",
                                        color = cores.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )

                                    Spacer(modifier = Modifier.height(7.dp))

                                    StatisticsProgressBar(
                                        progressPercent = assunto.accuracy
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                StatsMessageCard(message)
            }
        }
    }
}

@Composable
private fun StatisticsMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val cores = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = cores.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = title,
                color = cores.onSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = cores.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatisticsProgressBar(
    progressPercent: Double
) {
    val cores = MaterialTheme.colorScheme
    val fraction = (progressPercent / 100.0)
        .toFloat()
        .coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(cores.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(cores.primary)
        )
    }
}

@Composable
private fun StatsMessageCard(texto: String) {
    val cores = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cores.surface
        )
    ) {
        Text(
            text = texto,
            color = cores.onSurfaceVariant,
            modifier = Modifier.padding(16.dp),
            fontSize = 14.sp
        )
    }
}