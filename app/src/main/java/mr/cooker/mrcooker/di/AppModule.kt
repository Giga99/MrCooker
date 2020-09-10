package mr.cooker.mrcooker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import mr.cooker.mrcooker.data.db.RecipeDatabase
import mr.cooker.mrcooker.data.firebase.FirebaseDatabase
import mr.cooker.mrcooker.other.Constants.RECIPE_DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRecipeDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RecipeDatabase::class.java,
        RECIPE_DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideRecipeDao(db: RecipeDatabase) = db.getRecipeDao()

    @Singleton
    @Provides
    fun provideFirebaseDB() = FirebaseDatabase()
}