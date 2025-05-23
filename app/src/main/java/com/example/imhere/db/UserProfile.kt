import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.imhere.db.DateConverter
import com.example.imhere.db.UserProfileTypeConverter
import java.util.Date

@Entity
@TypeConverters(UserProfileTypeConverter::class, DateConverter::class)
data class UserProfile(
    @PrimaryKey
    val uid: String = "",

    val name: String = "",
    val email: String = "",
    val type: UserProfileType = UserProfileType.STUDENT,
    val birthDate: Date = Date()
)

enum class UserProfileType(val value: String) {
    STUDENT("student"),
    TEACHER("teacher");

    companion object {
        fun fromValue(value: String): UserProfileType {
            return entries.firstOrNull { it.value == value }
                ?: STUDENT
        }
    }
}
