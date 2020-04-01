package neilsayok.github.nodemcuiotapptest2.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import neilsayok.github.nodemcuiotapptest2.Room.DAOs.BoardItemsDAO;
import neilsayok.github.nodemcuiotapptest2.Room.DAOs.BoardsDAO;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.Board;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.BoardItems;

@Database(entities = {Board.class,BoardItems.class},version = 3,exportSchema = false)
public abstract class MyAppDataBase extends RoomDatabase {
    public static final String DB_NAME = "app_database";
    private static MyAppDataBase instance;

    public static synchronized MyAppDataBase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), MyAppDataBase.class,DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }



    public abstract BoardsDAO boardsDao();
    public abstract BoardItemsDAO boardItemsDAO();

}
