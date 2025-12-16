import androidx.room.*
import com.example.qrscan.database.data.QRCodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QrItemDao {

    @Query("SELECT * FROM QRCode ORDER BY createdAt DESC")
    fun getAll(): Flow<List<QRCodeEntity>>

    @Query("SELECT * FROM QRCode WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): QRCodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: QRCodeEntity)

    @Update
    suspend fun update(item: QRCodeEntity)

    @Query("DELETE FROM QRCode WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM QRCode")
    suspend fun deleteAll()


}
