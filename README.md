# Hotel Management System (HMS)

Java Swing 기반의 호텔 통합 관리 시스템입니다. 4인 팀 프로젝트로 진행했으며, 예약·체크인·객실 관리·식음료 주문·문의/채팅·관리자 기능을 하나의 GUI 프로그램으로 구현했습니다.

## 핵심 구현
- Java Swing 기반 데스크톱 GUI
- Java Socket + ObjectStream 기반 Client-Server 통신
- 클라이언트 연결별 `ClientHandler` 스레드 처리
- Request/Response 객체를 이용한 메시지 교환
- Repository Pattern을 적용한 파일 기반 데이터 접근
- Swing Timer 기반 주기적 데이터 갱신
- `Graphics2D` 기반 객실 현황 MapPanel

## 개인 담당 — 노지원
- `MapPanel` 직접 구현: `paintComponent()`와 `Graphics2D`를 이용해 호텔 평면도와 객실 상태를 시각화
- 로그인/인증 및 권한별 화면 분기
- 객실/요금 관리 일부 기능
- 객실·요금·사용자 프로필 관리 기능
- 식음료 주문 및 결제 내역 관련 기능

> 팀 전체 기능과 개인 담당 범위를 구분해 작성했습니다.

## 기술 스택
Java · Java Swing · Graphics2D · TCP/IP · Java Socket · Multi-threading · File I/O · Repository Pattern · Maven · NetBeans

## 시스템 구조

### Client-Server 통신
![Client-Server Socket 구조](images/client-server-socket.png)

UI의 요청을 `ServerProxy`가 전달하고 Socket/ObjectStream을 통해 서버의 `ClientHandler`와 Service 계층에서 처리합니다.

### Repository Pattern
![Repository Pattern 구조](images/repository-pattern.png)

`RoomService -> RoomRepository(interface) -> FileRoomRepository` 구조로 비즈니스 로직과 파일 저장 구현을 분리했습니다.

## 실행 화면

### 객실 현황 MapPanel
![MapPanel](images/map-panel.png)

`MapPanel`은 개인 구현 영역으로, Java Swing의 `paintComponent()`와 `Graphics2D`를 활용해 호텔 평면도와 객실 상태를 직접 렌더링했습니다.

## 주요 패키지
```text
src/main/java/cse/oop2/hotelreservation/
├── client/   # Swing UI, ServerProxy, ClientMain
├── server/   # ServerMain, Service, Repository, ClientHandler
└── common/   # Request, Response, Command, Session, Room 등 공통 객체
```

## 주요 기능
- 로그인 / 회원가입
- 객실 조회 / 예약 / 체크인
- 객실 상태 및 요금 관리
- 음식 주문
- 고객 문의 및 채팅
- 관리자 객실 / 회원 / 주문 관리
- 관리자 리포트
- 파일 기반 데이터 저장

## 실행 방법
프로젝트는 Client와 Server를 각각 실행합니다.

1. JDK와 Maven을 설치합니다. 원본 프로젝트 설정은 Java 24를 기준으로 합니다.
2. 프로젝트 루트에서 `mvn clean compile`로 컴파일합니다.
3. `cse.oop2.hotelreservation.server.ServerMain`을 먼저 실행합니다.
4. `cse.oop2.hotelreservation.client.ClientMain`을 실행합니다.

기본 서버 주소는 `localhost`, 포트는 `5000`입니다.

## 데이터 파일
프로젝트는 학습용 파일 기반 저장 구조를 사용합니다. 공개 저장소에는 예시 데이터만 포함하고, 기존 실행 과정에서 생성된 사용자·예약·채팅 데이터는 개인정보 및 평문 비밀번호 노출을 피하기 위해 제외했습니다.

## 참고
이 저장소는 대학 Java 팀 프로젝트 결과물을 포트폴리오용으로 정리한 버전입니다. 빌드 결과물(`target/`)과 기존 Git 메타데이터(`.git/`)는 포함하지 않았습니다.
