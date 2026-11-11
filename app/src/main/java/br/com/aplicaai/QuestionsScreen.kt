package br.com.aplicaai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun QuestionsScreen(
    token: String,
    onBack: () -> Unit
) {
    val cores = MaterialTheme.colorScheme

    val materias = listOf(
        "Matemática" to "matematica",
        "Ciências da Natureza" to "ciencias-natureza",
        "Ciências Humanas" to "ciencias-humanas",
        "Linguagens" to "linguagens"
    )

    var materiaSelecionada by remember {
        mutableStateOf(materias[0])
    }

    var menuAberto by remember {
        mutableStateOf(false)
    }

    var questions by remember {
        mutableStateOf<List<QuestionData>>(emptyList())
    }

    var message by remember {
        mutableStateOf("Escolha uma matéria e toque em carregar.")
    }

    var selectedAlternatives by remember {
        mutableStateOf<Map<Long, Long>>(emptyMap())
    }

    var answerMessages by remember {
        mutableStateOf<Map<Long, String>>(emptyMap())
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cores.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Banco de Questões 📝",
            color = cores.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Pratique e acompanhe seu desempenho.",
            color = cores.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = cores.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Matéria",
                    color = cores.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box {
                    OutlinedButton(
                        onClick = { menuAberto = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = materiaSelecionada.first,
                            color = cores.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = menuAberto,
                        onDismissRequest = { menuAberto = false }
                    ) {
                        materias.forEach { materia ->
                            DropdownMenuItem(
                                text = {
                                    Text(materia.first)
                                },
                                onClick = {
                                    materiaSelecionada = materia
                                    menuAberto = false

                                    questions = emptyList()
                                    selectedAlternatives = emptyMap()
                                    answerMessages = emptyMap()
                                    message =
                                        "Toque em carregar para ver esta matéria."
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        scope.launch {
                            message = "Importando e carregando questões..."

                            try {
                                val resultadoImportacao =
                                    ApiClient.service.importQuestions(
                                        authorization = "Bearer $token",
                                        year = 2023,
                                        discipline = materiaSelecionada.second,
                                        limit = 10,
                                        offset = 0
                                    ).string()

                                val page = ApiClient.service.getQuestions(
                                    "Bearer $token",
                                    0,
                                    10,
                                    materiaSelecionada.second
                                )

                                questions = page.questions.filter { question ->
                                    question.subject.equals(
                                        materiaSelecionada.second,
                                        ignoreCase = true
                                    )
                                }

                                selectedAlternatives = emptyMap()
                                answerMessages = emptyMap()

                                message =
                                    "$resultadoImportacao\n" +
                                            "${questions.size} questão(ões) exibida(s) nesta página."
                            } catch (e: HttpException) {
                                message = "Erro da API: ${e.code()}"
                            } catch (e: Exception) {
                                message = "Falha de conexão: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cores.primary
                    )
                ) {
                    Text("Carregar questões")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (message.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cores.surface
                )
            ) {
                Text(
                    text = message,
                    color = cores.onSurfaceVariant,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        questions.forEachIndexed { index, question ->
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
                        text = "Questão ${index + 1}",
                        color = cores.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = question.title,
                        color = cores.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${question.subject} • ${question.topic}",
                        color = cores.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    question.context
                        ?.takeIf { it.isNotBlank() }
                        ?.let { context ->
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = context,
                                color = cores.onSurface,
                                fontSize = 14.sp,
                                lineHeight = 21.sp
                            )
                        }

                    Spacer(modifier = Modifier.height(12.dp))

                    question.alternatives.forEach { alternative ->
                        val selecionada =
                            selectedAlternatives[question.id] == alternative.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedAlternatives =
                                        selectedAlternatives +
                                                (question.id to alternative.id)
                                },
                            shape = RoundedCornerShape(12.dp),
                            border = if (selecionada) {
                                BorderStroke(2.dp, cores.primary)
                            } else {
                                BorderStroke(
                                    1.dp,
                                    cores.onSurfaceVariant.copy(alpha = 0.35f)
                                )
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selecionada) {
                                    cores.primary.copy(alpha = 0.18f)
                                } else {
                                    cores.background
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 4.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selecionada,
                                    onClick = {
                                        selectedAlternatives =
                                            selectedAlternatives +
                                                    (question.id to alternative.id)
                                    }
                                )

                                Text(
                                    text = alternative.text
                                        ?: alternative.file
                                        ?: "Alternativa",
                                    color = cores.onSurface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        enabled = selectedAlternatives[question.id] != null,
                        onClick = {
                            scope.launch {
                                val alternativeId =
                                    selectedAlternatives[question.id]
                                        ?: return@launch

                                answerMessages = answerMessages +
                                        (question.id to "Enviando resposta...")

                                try {
                                    val resultado =
                                        ApiClient.service.answerQuestion(
                                            question.id,
                                            "Bearer $token",
                                            AnswerRequest(alternativeId)
                                        )

                                    answerMessages = answerMessages +
                                            (
                                                    question.id to
                                                            if (resultado.correct) {
                                                                "Resposta correta!"
                                                            } else {
                                                                "Resposta incorreta."
                                                            }
                                                    )
                                } catch (e: HttpException) {
                                    answerMessages = answerMessages +
                                            (
                                                    question.id to
                                                            "Erro da API: ${e.code()}"
                                                    )
                                } catch (e: Exception) {
                                    answerMessages = answerMessages +
                                            (
                                                    question.id to
                                                            "Falha de conexão: ${e.message}"
                                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cores.primary,
                            disabledContainerColor =
                                cores.primary.copy(alpha = 0.35f)
                        )
                    ) {
                        Text("Responder")
                    }

                    val respostaDestaQuestao =
                        answerMessages[question.id].orEmpty()

                    if (respostaDestaQuestao.isNotBlank()) {
                        val corResultado = when (respostaDestaQuestao) {
                            "Resposta correta!" -> cores.tertiary
                            "Resposta incorreta." -> cores.error
                            else -> cores.onSurfaceVariant
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = respostaDestaQuestao,
                            color = corResultado,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}