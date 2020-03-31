package neilsayok.github.nodemcuiotapptest2.Room.Entities;


import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "BoardItems")
public class BoardItems {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String boardsTable;

    @ColumnInfo
    private String itemName;

    @ColumnInfo
    private byte type;

    @ColumnInfo
    private short value;

    @ColumnInfo
    private  long time;

  /* public BoardItems(String boardsTable, String itemName, byte type, short value, @Nullable long time) {
        this.boardsTable = boardsTable;
        this.itemName = itemName;
        this.type = type;
        this.value = value;
        this.time = time;
    }*/

    public BoardItems(int id, String boardsTable, String itemName, byte type, short value, long time) {
        this.id = id;
        this.boardsTable = boardsTable;
        this.itemName = itemName;
        this.type = type;
        this.value = value;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBoardsTable() {
        return boardsTable;
    }

    public void setBoardsTable(String boardsTable) {
        this.boardsTable = boardsTable;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTime() {
        this.time = System.currentTimeMillis();
    }
}
