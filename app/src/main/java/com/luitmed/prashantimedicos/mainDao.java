package com.luitmed.prashantimedicos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface mainDao {
    // Insert
    @Insert(onConflict = REPLACE)
    void insert(MainData mainData);

    @Delete
    void delete(MainData mainData);

    @Delete
    void reset(List<MainData> mainData);

    @Query("DELETE FROM cart WHERE id = :sID")
    void deleteItem(long sID);

    @Query("UPDATE cart SET number = :sNumber WHERE id = :sID")   //check
    void update(long sID, String sNumber);

    @Query("UPDATE cart SET price = :sPrice WHERE id = :sID")
    void updatePrice(long sID, String sPrice);

    @Query("SELECT * FROM cart")
    List<MainData> getAll();
}
