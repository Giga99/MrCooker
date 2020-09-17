package mr.cooker.mrcooker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import mr.cooker.mrcooker.data.firebase.FirebaseDB
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseDB() = FirebaseDB()
}