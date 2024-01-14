
public class Room {
    public final int roomNo;
    public final String roomType;
    public final boolean availability;

    public Room(int roomNo, String roomType, boolean availability) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.availability = availability;
    }

    // Getter methods for the properties
    public int getRoomNo() {
        return roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public boolean isAvailable() {
        return availability;
    }
}
