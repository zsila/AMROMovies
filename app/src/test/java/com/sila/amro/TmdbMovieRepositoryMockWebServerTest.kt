package com.sila.amro

import com.sila.amro.data.network.TmdbApi
import com.sila.amro.data.repo.TmdbMovieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class TmdbMovieRepositoryMockWebServerTest {

    private lateinit var server: MockWebServer
    private lateinit var repo: TmdbMovieRepository

    @Before
    fun setUp() {
        server = MockWebServer()
        server.dispatcher = tmdbDispatcher()

        server.start()

        val okHttp = OkHttpClient.Builder()
            // Note: we purposely do NOT add the auth interceptor here; this is a repo+Retrofit test.
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(TmdbApi::class.java)
        repo = TmdbMovieRepository(api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getTop100ThisWeek requests pages 1-5, dedupes, and returns exactly 100`() = runTest {
        val movies = repo.getTop100ThisWeek()

        // Your repo: distinctBy(id) then take(100)
        assertEquals(100, movies.size)
        assertEquals(1, movies.first().id)
        assertEquals(100, movies.last().id)

        // Verify 5 requests were made to the trending endpoint with pages 1..5 (order not guaranteed)
        val requests = (1..5).map { server.takeRequest() }
        val pages = requests.mapNotNull { it.requestUrl?.queryParameter("page") }.map { it.toInt() }.toSet()
        assertEquals(setOf(1, 2, 3, 4, 5), pages)

        // Verify paths are correct
        requests.forEach { req ->
            assertEquals("/trending/movie/week", req.requestUrl?.encodedPath)
        }
    }

    @Test
    fun `getGenres returns mapped genres`() = runTest {
        val genres = repo.getGenres()
        assertEquals(listOf(1, 2), genres.map { it.id })
        assertEquals(listOf("Comedy", "Action"), genres.map { it.name })

        val req = server.takeRequest()
        assertEquals("/genre/movie/list", req.requestUrl?.encodedPath)
    }

    @Test
    fun `getMovieDetail calls correct endpoint and maps detail`() = runTest {
        val detail = repo.getMovieDetail(7)
        assertEquals(7, detail.id)
        assertEquals("Seven", detail.title)
        assertEquals("Tag", detail.tagline)
        assertEquals(2, detail.genres.size)

        val req = server.takeRequest()
        assertEquals("/movie/7", req.requestUrl?.encodedPath)
    }

    /**
     * A dispatcher that simulates TMDB endpoints.
     * - trending/movie/week?page=N returns 30 results with overlap between pages to test distinctBy.
     *   Page1: 1..30
     *   Page2: 26..55
     *   Page3: 51..80
     *   Page4: 76..105
     *   Page5: 101..130
     * Distinct total = 130; repo takes first 100 => ids 1..100.
     */
    private fun tmdbDispatcher(): Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.requestUrl?.encodedPath ?: return notFound()

            return when {
                path == "/trending/movie/week" -> {
                    val page = request.requestUrl?.queryParameter("page")?.toIntOrNull() ?: 1
                    val json = trendingPageJson(page)
                    okJson(json)
                }

                path == "/genre/movie/list" -> okJson(
                    """
                    {
                      "genres": [
                        {"id": 1, "name": "Comedy"},
                        {"id": 2, "name": "Action"}
                      ]
                    }
                    """.trimIndent()
                )

                path.startsWith("/movie/") -> {
                    val id = path.removePrefix("/movie/").toIntOrNull() ?: 0
                    okJson(movieDetailJson(id))
                }

                else -> notFound()
            }
        }
    }

    private fun trendingPageJson(page: Int): String {
        val start = (page - 1) * 25 + 1
        val ids = (start until start + 30) // 30 items; overlap of 5 with previous page

        val resultsJson = ids.joinToString(separator = ",") { id ->
            """
            {
              "id": $id,
              "title": "Movie $id",
              "poster_path": "/p$id.jpg",
              "genre_ids": [1, 2],
              "popularity": ${1000.0 - id},
              "release_date": "2020-01-01"
            }
            """.trimIndent()
        }

        return """
        {
          "page": $page,
          "results": [
            $resultsJson
          ]
        }
        """.trimIndent()
    }

    private fun movieDetailJson(id: Int): String = """
        {
          "id": $id,
          "title": "Seven",
          "tagline": "Tag",
          "poster_path": "/p$id.jpg",
          "genres": [
            {"id": 1, "name": "Comedy"},
            {"id": 2, "name": "Action"}
          ],
          "overview": "Overview",
          "vote_average": 7.7,
          "vote_count": 777,
          "budget": 1000000,
          "revenue": 5000000,
          "status": "Released",
          "imdb_id": "tt1234567",
          "runtime": 120,
          "release_date": "2020-01-01"
        }
    """.trimIndent()

    private fun okJson(body: String): MockResponse =
        MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(body)

    private fun notFound(): MockResponse =
        MockResponse().setResponseCode(404).setBody("""{"status_message":"Not Found"}""")
}
