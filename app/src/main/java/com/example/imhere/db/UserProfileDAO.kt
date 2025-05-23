import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDAO {
    @Query("SELECT * FROM UserProfile")
    fun getAllSubjects(): Flow<List<UserProfile>>

    @Insert
    suspend fun insertSubject(subject: UserProfile)

    @Update
    suspend fun updateSubject(subject: UserProfile)

    @Delete
    suspend fun deleteSubject(subject: UserProfile)
}