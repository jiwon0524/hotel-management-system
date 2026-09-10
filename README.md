# Hotel Management System (HMS)

Java Swing 기반 호텔 통합 관리 시스템입니다. 4인 팀 프로젝트로 진행했으며, 예약·체크인·객실 관리·식음료 주문·문의/채팅·관리자 기능을 하나의 GUI 프로그램으로 구현했습니다.

## 핵심 구현
- Java Swing 기반 데스크톱 GUI
- Java Socket + ObjectStream 기반 Client-Server 통신
- 클라이언트 요청별 `ClientHandler` 스레드 처리
- Request/Response 객체를 이용한 메시지 교환
- Repository Pattern을 적용한 파일 기반 데이터 접근
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
UI 요청을 `ServerProxy`가 전달하고 Socket/ObjectStream을 통해 서버의 `ClientHandler`와 Service 계층에서 처리합니다.

### Repository Pattern
`RoomService -> RoomRepository(interface) -> FileRoomRepository` 구조로 비즈니스 로직과 파일 저장 구현을 분리했습니다.

### 객실 현황 MapPanel
`MapPanel`은 개인 구현 영역으로, Java Swing의 `paintComponent()`와 `Graphics2D`를 활용해 호텔 평면도와 객실 상태를 직접 렌더링했습니다.

## 공개 코드 구성
```text
src/main/java/cse/oop2/hotelreservation/
├── client/   # MapPanel, ServerProxy 등 핵심 UI/통신 코드
├── server/   # ServerMain, Service, Repository, ClientHandler
└── common/   # Request, Response, Command, Session, Room 등 공통 객체
```

이 저장소는 취업 포트폴리오 검토를 위해 핵심 구현과 아키텍처가 드러나는 코드를 선별해 공개한 버전입니다. 전체 팀 산출물과 실행 중 생성되는 사용자·예약·채팅 데이터는 포함하지 않았습니다.

## 주요 기능
- 로그인 / 회원가입
- 객실 조회 / 예약 / 체크인
- 객실 상태 및 요금 관리
- 음식 주문
- 고객 문의 및 채팅
- 관리자 객실 / 회원 / 주문 관리
- 관리자 리포트
- 파일 기반 데이터 저장

## 개발 환경
- Maven compiler release: Java 21
- Server port: `5000`
- 원본 팀 프로젝트는 NetBeans 기반으로 개발

## 데이터 및 보안 관련 주의사항
프로젝트는 학습 목적의 파일 기반 저장 구조를 사용했습니다. 공개 저장소에는 예시 데이터만 포함하며 실제 사용자 정보와 예약·채팅 기록은 제외했습니다.

현재 `FileUserRepository`는 교육용 구현으로 비밀번호를 파일에 평문 저장하는 한계가 있습니다. 실서비스라면 비밀번호 해시 처리, DB 적용, 입력 검증, 동시성 제어가 필요합니다.

## 개선 방향
- 파일 저장 구조를 RDBMS 기반 Repository 구현으로 교체
- 비밀번호 해시 및 인증 보안 강화
- Socket 예외 처리와 연결 관리 개선
- 테스트 코드 추가
- UI와 비즈니스 로직의 결합도 추가 감소

## 참고
이 저장소는 대학 Java 팀 프로젝트 결과물을 포트폴리오용으로 정리한 버전입니다. 빌드 결과물(`target/`)과 기존 Git 메타데이터(`.git/`)는 포함하지 않았습니다.
