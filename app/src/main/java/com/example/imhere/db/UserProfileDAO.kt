import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDAO {
    @Query("SELECT * FROM UserProfileEntity")
    fun getAllSubjects(): Flow<List<UserProfileEntity>>

    @Insert
    suspend fun insertSubject(subject: UserProfileEntity)

    @Update
    suspend fun updateSubject(subject: UserProfileEntity)

    @Delete
    suspend fun deleteSubject(subject: UserProfileEntity)

    @Upsert
    suspend fun upsertSubject(subject: UserProfileEntity)
}