package cse.oop2.hotelreservation.server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUserRepository implements UserRepository {
    private static final Logger logger=Logger.getLogger(FileUserRepository.class.getName());
    private final File userFile;
    public FileUserRepository(){this.userFile=new File("users.txt");checkAndCopyUsersFile();}
    private void checkAndCopyUsersFile(){
        if(!userFile.exists()){
            try(InputStream is=getClass().getResourceAsStream("/cse/oop2/hotelreservation/users.txt");OutputStream os=new FileOutputStream(userFile)){
                if(is==null){logger.warning("Default users.txt resource not found!");return;} byte[] buffer=new byte[1024];int length;while((length=is.read(buffer))>0)os.write(buffer,0,length);
            }catch(IOException e){logger.log(Level.SEVERE,"Could not copy default user file",e);}
        }
    }
    public String findPasswordByEmail(String email){
        if(!userFile.exists())return null; try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(userFile),StandardCharsets.UTF_8))){String line;while((line=br.readLine())!=null){String[] d=line.split(",");if(d.length!=3)continue;if(d[1].trim().equals(email))return d[2].trim();}}catch(IOException e){logger.log(Level.SEVERE,"파일 읽기 오류",e);} return null;
    }
    public boolean existsByEmail(String email){
        if(!userFile.exists())return false; try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(userFile),StandardCharsets.UTF_8))){String line;while((line=br.readLine())!=null){String[] d=line.split(",");if(d.length==3&&d[1].trim().equals(email))return true;}}catch(IOException e){logger.log(Level.SEVERE,"파일 읽기 오류",e);} return false;
    }
    public boolean saveUser(String name,String email,String password){
        try(BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userFile,true),StandardCharsets.UTF_8))){bw.write(name+","+email+","+password);bw.newLine();return true;}catch(IOException e){logger.log(Level.SEVERE,"파일 쓰기 오류",e);return false;}
    }
}
