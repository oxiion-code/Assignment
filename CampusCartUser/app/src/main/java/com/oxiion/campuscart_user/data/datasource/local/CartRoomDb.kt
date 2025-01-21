package com.oxiion.campuscart_user.data.datasource.local

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity(tableName = "cart")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "product_id") val productId: String,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "product_category") val productCategory: String,
    @ColumnInfo(name = "product_quantity") val quantity: Int,
    @ColumnInfo(name = "product_rating") val rating: Double,
    @ColumnInfo(name = "product_is_available") val isAvailable: Boolean,
    @ColumnInfo(name = "product_discount_price") val discountedPrice: Double?,
    @ColumnInfo(name = "product_description") val description: String,
    @ColumnInfo(name = "product_price") val price: Double,
    @ColumnInfo(name = "product_image") val image: String,
    @ColumnInfo(name = "total_price") val totalPrice:Double
)


@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun removeFromCart(cartItem: CartItem)

    @Query("SELECT * FROM cart WHERE product_id = :productId LIMIT 1")
    suspend fun findCartItemByProductId(productId: String): CartItem?

    @Query("SELECT * FROM cart")
    suspend fun getAllCartItems(): List<CartItem>

    @Query("SELECT SUM(product_quantity * product_price) FROM cart")
    suspend fun getTotalPrice(): Double

    @Query("SELECT SUM(product_quantity * product_discount_price) FROM cart")
    suspend fun getTotalDiscountedPrice(): Double

    @Query("DELETE FROM cart")
    suspend fun clearCart()
}

@Database(entities = [CartItem::class], version = 4, exportSchema = true)
abstract class CartDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: CartDatabase? = null

        fun getDatabase(context: Context): CartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartDatabase::class.java,
                    "cart_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



