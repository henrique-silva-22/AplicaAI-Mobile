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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun StudyPlanScreen(
    token: String,
    onBack: () -> Unit
) {
    val cores = MaterialTheme.colorScheme

    var plan by remember {
        mutableStateOf<StudyPlanData?>(null)
    }

    var message by remember {
        mutableStateOf("Carregando plano de estudos...")
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(token) {
        try {
            plan = ApiClient.service.getStudyPlan("Bearer $token")
            message = ""
        } catch (e: HttpException) {
            message = if (e.code() == 404) {
                "Esta conta ainda não tem um plano de estudos cadastrado."
            } else {
                "Erro da API: ${e.code()}"
            }
        } catch (e: Exception) {
            message = "Falha de conexão: ${e.message}"
        }
    }

    val planoAtual = plan

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cores.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Minha Trilha",
            color = cores.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Acompanhe o progresso dos seus estudos.",
            color = cores.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            planoAtual == null -> {
                MensagemTrilhaCard(texto = message)
            }

            planoAtual.subjects.isEmpty() -> {
                MensagemTrilhaCard(
                    texto = "Este plano ainda não tem matérias."
                )
            }

            else -> {
                planoAtual.subjects.forEach { subject ->
                    val totalTopics = subject.topics.size
                    val completedTopics =
                        subject.topics.count { it.completed }

                    val progressFraction =
                        (subject.progress / 100.0)
                            .toFloat()
                            .coerceIn(0f, 1f)

                    val nomeMateria = subject.subject
                        .replace('_', ' ')
                        .replace('-', ' ')
                        .lowercase()
                        .replaceFirstChar { it.titlecase() }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
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
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$completedTopics de $totalTopics assuntos concluídos",
                                    color = cores.onSurfaceVariant,
                                    fontSize = 13.sp
                                )

                                Text(
                                    text = "${subject.progress.toInt()}%",
                                    color = cores.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(cores.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progressFraction)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(50))
                                        .background(cores.primary)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            subject.topics.forEach { topic ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = topic.completed,
                                        enabled = !topic.completed,
                                        onCheckedChange = { marcado ->
                                            if (marcado) {
                                                scope.launch {
                                                    message =
                                                        "Atualizando seu plano..."

                                                    try {
                                                        val resposta =
                                                            ApiClient.service
                                                                .completeStudyTopic(
                                                                    topic.id,
                                                                    "Bearer $token"
                                                                )

                                                        if (resposta.isSuccessful) {
                                                            plan =
                                                                ApiClient.service
                                                                    .getStudyPlan(
                                                                        "Bearer $token"
                                                                    )
                                                            message = ""
                                                        } else {
                                                            message =
                                                                "Não foi possível concluir o assunto."
                                                        }
                                                    } catch (e: HttpException) {
                                                        message =
                                                            "Erro da API: ${e.code()}"
                                                    } catch (e: Exception) {
                                                        message =
                                                            "Falha de conexão: ${e.message}"
                                                    }
                                                }
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = cores.primary,
                                            uncheckedColor =
                                                cores.onSurfaceVariant,
                                            checkmarkColor = cores.onPrimary
                                        )
                                    )

                                    Text(
                                        text = nomeMateria,
                                        modifier = Modifier.weight(1f),
                                        color = if (topic.completed) {
                                            cores.onSurfaceVariant
                                        } else {
                                            cores.onSurface
                                        },
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MensagemTrilhaCard(texto = message)
                }
            }
        }
    }
}

@Composable
private fun MensagemTrilhaCard(texto: String) {
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