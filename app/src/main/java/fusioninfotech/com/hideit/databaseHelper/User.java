package fusioninfotech.com.hideit.databaseHelper;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by MAIN on 05-12-2017.
 */
@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String password;
    private String password_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_type() {
        return password_type;
    }

    public void setPassword_type(String password_type) {
        this.password_type = password_type;
    }

}
