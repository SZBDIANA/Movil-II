package com.example.marsphotos.di


import com.example.marsphotos.data.MarsPhotosRepository
import com.example.marsphotos.data.NetworkMarsPhotosRepository
import com.example.marsphotos.network.MarsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)  // Esto hace que las dependencias sean singleton
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://mars.udacity.com/")  // URL de la API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMarsApiService(retrofit: Retrofit): MarsApiService {
        return retrofit.create(MarsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMarsPhotosRepository(apiService: MarsApiService): MarsPhotosRepository {
        return NetworkMarsPhotosRepository(apiService)
    }
}
