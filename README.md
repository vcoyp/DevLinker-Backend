# DevLinker Backend

DevLinker는 개발자 팀 모집, 지원 승인, 팀 생성, 협업 기능을 제공하는 플랫폼입니다.

## Overview

```text
모집글 작성 → 지원 신청 → 승인 / 거절 → 팀 생성 → 팀 협업 → 회고
```

## Features

- 회원가입 / 로그인
- JWT 기반 인증
- 모집글 CRUD
- 지원 신청, 승인, 거절
- 승인 시 팀 생성 및 팀원 등록
- 팀 공지, 일정, 문서, 회고 API
- 공통 응답 형식 및 예외 처리
- 모집글 목록 조회 성능 개선
- 지원 승인 로직 단위 테스트

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- Gradle
- JUnit / Mockito
- AWS EC2 / RDS / S3 / CloudFront
- Swagger

## Backend Scope

이 레포지토리는 DevLinker 프로젝트의 백엔드 구현 내용을 정리한 저장소입니다.

주요 구현 범위는 도메인 로직, REST API, JWT 인증, 예외 처리, 배포 검증, 성능 개선, 테스트 코드입니다.

## Environment

실행 시 아래 환경변수 설정이 필요합니다.

- DB_URL
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET
- JWT_ACCESS_TOKEN_EXPIRATION
- JWT_REFRESH_TOKEN_EXPIRATION
- SERVER_PORT

## Run

```bash
./gradlew bootRun
```

## Test

```bash
./gradlew test
```
