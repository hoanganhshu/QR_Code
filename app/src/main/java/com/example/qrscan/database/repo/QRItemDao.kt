import androidx.room.*
import com.example.qrscan.database.data.QRCodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QrItemDao {

    @Query("SELECT * FROM QRCode ORDER BY createdAt DESC")
    fun getAll(): Flow<List<QRCodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: QRCodeEntity)

    @Update
    suspend fun update(item: QRCodeEntity)

    @Query("DELETE FROM QRCode WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM QRCode")
    suspend fun deleteAll()
    @Query("DELETE FROM QRCode WHERE id IN (:ids)")
    suspend fun deleteListById(ids: List<Int>)

    @Query("SELECT * FROM QRCode WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): QRCodeEntity?





}
