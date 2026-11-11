package br.com.aplicaai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import br.com.aplicaai.ui.theme.AplicaAiTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip






class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AplicaAiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    LoginScreen()
                }
            }
        }
}

    @Composable
    fun LoginScreen() {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var token by remember { mutableStateOf<String?>(null) }
        var userName by remember { mutableStateOf("Estudante") }
        var mostrarCadastro by remember { mutableStateOf(false) }
        var abaSelecionada by remember { mutableStateOf(0) }

        var totalAnswered by remember { mutableStateOf(0L) }
        var correctAnswers by remember { mutableStateOf(0L) }
        var subjectsStudied by remember { mutableStateOf(0L) }
        var accuracyPercent by remember { mutableStateOf(0f) }

        var dashboardMessage by remember {
            mutableStateOf("Carregando desempenho...")
        }
        var message by remember {
            mutableStateOf("")
        }

        val scope = rememberCoroutineScope()

        // Quando houver token, busca os dados do dashboard.
        LaunchedEffect(token) {
            val tokenAtual = token ?: return@LaunchedEffect

            dashboardMessage = "Carregando desempenho..."

            try {
                val dashboard = ApiClient.service.getDashboard(
                    "Bearer $tokenAtual"
                )

                totalAnswered = dashboard.progress.totalAnswered
                correctAnswers = dashboard.progress.correctAnswers
                subjectsStudied = dashboard.progress.subjectsStudied
                accuracyPercent = dashboard.progress.accuracy
                    .toFloat()
                    .coerceIn(0f, 100f)

                dashboardMessage = ""
            } catch (e: HttpException) {
                dashboardMessage =
                    "Erro ao carregar o Dashboard: ${e.code()}"
            } catch (e: Exception) {
                dashboardMessage =
                    "Falha de conexão: ${e.message}"
            }
        }

        // Usuário conectado: mostra as telas e a navegação inferior.
        if (token != null) {
            val nomesAbas = listOf(
                "Início",
                "Trilha",
                "Questões",
                "Estatísticas",
                "Perfil"
            )

            val iconesAbas = listOf(
                Icons.Filled.Home,
                Icons.AutoMirrored.Filled.MenuBook,
                Icons.AutoMirrored.Filled.Assignment,
                Icons.Filled.BarChart,
                Icons.Filled.Person
            )

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        nomesAbas.forEachIndexed { indice, nome ->
                            NavigationBarItem(
                                selected = abaSelecionada == indice,
                                onClick = {
                                    abaSelecionada = indice
                                },
                                icon = {
                                    Icon(
                                        imageVector = iconesAbas[indice],
                                        contentDescription = nome
                                    )
                                },
                                label = {
                                    Text(nome)
                                }
                            )
                        }
                    }
                }
            ) { espacoDaBarra ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(espacoDaBarra)
                ) {
                    when (abaSelecionada) {
                        0 -> DashboardScreen(
                            userName = userName,
                            totalAnswered = totalAnswered,
                            correctAnswers = correctAnswers,
                            subjectsStudied = subjectsStudied,
                            accuracyPercent = accuracyPercent,
                            dashboardMessage = dashboardMessage,
                            onAbrirTrilha = {
                                abaSelecionada = 1
                            },
                            onAbrirQuestoes = {
                                abaSelecionada = 2
                            },
                            onAbrirEstatisticas = {
                                abaSelecionada = 3
                            }
                        )

                        1 -> StudyPlanScreen(
                            token = token!!,
                            onBack = {
                                abaSelecionada = 0
                            }
                        )

                        2 -> QuestionsScreen(
                            token = token!!,
                            onBack = {
                                abaSelecionada = 0
                            }
                        )

                        3 -> StatisticsScreen(
                            token = token!!,
                            onBack = {
                                abaSelecionada = 0
                            }
                        )

                        else -> ProfileScreen(
                            onLogout = {
                                token = null
                                abaSelecionada = 0
                                password = ""
                                userName = "Estudante"
                                totalAnswered = 0L
                                correctAnswers = 0L
                                subjectsStudied = 0L
                                accuracyPercent = 0f
                                dashboardMessage = "Carregando desempenho..."
                            }
                        )
                    }
                }
            }
        } else {
            // Usuário desconectado: mostra login ou cadastro.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "AplicaAI",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (mostrarCadastro) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = {
                            Text("Nome")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text("E-mail")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text("Senha")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            if (mostrarCadastro) {
                                if (name.trim().length < 3) {
                                    message =
                                        "Digite um nome com pelo menos 3 caracteres."
                                    return@launch
                                }

                                if (email.isBlank()) {
                                    message = "Digite seu e-mail."
                                    return@launch
                                }

                                if (password.length < 8) {
                                    message =
                                        "A senha precisa ter pelo menos 8 caracteres."
                                    return@launch
                                }

                                message = "Criando conta..."

                                try {
                                    val resposta =
                                        ApiClient.service.register(
                                            RegisterRequest(
                                                name = name.trim(),
                                                email = email.trim(),
                                                password = password
                                            )
                                        )

                                    mostrarCadastro = false
                                    name = ""
                                    password = ""
                                    message =
                                        "${resposta.message} Agora entre com sua conta."
                                } catch (e: HttpException) {
                                    message =
                                        "Erro da API: ${e.code()}. Verifique os dados ou se o e-mail já está cadastrado."
                                } catch (e: Exception) {
                                    message =
                                        "Falha de conexão: ${e.message}"
                                }
                            } else {
                                if (email.isBlank() || password.isBlank()) {
                                    message = "Preencha o e-mail e a senha."
                                    return@launch
                                }

                                message = "Conectando..."

                                try {
                                    val resposta =
                                        ApiClient.service.login(
                                            LoginRequest(
                                                email.trim(),
                                                password
                                            )
                                        )

                                    userName = resposta.data.user.name
                                    token = resposta.data.token
                                    message = "Login realizado!"
                                } catch (e: HttpException) {
                                    message =
                                        "Erro da API: ${e.code()}. Confira o e-mail e a senha."
                                } catch (e: Exception) {
                                    message =
                                        "Falha de conexão: ${e.message}"
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        if (mostrarCadastro) "Criar conta" else "Entrar"
                    )
                }

                TextButton(
                    onClick = {
                        mostrarCadastro = !mostrarCadastro
                        message = if (mostrarCadastro) {
                            "Preencha os dados para criar sua conta."
                        } else {
                            "Digite seus dados para entrar."
                        }
                    }
                ) {
                    Text(
                        if (mostrarCadastro) {
                            "Já tem uma conta? Entrar"
                        } else {
                            "Não tem uma conta? Criar cadastro"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(message)
            }
        }
    }
}

