package com.example.graphqlsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient


class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://api.github.com/graphql"
    private lateinit var client: ApolloClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        client = setUpApollo()

        button.setOnClickListener {

            client.query(
                FindQueryOnGithubQuery
                    .builder()
                    .name("octocat")
                    .owner("Hello-World")
                    .build()
            ).enqueue(object : ApolloCall.Callback<FindQueryOnGithubQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("TAG1", e.message.toString())
                }

                override fun onResponse(response: Response<FindQueryOnGithubQuery.Data>) {

                    Log.e("TAG2", "${response.data()?.repository()}")

                    runOnUiThread {
                        /*progress_bar.visibility = View.GONE

                        name_text_view.text = String.format(
                            getString(R.string.name_text),
                            response.data()?.repository()?.name()
                        )

                        description_text_view.text = String.format(
                            getString(R.string.description_text),
                            response.data()?.repository()?.description()
                        )
                        forks_text_view.text = String.format(
                            getString(R.string.fork_count_text),
                            response.data()?.repository()?.forkCount().toString()
                        )
                        url_text_view.text = String.format(
                            getString(R.string.url_count_text),
                            response.data()?.repository()?.url().toString()
                        )
*/
                    }
                }

            })
        }


    }

    private fun setUpApollo(): ApolloClient {
        val okHttp = OkHttpClient.Builder().addInterceptor {
            val original = it.request()
            val builder = original.newBuilder().method(
                original.method(),
                original.body()
            )
            builder.addHeader(
                "Authorization",
                "Bearer " + BuildConfig.AUTH_TOKEN

            )
            it.proceed(builder.build())
        }.build()

        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttp)
            .build()
    }
}
