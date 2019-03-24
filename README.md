# 8. Spring Web Security
- 사용자의 권한에 따른 URI 접근 제어
- 데이터베이스와 연동하는 로그인 처리
- 쿠키를 이용한 자동 로그인(rememeber-me)
- 패스워드 암호화

> 인증(Authentication): 인증 절차를 거쳐 증명하는 것
> 인가(Authorization): 권한 부여나 허가와 같은 의미

## 1. 예제 프로젝트 생성
사용자와 사용자의 권한을 관리한ㄴ 기능과 해당 기능을 이용해서 스프링 시큐리티를 적용하는 부분으로 구성

1. 프로젝트 생성
   - Security
   - DevTools
   - Lombok
   - JPA
   - MyBatis
   - MySQL
   - Thymeleaf
   - Web

2. `application.properties` 수정

### 시큐리티의 기본 설정 추가하기
1. SecurityConfig 클래스 추가
2. 빈 등록을 위해 `@EnableWebSecurity` 어노테이션 추가
3. 설정을 담당하는 `WebSecurityConfigurerAdapter` 클래스 상속
4. `configure(HttpSecurity http)` 메서드 오버라이드

### 샘플 URI 생성
1. SampleController 를 생성
2. templates 파일들 생성
  - guest.html
  - manager.html
  - admin.html
  - index.html

## 2. 회원과 권한 설계
Spring Data JPA를 이용해서 서비스를 이용하는 회원과 각 회원이 가지는 권한을 생성

회원에 대한 용어는 Member를 이용
회원(Member)은 등급이나 권한을 가지도록 설계
스프링 시큐리티에서 User라는 용어를 사용함

프로젝트에서 작성하는 수준은 특정한 회원(Member)이 특정한 권한(MemberRole)을 가진다고 가정하고
특정 URL에 대해서 이를 체크하도록 함

### 도메인 클래스 설계
Member 와 MemberRole 클래스를 생성
- Member: uid, upw, uname
- MemberRole: fno, roleName

속성을 가지도록 설계
스프링 시큐리티에서 username, password 등의 용어를 사용

#### 연관관계의 설정
- Member와 MemberRole은 '일대다', '다대일'의 관계
- MemberRole 자체가 단독으로 생성되는 경우는 거의 없으므로, Member가 MemberRole을 관리하는 방식의 설계

### Repository 생성
MemberRepository 생성

### 테스트를 통한 데이터 추가/조회
1. 테스트 클래스인 MemberTests 클래스 생성
2. 데이터 추가 코드
3. testInsert() 100명의 사용자 생성
4. 사용자 중 user0 부터 user80까지는 'BASIC'이라는 권한
   user90까지는 'MANAGER' 나머지 10명은 'ADMIN' 권한을 가지도록 설계

> Member 엔티티와 MemberRole 엔티티를 동시에 저장해서 에러가 발생  
> 이에 대한 처리로 cascade 설정을 추가

5. 회원 데이터와 함께 권한들에 대한 정보를 같이 조회할 수 있도록 조회 작업 진행

> tbl_members와 tbl_member_roles 테이블을 둘 다 조회해야 하기 때문에  
> 트랜잭션 처리를 해주거나 즉시 로딩을 이용해서 조인을 하는 방식으로 처리
> fetch 모드를 즉시 로딩으로 설정

## 3. 단순 시큐리티 적용
https://spring.io/guides/topicals/spring-security-architecture
웹에서 스프링 시큐리티는 기본적으로 필터 기반으로 동작
내부에는 상당히 많은 종류의 필터들이 이미 존재하므로 개발 시 필터들의 설정을 조정하는 방식을 주로 사용

### 로그인/로그아웃 관련 처리
#### 특정 권한을 가진 사람만이 특정 URI에 접근하기
SecurityConfig 클래스에는 configure() 메소드를 이용해서 웹 자원에 대한 보안을 확인  
HttpSecurity는 웹과 관련된 다양한 보안설정을 걸어줄 수 있음

특정한 경로에 특정한 권한을 가진 사용자만 접근할 수 있도록 설정
- authorizeRequests(): 시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미
- antMatches(): 특정한 경로를 지정
  - permitAll(): 모든 사용자가 접근할 수 있음
  - hasRole(): 시스템상에서 특정 권한을 가진 사람만이 접근할 수 있음

'/guest', '/manager'로 접근 테스트 '/manager'은 Access Denied' 메세지 출력