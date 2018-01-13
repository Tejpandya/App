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
public interface DaoAccessContact {


    @Insert
    void insertMultipleContactList(List<ContactModel> list_contact);

    @Query("SELECT * FROM ContactModel")
    List<ContactModel> fetchAllData();
}
