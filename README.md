# DevLinker Backend

DevLinker는 개발자 팀 모집부터 지원 승인, 팀 생성, 협업, 회고까지 하나의 흐름으로 연결한 팀 프로젝트 플랫폼입니다.

DevLinker 프로젝트에서 구현한 백엔드 도메인 로직, REST API, 인증·인가, 예외 처리, 성능 개선 및 테스트 내용을 정리했습니다.

## Service Flow

```text
모집글 작성
→ 지원 신청
→ 승인 / 거절
→ 팀 생성
→ 팀 협업
→ 회고
```

지원이 승인되면 지원 상태만 변경하는 것이 아니라, Team과 TeamMember가 생성되고 이후 공지·일정·문서·회고 기능이 팀 단위로 연결됩니다.

## Main Features

- 회원가입 및 로그인
- JWT 기반 인증
- 모집글 작성, 조회, 수정, 삭제
- 모집글 검색 및 페이징 조회
- 프로젝트 지원 신청, 승인, 거절
- 지원 승인 시 팀 생성 및 팀원 등록
- 팀 공지, 일정, 문서, 회고 API
- 공통 API 응답 형식
- 전역 예외 처리
- Swagger 기반 API 검증
- 지원 승인 서비스 로직 단위 테스트

## Architecture

```text
Client
  │
  ├─ Frontend: AWS S3 / CloudFront
  │
  └─ Backend: AWS EC2 / Spring Boot
                     │
                     └─ AWS RDS / MySQL
```

프론트엔드는 S3와 CloudFront를 통해 제공하고, 백엔드는 EC2에서 Spring Boot 애플리케이션을 실행하며, 데이터는 RDS MySQL에 저장하는 구조로 검증했습니다.

## Core Domain Design

지원 승인은 DevLinker의 핵심 도메인 로직입니다.

```text
Application 조회
→ 모집글 작성자 권한 검증
→ 지원 상태 검증
→ 승인 처리
→ Team 조회 또는 생성
→ OWNER / MEMBER 등록
→ 승인 결과 반환
```

주요 검증 조건은 다음과 같습니다.

- 현재 사용자가 모집글 작성자인지 확인
- 지원 상태가 `APPLIED`인지 확인
- 이미 승인 또는 거절된 지원의 재처리 차단
- 모집글에 연결된 Team 조회 또는 생성
- 모집글 작성자를 `OWNER`로 등록
- 승인된 지원자를 `MEMBER`로 등록
- 동일 팀의 중복 팀원 등록 방지

## Backend Structure

```text
Controller
→ Service
→ Repository
→ Entity
→ Database
```

- `Controller`: HTTP 요청과 응답 처리
- `Service`: 도메인 규칙, 권한 및 상태 검증
- `Repository`: Spring Data JPA 기반 데이터 접근
- `Entity`: 도메인 상태와 변경 행위 관리
- `DTO`: API 요청과 응답 데이터 분리

## Authentication and Authorization

로그인에 성공하면 Access Token과 Refresh Token을 발급합니다.

보호된 API 요청에서는 `JwtAuthenticationFilter`가 `Authorization` 헤더의 Bearer Token을 추출하고, `JwtTokenProvider`가 토큰의 유효성을 검증합니다. 검증된 인증 정보는 `SecurityContextHolder`에 저장됩니다.

JWT는 요청 사용자를 식별하는 역할을 담당하며, 모집글 작성자 또는 팀원 여부와 같은 실제 도메인 권한은 Service 계층에서 검증합니다.

## Common Response and Exception Handling

API 응답은 `ApiResponse`를 사용하여 다음 구조로 통일했습니다.

```text
success
code
message
data
```

`GlobalExceptionHandler`에서는 존재하지 않는 데이터, 잘못된 요청, 권한 부족 등의 예외를 HTTP 상태 코드와 공통 실패 응답으로 변환합니다.

이를 통해 Controller마다 예외 응답을 반복해서 작성하지 않고, 일관된 형식으로 성공 및 실패 결과를 반환하도록 구성했습니다.

