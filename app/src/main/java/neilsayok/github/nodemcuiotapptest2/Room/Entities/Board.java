package neilsayok.github.nodemcuiotapptest2.Room.Entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Board")
public class Board {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String boardName;
    @ColumnInfo
    private String imgUrl;
    @ColumnInfo
    private String boardsTable;
    @ColumnInfo
    private boolean wifiDirectStat;
    @ColumnInfo
    private boolean httpDirectStat;



    public Board(String boardName, String imgUrl, String boardsTable) {
        this.boardName = boardName;
        this.imgUrl = imgUrl;
        this.boardsTable = boardsTable;
        this.wifiDirectStat = false;
        this.httpDirectStat = false;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBoardsTable() {
        return boardsTable;
    }

    public void setBoardsTable(String boardsTable) {
        this.boardsTable = boardsTable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isWifiDirectStat() {
        return wifiDirectStat;
    }

    public void setWifiDirectStat(boolean wifiDirectStat) {
        this.wifiDirectStat = wifiDirectStat;
    }

    public boolean isHttpDirectStat() {
        return httpDirectStat;
    }

    public void setHttpDirectStat(boolean httpDirectStat) {
        this.httpDirectStat = httpDirectStat;
    }
}
