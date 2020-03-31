package neilsayok.github.nodemcuiotapptest2.Room.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import neilsayok.github.nodemcuiotapptest2.Room.Entities.Board;

@Dao
public interface BoardsDAO {
    @Insert
    void addBoard(Board board);

    @Query("Select * From Board")
    LiveData<List<Board>> getBoards();

    @Query("Select Count(*) From Board")
    int getBoardsCount();

    @Query("Select boardsTable From Board")
    List<String> getBoardsTables();

    @Query("Select * From Board where id =:id")
    Board getBoardByID(int id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateBoard(Board board);

    @Delete
    public void deleteBoard(Board board);

    @Query("DELETE FROM Board")
    public void deleteAllData();


}
