package fusioninfotech.com.hideit.databaseHelper;

/**
 * Created by MAIN on 05-12-2017.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import fusioninfotech.com.hideit.Helper.ContactModel;
import fusioninfotech.com.hideit.Helper.SMSData;

@Database(entities = {User.class,ContactModel.class, SMSData.class}, version = 1)

public abstract class UserDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess();
    public abstract DaoAccessContact daoAccessContact();
    public abstract DaoAccessSms daoAccessSms();

}





