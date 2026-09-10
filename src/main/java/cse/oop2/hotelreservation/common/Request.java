package cse.oop2.hotelreservation.common;
import java.io.Serializable; import java.util.HashMap; import java.util.Map;
public class Request implements Serializable { private Command command; private Map<String,String> params=new HashMap<>(); public Request(Command command){this.command=command;} public Command getCommand(){return command;} public void put(String key,String value){params.put(key,value);} public String get(String key){return params.get(key);} public Map<String,String> getParams(){return params;} }
