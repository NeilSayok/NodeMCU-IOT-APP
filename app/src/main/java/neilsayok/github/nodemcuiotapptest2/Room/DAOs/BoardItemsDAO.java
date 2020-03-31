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
import neilsayok.github.nodemcuiotapptest2.Room.Entities.BoardItems;

@Dao
public interface BoardItemsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addBoardItem(BoardItems item);

    @Query("Select * From BoardItems")
    LiveData<List<BoardItems>> getAllBoardItems();

    @Query("Select * From BoardItems Where boardsTable = :boardsTable")
    LiveData<List<BoardItems>> getBoardItems(String boardsTable);

    @Query("Select * From BoardItems where id =:id")
    BoardItems getBoardItemByID(int id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateBoardItem(BoardItems items);

    @Delete
    public void deleteBoard(BoardItems items);

    @Query("DELETE FROM BoardItems")
    public void deleteAllBoardItems();

    @Query("DELETE FROM BoardItems Where boardsTable = :boardTable")
    public void deleteBoardItems(String boardTable);





}
