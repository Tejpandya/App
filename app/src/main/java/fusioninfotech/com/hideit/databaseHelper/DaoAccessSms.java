package fusioninfotech.com.hideit.databaseHelper;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import java.util.List;

import fusioninfotech.com.hideit.Helper.ContactModel;
import fusioninfotech.com.hideit.Helper.SMSData;

/**
 * Created by MAIN on 05-12-2017.
 */
@Dao
public interface DaoAccessSms {


    @Insert
    void insertMultipleSmsList(List<SMSData> list_sms);


}
