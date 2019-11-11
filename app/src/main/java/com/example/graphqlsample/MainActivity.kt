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
    private val TAG = this.javaClass.simpleName
    private lateinit var client: ApolloClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        client = setUpApollo()

        button.setOnClickListener {
            Log.d(TAG, "* * * Click :: ${editInputName.text} :: ${editInputOwner.text}")
            client.query(
                FindQueryOnGithubQuery
                    .builder()
                    .name(editInputName.text.toString())
                    .owner(editInputOwner.text.toString())
                    .build()
            ).enqueue(object : ApolloCall.Callback<FindQueryOnGithubQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.d(TAG, "* * * onFailure")
                    Log.d(TAG, "* * * " + e.message.toString())
                }

                override fun onResponse(response: Response<FindQueryOnGithubQuery.Data>) {
                    Log.d(TAG, "* * * onResponse")
                    Log.d(TAG, "* * * ${response.data()?.repository()}")

                    runOnUiThread {
                        textName.text = response.data()?.repository?.name
                        textDes.text = response.data()?.repository?.description
                        textForkCount.text = response.data()?.repository?.forkCount.toString()
                        textUrl.text = response.data()?.repository?.url.toString()
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
