package cse.oop2.hotelreservation.client;

import cse.oop2.hotelreservation.common.Room;
import cse.oop2.hotelreservation.common.RoomStatusInfo;
import cse.oop2.hotelreservation.common.Session;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class MapPanel extends JPanel {
    private Image bgImage;
    private List<Room> rooms = new ArrayList<>();
    private int referenceWidth = 1440, referenceHeight = 1034;
    private double scaleX = 1.0, scaleY = 1.0;
    private JLabel infoLabel, imageLabel;
    private DecimalFormat df = new DecimalFormat("#,###");
    private final ServerProxy serverProxy = new ServerProxy();
    private final Map<String, RoomStatusInfo> roomStatusMap = new HashMap<>();

    public MapPanel() {
        addMouseListener(new MouseAdapter(){ @Override public void mouseClicked(MouseEvent e){ handleMouseClick(e.getPoint()); }});
        addMouseMotionListener(new MouseMotionAdapter(){ @Override public void mouseMoved(MouseEvent e){ updateCursorAndInfo(e.getPoint()); }});
    }
    public void setInfoLabels(JLabel textLabel, JLabel imgLabel){ infoLabel=textLabel; imageLabel=imgLabel; }
    public String getRoomIdAt(int x,int y){ for(Room r:rooms){ if(getScaledRect(r.getArea().getBounds()).contains(x,y)) return r.getId(); } return null; }

    private void handleMouseClick(Point clickedPoint){
        for(Room r:rooms){
            if(!getScaledRect(r.getArea().getBounds()).contains(clickedPoint)) continue;
            RoomStatusInfo info=roomStatusMap.get(r.getId());
            String state=info!=null?info.getState():RoomStatusInfo.STATE_AVAILABLE;
            boolean isBooked=info!=null?info.isBooked():r.isBooked();
            String ownerEmail=info!=null?info.getOwnerEmail():r.getOwnerEmail();
            if(!RoomStatusInfo.STATE_AVAILABLE.equals(state)){
                JFrame topFrame=(JFrame)SwingUtilities.getWindowAncestor(this);
                if(!(topFrame instanceof AdminRoomFrame)) JOptionPane.showMessageDialog(this,"현재 객실 정비("+state+") 중이라 예약할 수 없습니다.");
                return;
            }
            if(isBooked){
                if(ownerEmail!=null && ownerEmail.equals(Session.currentEmail)){
                    int answer=JOptionPane.showConfirmDialog(this,r.getId()+"호 예약을 취소하시겠습니까?","취소",JOptionPane.YES_NO_OPTION);
                    if(answer==JOptionPane.YES_OPTION){ cancelReservation(Session.currentEmail,r.getId()); checkRoomStatus(); JOptionPane.showMessageDialog(this,"취소 완료"); }
                } else JOptionPane.showMessageDialog(this,"다른 사용자가 예약한 방입니다.");
                return;
            }
            if(Session.currentEmail==null){ JOptionPane.showMessageDialog(this,"로그인이 필요합니다."); return; }
            JFrame topFrame=(JFrame)SwingUtilities.getWindowAncestor(this);
            if(topFrame instanceof AdminRoomFrame) return;
            new ReservationDialog(topFrame,r.getId(),r,this).setVisible(true); return;
        }
    }

    private void updateCursorAndInfo(Point p){
        boolean hovered=false; String statusText="<html><div style='text-align:center'><br>객실 위에<br>마우스를 올려보세요<br></div></html>"; ImageIcon roomIcon=null;
        for(Room r:rooms){
            if(!getScaledRect(r.getArea().getBounds()).contains(p)) continue;
            hovered=true; RoomStatusInfo info=roomStatusMap.get(r.getId()); String state=info!=null?info.getState():RoomStatusInfo.STATE_AVAILABLE; boolean isBooked=info!=null?info.isBooked():r.isBooked();
            String statusDisplay=RoomStatusInfo.STATE_CLEANING.equals(state)?"<b style='color:black'>[청소 중]</b>":RoomStatusInfo.STATE_CONSTRUCTION.equals(state)?"<b style='color:black'>[공사 중]</b>":isBooked?"<b style='color:red'>[예약 불가]</b>":"<b style='color:blue'>[예약 가능]</b>";
            char floor=r.getId().charAt(0); String type=floor=='5'?"Suite (4인)":(floor=='3'||floor=='4')?"Deluxe (2인)":"Standard (2인)";
            int price; try{ price=info!=null&&info.getPrice()!=null?Integer.parseInt(info.getPrice()):calcDefaultPrice(r.getId()); }catch(NumberFormatException ex){ price=calcDefaultPrice(r.getId()); }
            statusText=String.format("<html><div style='text-align:center; font-size:12px'><h2>%s호</h2>%s<br><br>타입: %s<br>가격: %s원/박</div></html>",r.getId(),statusDisplay,type,df.format(price));
            try{ URL url=getClass().getResource("/cse/oop2/hotelreservation/std_1.png"); if(url!=null){ Image img=new ImageIcon(url).getImage().getScaledInstance(260,170,Image.SCALE_SMOOTH); roomIcon=new ImageIcon(img); }}catch(Exception ignored){}
            break;
        }
        setCursor(hovered?Cursor.getPredefinedCursor(Cursor.HAND_CURSOR):Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(infoLabel!=null) infoLabel.setText(statusText); if(imageLabel!=null){ imageLabel.setIcon(roomIcon); if(roomIcon==null) imageLabel.setText(""); }
    }
    private int calcDefaultPrice(String roomId){ char floor=roomId.charAt(0); if(floor=='5')return 250000; if(floor=='3'||floor=='4')return 150000; return 100000; }
    private Rectangle getScaledRect(Rectangle original){ return new Rectangle((int)(original.x*scaleX),(int)(original.y*scaleY),(int)(original.width*scaleX),(int)(original.height*scaleY)); }
    public void setReferenceSize(int w,int h){ referenceWidth=w; referenceHeight=h; repaint(); }
    public void setBackgroundImage(String imageName){ try{ URL url=getClass().getResource("/cse/oop2/hotelreservation/"+imageName); if(url!=null) bgImage=new ImageIcon(url).getImage(); }catch(Exception ignored){} repaint(); }
    public void clearRooms(){ rooms.clear(); repaint(); }
    public void addRoom(String id,int x,int y,int w,int h){ rooms.add(new Room(id,new Rectangle(x,y,w,h))); }

    @Override protected void paintComponent(Graphics g){
        super.paintComponent(g); Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        int w=getWidth(),h=getHeight(); if(referenceWidth>0){ scaleX=(double)w/referenceWidth; scaleY=(double)h/referenceHeight; } if(bgImage!=null)g2.drawImage(bgImage,0,0,w,h,this);
        for(Room r:rooms){ Rectangle rect=getScaledRect(r.getArea().getBounds()); RoomStatusInfo info=roomStatusMap.get(r.getId()); String state=info!=null?info.getState():RoomStatusInfo.STATE_AVAILABLE; boolean isBooked=info!=null?info.isBooked():r.isBooked();
            Color fillColor; String overlayText="";
            if(RoomStatusInfo.STATE_CLEANING.equals(state)){fillColor=new Color(64,64,64,220);overlayText="청소중";} else if(RoomStatusInfo.STATE_CONSTRUCTION.equals(state)){fillColor=new Color(64,64,64,220);overlayText="공사중";} else if(isBooked)fillColor=new Color(255,0,0,180); else fillColor=new Color(0,255,0,50);
            g2.setColor(fillColor); g2.fill(rect); if(!overlayText.isEmpty()){g2.setColor(Color.WHITE);g2.setFont(new Font("맑은 고딕",Font.BOLD,16));FontMetrics fm=g2.getFontMetrics();g2.drawString(overlayText,rect.x+(rect.width-fm.stringWidth(overlayText))/2,rect.y+(rect.height-fm.getHeight())/2+fm.getAscent());}
        }
    }
    public void cancelReservation(String email,String roomId){ try{ if(!serverProxy.cancelReservation(email,roomId))JOptionPane.showMessageDialog(this,"예약 취소에 실패했습니다.","오류",JOptionPane.ERROR_MESSAGE); }catch(Exception e){JOptionPane.showMessageDialog(this,"서버 통신 중 오류가 발생했습니다.","오류",JOptionPane.ERROR_MESSAGE);} }
    public void checkRoomStatus(){ try{ List<RoomStatusInfo> list=serverProxy.getAllRoomStatuses(); roomStatusMap.clear(); for(RoomStatusInfo info:list){roomStatusMap.put(info.getRoomId(),info);for(Room r:rooms)if(r.getId().equals(info.getRoomId())){r.setBooked(info.isBooked());r.setOwnerEmail(info.getOwnerEmail());}} repaint();}catch(Exception e){e.printStackTrace();JOptionPane.showMessageDialog(this,"객실 상태를 불러오지 못했습니다.","오류",JOptionPane.ERROR_MESSAGE);} }
}
