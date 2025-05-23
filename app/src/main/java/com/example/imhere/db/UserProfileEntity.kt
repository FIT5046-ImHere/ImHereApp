import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.imhere.db.DateConverter
import com.example.imhere.db.UserProfileTypeConverter
import com.example.imhere.model.UserProfileType
import java.util.Date

@Entity
@TypeConverters(UserProfileTypeConverter::class, DateConverter::class)
data class UserProfileEntity(
    @PrimaryKey
    val uid: String = "",

    val name: String = "",
    val email: String = "",
    val type: UserProfileType = UserProfileType.STUDENT,
    val birthDate: Date = Date()
)