## Troubleshooting

### 1. 지원 승인 로직의 상태 및 권한 검증

지원 승인 기능은 단순히 Application 상태만 변경하는 기능이 아니라, 모집글 작성자 권한, 지원 상태, 팀 생성, 팀원 등록까지 함께 보장해야 하는 도메인 흐름이었습니다.

승인 요청을 처리할 때 다음과 같은 비정상 흐름을 방지해야 했습니다.

- 작성자가 아닌 사용자의 승인 시도
- 이미 승인 또는 거절된 지원의 재처리
- 승인 후 Team 또는 TeamMember 등록 누락
- 동일 팀의 중복 팀원 등록

이를 해결하기 위해 `approveApplication()`에서 작성자 권한 검증, 지원 상태 검증, Team 조회 또는 생성, OWNER와 MEMBER 등록, 승인 처리를 하나의 Service 흐름으로 구성했습니다.

그 결과 권한이 없거나 이미 처리된 지원은 차단하고, 지원 승인이 실제 팀 구성으로 이어지도록 도메인 흐름을 정리했습니다.

### 2. 모집글 목록 조회 성능 개선

#### Problem

기존 모집글 목록 조회는 전체 데이터를 조회한 뒤 응답 DTO로 변환하는 방식이었습니다.

데이터가 증가할수록 조회 범위와 DTO 변환 비용이 함께 커질 수 있어, 목록 API의 조회 범위를 제한할 필요가 있었습니다.

#### Measurement

- 테스트 환경: 로컬 MySQL
- 테스트 데이터: 모집글 2,007건
- 측정 방식: 워밍업 후 동일 API 10회 호출 평균
- 측정 도구: PowerShell `curl`

#### Improvement

- 전체 조회 방식에서 Pageable 기반 페이징 조회로 변경
- 요청한 페이지 범위의 데이터만 조회
- `id DESC` 기준 최신순 정렬 적용

#### Result

```text
개선 전 평균 응답시간: 46.1ms
개선 후 평균 응답시간: 21.3ms
응답시간 감소율: 약 54%
```

## Test

지원 승인 서비스 로직의 정상 흐름과 예외 흐름을 단위 테스트로 검증했습니다.

### Test Cases

- 정상 승인 시 `APPLIED`에서 `APPROVED`로 상태 변경
- 승인 시 OWNER와 MEMBER 등록
- 모집글 작성자가 아닌 사용자의 승인 요청 차단
- 이미 승인 또는 거절된 지원의 재처리 차단

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- Gradle

### Database

- MySQL
- AWS RDS

### Test and Documentation

- JUnit 5
- Mockito
- Swagger

### Infrastructure

- AWS EC2
- AWS S3
- AWS CloudFront
- Git / GitHub

## Environment Variables

애플리케이션 실행 전 다음 환경변수 설정이 필요합니다.

| Name | Description |
| --- | --- |
| `DB_URL` | MySQL 데이터베이스 접속 URL |
| `DB_USERNAME` | 데이터베이스 사용자명 |
| `DB_PASSWORD` | 데이터베이스 비밀번호 |
| `JWT_SECRET` | JWT 서명 키 |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access Token 만료 시간 |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh Token 만료 시간 |
| `SERVER_PORT` | 서버 실행 포트 |

실제 비밀번호, JWT 서명 키, 배포 서버 주소 등의 민감정보는 저장소에 포함하지 않습니다.

## Run

### Windows

```bash
gradlew.bat bootRun
```

### Linux / macOS

```bash
./gradlew bootRun
```

## Run Tests

### Windows

```bash
gradlew.bat test
```

### Linux / macOS

```bash
./gradlew test
```

## Responsibility

- 본인: 백엔드 도메인 설계, REST API, JWT 인증, 예외 처리, 성능 개선, 테스트, AWS 배포 검증
- 팀원: 프론트엔드 화면 구현 및 백엔드 API 연동

