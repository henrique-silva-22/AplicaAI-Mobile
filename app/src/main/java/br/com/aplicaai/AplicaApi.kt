package br.com.aplicaai

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class QuestionPageData(
    val questions: List<QuestionData>,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int
)

data class StatisticsData(
    val totalAnswered: Long,
    val correctAnswers: Long,
    val wrongAnswers: Long,
    val accuracy: Double,
    val subjectStatistics: List<SubjectStatisticsData>
)

data class SubjectStatisticsData(
    val subject: String,
    val totalAnswered: Long,
    val correctAnswers: Long,
    val wrongAnswers: Long,
    val accuracy: Double,
    val topicStatistics: List<TopicStatisticsData>
)

data class TopicStatisticsData(
    val topic: String,
    val totalAnswered: Long,
    val correctAnswers: Long,
    val wrongAnswers: Long,
    val accuracy: Double
)

data class QuestionData(
    val id: Long,
    val title: String,
    val context: String?,
    val alternativesIntroduction: String?,
    val subject: String,
    val topic: String,
    val difficulty: String,
    val source: String?,
    val year: Int?,
    val files: List<String>?,
    val alternatives: List<AlternativeData>
)

data class AlternativeData(
    val id: Long,
    val text: String?,
    val file: String?
)

data class AnswerRequest(val alternativeId: Long)

data class AnswerData(
    val questionId: Long,
    val selectedAlternativeId: Long,
    val correctAlternativeId: Long,
    val correct: Boolean
)

data class StudyPlanData(
    val id: Long,
    val subjects: List<StudyPlanSubjectData>
)

data class StudyPlanSubjectData(
    val subject: String,
    val progress: Double,
    val topics: List<StudyPlanTopicData>
)

data class StudyPlanTopicData(
    val id: Long,
    val topic: String,
    val completed: Boolean
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthData(
    val token: String,
    val user: UserData
)

data class UserData(
    val id: Long,
    val name: String,
    val email: String
)

data class DashboardData(
    val user: UserData,
    val progress: ProgressData,
    val subjects: List<SubjectProgressData>,
    val studyPlanProgress: Double
)

data class ProgressData(
    val totalAnswered: Long,
    val correctAnswers: Long,
    val wrongAnswers: Long,
    val accuracy: Double,
    val subjectsStudied: Long
)

data class SubjectProgressData(
    val subject: String,
    val accuracy: Double
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterUserData(
    val id: Long?,
    val name: String,
    val email: String,
    val avatar: String?,
    val targetScore: Int?,
    val level: String?
)

data class RegisterApiResponse(
    val success: Boolean,
    val message: String,
    val data: RegisterUserData?
)

interface AplicaApi {


    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterApiResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<AuthData>

    @GET("dashboard")
    suspend fun getDashboard(
        @Header("Authorization") authorization: String
    ): DashboardData

    @GET("questions")
    suspend fun getQuestions(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("subject") subject: String
    ): QuestionPageData

    @POST("questions/{id}/answer")
    suspend fun answerQuestion(
        @Path("id") questionId: Long,
        @Header("Authorization") authorization: String,
        @Body request: AnswerRequest
    ): AnswerData

    @GET("statistics")
    suspend fun getStatistics(
        @Header("Authorization") authorization: String
    ): StatisticsData

    @GET("study-plan")
    suspend fun getStudyPlan(
        @Header("Authorization") authorization: String
    ): StudyPlanData

    @PUT("study-plan/topics/{id}/complete")
    suspend fun completeStudyTopic(
        @Path("id") id: Long,
        @Header("Authorization") authorization: String
    ): retrofit2.Response<Unit>

    @PUT("study-plan/topics/{id}/complete")
    suspend fun completeTopic(
        @Path("id") topicId: Long,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @GET("questions/import")
    suspend fun importQuestions(
        @Header("Authorization") authorization: String,
        @Query("year") year: Int,
        @Query("discipline") discipline: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ResponseBody
}

object ApiClient {
    val service: AplicaApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AplicaApi::class.java)
    }
}