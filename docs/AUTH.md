# Authentication & Authorization Flow

## Overview

DevLinker는 JWT 기반 인증 방식을 사용합니다.

인증 단계에서는 요청한 사용자가 누구인지 식별하고, 권한 검증 단계에서는 해당 사용자가 요청한 기능을 수행할 수 있는지 확인합니다.

사용자 인증은 Spring Security와 JWT가 담당하고, 모집글 작성자 및 팀원 여부와 같은 도메인 권한은 Service 계층에서 검증하도록 책임을 분리했습니다.

## Login & Token Issuance Flow

```text
로그인 요청
→ 사용자 정보 조회
→ 비밀번호 검증
→ Access Token 생성
→ Refresh Token 생성
→ 토큰 반환
```

로그인 요청이 들어오면 사용자 정보와 비밀번호를 검증한 뒤 `JwtTokenProvider`를 통해 Access Token과 Refresh Token을 발급합니다.

- `Access Token`: 보호된 API 요청에서 사용자 인증에 사용
- `Refresh Token`: Access Token 재발급을 위한 토큰으로 사용

인증 정보를 서버 세션에 저장하지 않고 토큰에 담아 전달하는 방식으로 구성했습니다.

## Token Verification Flow

```text
Authorization: Bearer {Access Token}
→ JwtAuthenticationFilter
→ Bearer Token 추출
→ JwtTokenProvider 토큰 검증
→ Authentication 생성
→ SecurityContextHolder 저장
→ Controller 요청 전달
```

보호된 API 요청에서는 `JwtAuthenticationFilter`가 `Authorization` 헤더에서 Bearer Token을 추출합니다.

`JwtTokenProvider`는 토큰의 서명과 만료 여부를 검증합니다. 유효한 토큰이면 사용자 인증 정보를 생성하여 `SecurityContextHolder`에 저장하고, 이후 Controller와 Service에서 현재 사용자를 식별할 수 있도록 구성했습니다.

토큰이 없거나 유효하지 않은 경우에는 인증 정보가 생성되지 않으며, 보호된 기능에 대한 요청이 차단됩니다.

## Domain Authorization

JWT 인증에 성공했다는 것은 사용자가 식별되었다는 의미이며, 모든 기능을 수행할 권한이 있다는 의미는 아닙니다.

실제 도메인 권한은 각 Service에서 검증합니다.

```text
JWT 인증
→ 현재 사용자 식별
→ 도메인 데이터 조회
→ 작성자 또는 팀원 여부 검증
→ 비즈니스 로직 실행
```

대표적인 권한 검증은 다음과 같습니다.

- 모집글 작성자만 지원 승인 및 거절 가능
- 팀 협업 기능 요청 시 팀원 여부 확인
- 데이터 변경 요청 시 현재 사용자와 도메인 소유자 비교
- 이미 승인 또는 거절된 지원의 재처리 차단

이를 통해 인증된 사용자가 자신의 권한 범위를 벗어난 기능을 실행하지 못하도록 구성했습니다.

## Failure Flow

```text
유효하지 않은 토큰
→ 인증 단계에서 요청 차단

유효한 토큰 + 권한 없는 사용자
→ Service 계층에서 요청 차단

유효한 토큰 + 권한 있는 사용자
→ 비즈니스 로직 실행
```

인증 실패와 권한 검증 실패를 분리하여 처리함으로써, 사용자 식별 문제와 도메인 규칙 위반을 서로 다른 책임으로 관리했습니다.

## Responsibility Separation

| Layer | Responsibility |
| --- | --- |
| `JwtAuthenticationFilter` | 요청 헤더에서 JWT 추출 |
| `JwtTokenProvider` | 토큰 생성 및 유효성 검증 |
| `SecurityContextHolder` | 현재 요청의 인증 정보 보관 |
| `Service` | 작성자, 팀원, 지원 상태 등 도메인 권한 검증 |
| `GlobalExceptionHandler` | 예외를 공통 실패 응답으로 변환 |

```text
Spring Security / JWT
→ 사용자가 누구인지 인증

Service Layer
→ 해당 사용자가 기능을 수행할 권한이 있는지 검증
```

인증과 도메인 권한 검증을 분리하여 보안 관련 책임을 명확히 하고, 권한 규칙이 실제 비즈니스 로직과 함께 관리되도록 설계했습니다.
