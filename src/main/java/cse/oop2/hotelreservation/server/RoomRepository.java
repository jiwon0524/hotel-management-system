package cse.oop2.hotelreservation.server; import cse.oop2.hotelreservation.common.Room; import java.util.List; public interface RoomRepository { List<Room> findAllRooms(); }
