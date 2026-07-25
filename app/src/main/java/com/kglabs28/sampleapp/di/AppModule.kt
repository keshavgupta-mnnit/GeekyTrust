package com.kglabs28.sampleapp.di

import android.app.Application
import androidx.room.Room
import com.kglabs28.sampleapp.data.local.LocalManager
import com.kglabs28.sampleapp.data.local.LocalManagerImpl
import com.kglabs28.sampleapp.data.local.db.NewsDatabase
import com.kglabs28.sampleapp.data.remote.NewsApi
import com.kglabs28.sampleapp.data.remote.RemoteManager
import com.kglabs28.sampleapp.data.remote.RemoteManagerImpl
import com.kglabs28.sampleapp.data.repository.NewsRepositoryImpl
import com.kglabs28.sampleapp.data.repository.NewsRepository
import com.kglabs28.sampleapp.data.usecase.BookmarkUseCase
import com.kglabs28.sampleapp.data.usecase.GetNewsUseCase
import com.kglabs28.sampleapp.utils.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteManager(api: NewsApi): RemoteManager {
        return RemoteManagerImpl(api)
    }

    @Provides
    @Singleton
    fun provideLocalManager(database: NewsDatabase): LocalManager {
        return LocalManagerImpl(database)
    }



    @Provides
    @Singleton
    fun provideNewsRepository(
        app: Application,
        localManager: LocalManager,
        remoteManager: RemoteManager
    ): NewsRepository {
        return NewsRepositoryImpl(app, localManager, remoteManager)
    }

    @Provides
    @Singleton
    fun provideGetNewsUseCase(repository: NewsRepository): GetNewsUseCase {
        return GetNewsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideBookmarkUseCase(repository: NewsRepository): BookmarkUseCase {
        return BookmarkUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideNewsDatabase(app: Application): NewsDatabase {
        return Room.databaseBuilder(
            app,
            NewsDatabase::class.java,
            "news_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }


}
