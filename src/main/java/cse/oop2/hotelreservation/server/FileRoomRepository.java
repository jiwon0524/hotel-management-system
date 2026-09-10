package cse.oop2.hotelreservation.server;

import cse.oop2.hotelreservation.common.Room;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileRoomRepository implements RoomRepository {
    private static final Logger logger=Logger.getLogger(FileRoomRepository.class.getName());
    private final File roomFile=new File("rooms.txt");
    public FileRoomRepository(){initRoomsFileIfNeeded();}
    private void initRoomsFileIfNeeded(){
        if(roomFile.exists())return;
        String[] ids={"101","102","103","104","105","106","107","201","202","203","204","205","206","207","208","209","210","301","302","303","304","305","306","307","308","309","310","401","402","403","404","405","406","407","408","409","410","501","502","503","504","505"};
        try(BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(roomFile),StandardCharsets.UTF_8))){for(String id:ids){bw.write(id+",false,");bw.newLine();}}catch(IOException e){logger.severe("rooms.txt 기본 생성 중 오류: "+e.getMessage());}
    }
    @Override public List<Room> findAllRooms(){
        List<Room> rooms=new ArrayList<>(); if(!roomFile.exists())return rooms;
        try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(roomFile),StandardCharsets.UTF_8))){String line;while((line=br.readLine())!=null){String[] arr=line.split(",");if(arr.length<2)continue;String id=arr[0].trim();boolean booked=Boolean.parseBoolean(arr[1].trim());String ownerEmail=arr.length>=3?arr[2].trim():"";if(ownerEmail.isEmpty())ownerEmail=null;rooms.add(new Room(id,booked,ownerEmail));}}catch(Exception e){logger.severe("rooms.txt 읽기 오류: "+e.getMessage());}
        return rooms;
    }
}
