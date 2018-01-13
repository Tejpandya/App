package fusioninfotech.com.hideit.databaseHelper;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import fusioninfotech.com.hideit.Helper.ContactModel;

/**
 * Created by MAIN on 05-12-2017.
 */
@Dao
public interface DaoAccess {
    @Insert
    void insertMultipleRecord(User... users);

    @Insert
    void insertMultipleListRecord(List<User> universities);

    @Insert
    void insertOnlySingleRecord(User user);

    @Query("SELECT * FROM User")
    List<User> fetchAllData();


    @Query("SELECT * FROM User ORDER BY id DESC LIMIT 1")
    List<User> getLastPassword();


    @Update
    void updateRecord(User user);

    @Delete
    void deleteRecord(User user);




}
