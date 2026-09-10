package cse.oop2.hotelreservation.server;

import cse.oop2.hotelreservation.common.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.*;

public class ClientHandler extends Thread {
    private static final Logger logger=Logger.getLogger(ClientHandler.class.getName());
    private final Socket socket; private final AuthService authService; private final RoomService roomService;
    public ClientHandler(Socket socket,AuthService authService,RoomService roomService){this.socket=socket;this.authService=authService;this.roomService=roomService;}
    @Override public void run(){try(ObjectInputStream in=new ObjectInputStream(socket.getInputStream());ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream())){Request req=(Request)in.readObject();Response res=handle(req);out.writeObject(res);out.flush();}catch(Exception e){logger.log(Level.SEVERE,"클라이언트 처리 중 오류",e);}finally{try{socket.close();}catch(IOException ignored){}}}
    private Response handle(Request req){
        switch(req.getCommand()){
            case LOGIN:{boolean ok=authService.login(req.get("email"),req.get("password"));return new Response(ok,ok?"로그인 성공":"Email 또는 비밀번호가 일치하지 않습니다.",null);}
            case SIGNUP:{boolean ok=authService.signup(req.get("name"),req.get("email"),req.get("password"));return new Response(ok,ok?"회원가입 성공":"이미 존재하는 이메일입니다.",null);}
            case GET_ROOMS:return new Response(true,"방 목록 조회 성공",roomService.getAllRooms());
            case MAKE_RESERVATION:{
                File file=new File("reservations.txt");
                try(PrintWriter out=new PrintWriter(new BufferedWriter(new FileWriter(file,true)))){out.println(String.join(",",req.get("email"),req.get("roomId"),req.get("name"),req.get("phone"),req.get("payment"),req.get("carNumber"),req.get("checkIn"),req.get("checkOut"),req.get("totalPrice"),req.get("guestCount"),req.get("checkoutTime")));return new Response(true,"예약이 완료되었습니다.",null);}catch(IOException e){logger.log(Level.SEVERE,"예약 저장 실패",e);return new Response(false,"예약 처리 중 오류가 발생했습니다.",null);}}
            case CANCEL_RESERVATION:{
                String email=req.get("email"),roomId=req.get("roomId");File in=new File("reservations.txt"),tmp=new File("reservations_temp.txt");if(!in.exists())return new Response(false,"예약 내역이 없습니다.",null);boolean removed=false;
                try(BufferedReader br=new BufferedReader(new FileReader(in));BufferedWriter bw=new BufferedWriter(new FileWriter(tmp))){String line;while((line=br.readLine())!=null){String[] d=line.split(",");if(d.length>=2&&d[0].trim().equals(email)&&d[1].trim().equals(roomId)){removed=true;continue;}bw.write(line);bw.newLine();}}catch(IOException e){return new Response(false,"예약 취소 처리 중 오류가 발생했습니다.",null);}if(!removed){tmp.delete();return new Response(false,"해당 예약을 찾을 수 없습니다.",null);}if(!in.delete()||!tmp.renameTo(in))return new Response(false,"예약 파일 갱신에 실패했습니다.",null);return new Response(true,"예약이 취소되었습니다.",null);}
            case GET_ALL_ROOM_STATUSES:{
                try{List<Room> rooms=roomService.getAllRooms();Map<String,String> ownerByRoomId=new HashMap<>();File f=new File("reservations.txt");if(f.exists())try(BufferedReader br=new BufferedReader(new FileReader(f))){String line;while((line=br.readLine())!=null){String[] d=line.split(",");if(d.length>=2)ownerByRoomId.put(d[1].trim(),d[0].trim());}}RoomStatusManager.loadStatuses();List<RoomStatusInfo> result=new ArrayList<>();for(Room r:rooms){String id=r.getId();result.add(new RoomStatusInfo(id,RoomStatusManager.getStatus(id),RoomStatusManager.getPrice(id),RoomStatusManager.getNote(id),ownerByRoomId.containsKey(id),ownerByRoomId.get(id)));}return new Response(true,"객실 상태 조회 성공",result);}catch(Exception e){return new Response(false,"객실 상태 조회 중 오류가 발생했습니다.",null);}}
            case CHANGE_ROOM_STATUS:{try{RoomStatusManager.loadStatuses();RoomStatusManager.saveStatus(req.get("roomId"),req.get("state"),req.get("price"),req.get("note"));return new Response(true,"객실 상태 변경 성공",null);}catch(Exception e){return new Response(false,"객실 상태 변경 중 오류가 발생했습니다.",null);}}
            default:return new Response(false,"지원하지 않는 명령입니다.",null);
        }
    }
}
