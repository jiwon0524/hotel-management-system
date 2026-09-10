package cse.oop2.hotelreservation.client;

import cse.oop2.hotelreservation.common.Command;
import cse.oop2.hotelreservation.common.Request;
import cse.oop2.hotelreservation.common.Response;
import cse.oop2.hotelreservation.common.Room;
import cse.oop2.hotelreservation.common.RoomStatusInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ServerProxy {
    private final String host = "localhost";
    private final int port = 5000;

    private Response send(Request request) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();
        }
    }

    public Response login(String email, String password) throws IOException, ClassNotFoundException {
        Request req = new Request(Command.LOGIN); req.put("email", email); req.put("password", password); return send(req);
    }
    public Response signup(String fullName, String email, String password) throws IOException, ClassNotFoundException {
        Request req = new Request(Command.SIGNUP); req.put("name", fullName); req.put("email", email); req.put("password", password); return send(req);
    }
    public List<Room> getRooms() throws IOException, ClassNotFoundException {
        Response res = send(new Request(Command.GET_ROOMS));
        if (!res.isSuccess()) throw new IOException("방 목록 조회 실패: " + res.getMessage());
        @SuppressWarnings("unchecked") List<Room> rooms = (List<Room>) res.getData(); return rooms;
    }
    public boolean cancelReservation(String email, String roomId) throws IOException, ClassNotFoundException {
        Request req = new Request(Command.CANCEL_RESERVATION); req.put("email", email); req.put("roomId", roomId); return send(req).isSuccess();
    }
    public boolean makeReservation(String email, String roomId, String name, String phone, String payment, String carNumber, String checkIn, String checkOut, String totalPrice, String guestCount, String checkoutTime) throws IOException, ClassNotFoundException {
        Request req = new Request(Command.MAKE_RESERVATION);
        req.put("email", email); req.put("roomId", roomId); req.put("name", name); req.put("phone", phone); req.put("payment", payment); req.put("carNumber", carNumber); req.put("checkIn", checkIn); req.put("checkOut", checkOut); req.put("totalPrice", totalPrice); req.put("guestCount", guestCount); req.put("checkoutTime", checkoutTime);
        return send(req).isSuccess();
    }
    public List<RoomStatusInfo> getAllRoomStatuses() throws IOException, ClassNotFoundException {
        Response res = send(new Request(Command.GET_ALL_ROOM_STATUSES));
        if (!res.isSuccess()) throw new IOException("객실 상태 조회 실패: " + res.getMessage());
        @SuppressWarnings("unchecked") List<RoomStatusInfo> list = (List<RoomStatusInfo>) res.getData(); return list;
    }
    public boolean changeRoomStatus(String roomId, String state, String price, String note) throws IOException, ClassNotFoundException {
        Request req = new Request(Command.CHANGE_ROOM_STATUS); req.put("roomId", roomId); req.put("state", state); req.put("price", price); req.put("note", note); return send(req).isSuccess();
    }
}
