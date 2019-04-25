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

#### 로그인 페이지 보여주기
'/login'으로 접근해 권한을 인가 받도록 configure() 수정

`formLogin()`: form 태그 기반의 로그인을 지원하겠다는 설정

스프링 시큐리티에서 기본 로그인 화면 제공

#### 로그인 정보 설정하기
로그인 처리를 위해 SecurityConfig에 AuthenticationManagerBuilder를 주입해서 인증 처리

#### 로그인 관련 정보 삭제하기
개발자 도구 -> Application 탭 -> Cookies 쿠키 확인  
Cookies 메뉴에서 Clear로 브라우저 종료

#### 커스텀 로그인 페이지 만들기
1. `formLogin()` 이후 `loginPage()` 메소드를 이용해서 URI 지정
2. LoginController 클래스 생성
3. templates 폴더 내에 login.html 작성
   스프링 시큐리티는 기본적으로 username과 password라는 이름을 이용
   `<input>`태그의 name 속성값을 변경할 수 없음
   action 속성을 지정하지 않았으므로 버튼 클릭시 '/login'으로 이동 POST 방식으로 데이터 전송

`ㅡcsrf`: `<form>`태그의 내부에 hidden 속성으로 작성된 속성
  - 사이트 간 요청 위조(Cross-site request forgery, CSRF, XSRF)를 방지하기 위한 것
  - 요청을 보내는 URL에서 서버가 가진 동일한 값과 같은 값을 가지고 데이터를 전송할 때에만 신뢰하기 위한 방법

실제로 모든 작업은 여러 종류의 Filter들과 Interceptor를 통해서 동작  
개발자 입장에서는 적절한 처리를 담당하는 핸들러(Handler)들을 추가하는 것만으로 모든 처리가 완료됨

스프링 시큐리티가 적용되면 **POST 방식으로 보내는 모든 데이터는 CSRF 토큰 값이 필요**해짐
> CSRF 토큰을 사용하지 않으려면 `application.properties`에 `security.enable-csrf` 속성을 이용해서  
> CSRF 토큰을 사용하지 않도록 설정 해야 함

#### 접근 권한 없음 페이지 처리
'/admin' 경로로 접근하면 브라우저는 자동으로 '/login' 경로로 이동함

HttpSecurity에서 exceptionHandling()을 이용해서 권한이 없을 경우  
알려주고 로그인 화면으로 이동할 수 있도록 안내 페이지 작성

`exceptionHandling()` 이후에 메소드는 `accessDeniedPage()`나 `accessDeniedHandler()`를 이용하는 것이 일반적

'/accessDenied'라는 URI가 처리할 것이므로 LoginController에 메소드 작성

templates에 accessDenied.html 작성

#### 로그아웃 처리
HttpSession의 정보를 무효화시키고 필요한 경우에는 모든 쿠키를 삭제

`logout()` 뒤에는 `invalidateHttpSession()` 과 `deleteCookie()`를 이용해서 처리

로그아웃을 특정한 페이지에서 진행하고 싶다면 먼저 로그아웃을 처리하는 URI를 처리 해야하고  
POST 방식으로 로그아웃을 시도

SecurityConfig의 configure()에서 로그아웃을 위한 URI를 지정

templates에는 logout.html 작성

## 4. 다양한 인증 방식
- 인증 매니저(Authentication Manager): 인증에 대한 실제적인 처리를 담당
  - UserDetails: '인증 매니저'는 결과적으로 인증과 관련된 모든 정보를 이 타입으로 반환
    사용자 계정과 같은 정보와 더불어 사용자가 어떤 권한들을 가지고 있는지를 Collection 타입으로 가지고 있음
  - UserDetailsService: 자신이 어떻게 관련 정보를 처리해아 하는지 판단
    인증되는 방식을 수정하고 싶다면 UserDetailsService라는 인터페이스를 구현하고, 인증 매니저에 연결

![](img/1.png)

### 스프링 시큐리티의 용어에 대한 이해
- AuthenticationManager(인증 매니저)
  - AuthenticationManagerBuilder(인증 매니저 빌더)
    - JDBC, LDAP
    - authenticate()
  - Authentication(인증)
- UserDetailsService 인터페이스
  - UserDetailsManager 인터페이스
  - loadUserByUsername()
- UserDetails 인터페이스
  - User 클래스

- 모든 인증은 인증 매니저(AuthenticationManager)를 통해서 이루어진다
  인증 매니저를 생성하기 위해 인증 매니저 빌더(AuthenticationManagerBuilder)라는 존재가 사용된다
- 인증 매니저를 이용해서 인증(Authentication)이라는 작업이 수행된다
- 인증 매니저들은 인증/인가를 위한 UserDetailsService를 통해서 필요한 정보들을 가져온다
- UserDetails는 사용자의 정보 + 권한 정보들의 묶음이다

### JDBC를 이용한 인증 처리
- JdbcUserDetailsManagerConfigurer: 데이터베이스를 연동하여 로그인/로그아웃 처리

스프링 시큐리티가 데이터베이스를 연동하는 방법
1. 직접 SQL등을 지정해서 처리하는 방법
  - DataSource 타입의 객체를 주입
  - 사용자에 대한 계정 정보와 권한을 체크하는 부분에는 DataSource를 이용하고 SQL을 지정
  - 사용자의 계정 정보를 이용해서 필요한 정보를 가져오는 SQL 필요
  - 해당 사용자의 권한을 확인하는 SQL 필요

2. 기존에 작성된 Repository나 서비스 객체들을 이용해서 별도로 시큐리티 관련 서비스를 개발하는 방법


##### userByUsernameQuery()
userByUsernameQuery()를 이용하는 경우 우선 username과 password, enabled라는 칼럼의 데이터가 필요  
- Enabled: 해당 계정이 사용 가능한지를 의미(만일 적절한 데이터가 없다면 무조건 true를 이용하도록 설정)

##### authoritiesByUsernameQuery()
authoritiesByUsernameQuery()의 파라미터로 사용되는 SQL은 실제 권한에 대한 정보를 가져오는 SQL  
이때 사용하는 SQL은 username 하나의 파라미터를 전달, username과 권한 정보를 처리하도록 작성

##### rolePrefix()
'/manager'라는 경로로 접근하려면 'ROLE_MANAGER'라는 이름의 권한이 필요함  
DB에는 'ROLE_라는 문자열은 없고 단순히 'BASIC, MANAGER, ADMIN'으로 되어있으므로  
rolePrefix()라는 메서드로 'ROLE_'라는 문자열을 붙임

### 사용자 정의(custom) UserDetailsService 작성하기
User 클래스는 UserDetails라는 인터페이스를 구현해서 보다 상세한 사용자 정보를 처리

모든 인증 매니저는 결과적으로 UserDetails 타입의 객체를 반환하도록 구현
개발자가 인증 매니저를 커스터마이징하려면 UserDetailsService 인터페이스를 구현하고
이를 HttpSecurity 객체가 사용할 수 있도록 지정하면 됨

API에서 UserDetailsService를 보면 이미 여러 클래스가 UserDetailsService를 구현해 두었고
앞의 예제들이 내부적으로 이를 활용한다는 것을 짐작할 수 있음

본인이 원하는 방식으로 인증을 처리하고 싶다면 가장 먼저 UserDetailsService 인터페이스를 구현하는 
새로운 클래스를 정의해야 함

본인의 UserService를 security 패키지 내에 생성하고 UserDetailsService 인터페이스를 구현하도록 작성

#### 간단한 커스텀 UserDetailsService 사용하기
User 클래스의 인스턴스는 Collection<Authority>를 가질 수 있으므로, Arrays.asList()를 이용해 여러 권한을 부여할 수 있음

SimpleGrantedAuthority 클래스는 GrantedAuthority라는 인터페이스의 구현체  
GrantedAuthority 인터페이스는 문자열을 반환하는 getAuthority() 메소드 하나만을 가지고 있음

'/manager'의 경로에 로그인 패스워드는 '1111'을 입력해서 처리

### MemberRepository와의 연동
FreelifeUserService에 MemberRepository 인스턴스를 주입해 줄 필요가 있음

MemberRepository의 findById()는 PK를 이용해서 Member 엔티티 인스턴스를 얻어오지만
UserDetails라는 리턴 타입에는 맞지 않음

Member 타입의 인스턴스를 UserDetails로 처리하려면
1. Member 클래스에 UserDetails 인터페이스를 구현해 주는 방법을 이용
2. Member 클래스가 이미 UserDetails 인터페이스를 구현한 User 클래스를 상속
3. 조합을 이용해서 Member를 포함하는 별도의 클래스를 만드는 방법을 사용

#### FreelifeSecurityUser 클래스의 생성
security 폴더에 FreelifeSecurityUser라는 클래스를 스프링 시큐리티의 User 클래스를 상속받는 형태로 생성

FreelifeUserService에서는 Member 타입의 인스턴스를 이용해서 FreelifeSecurityUser를 생성  
FreelifeSecurityUser는 Member를 이용하도록 수정

## 5. 화면에서 로그인한 사용자 정보 사용하기
스프링 시큐리티의 정보를 Thymeleaf에서 사용하기 위해서는 Spring Security Dialect라는 것을 이용해야 함
http://github.com/thymeleaf/thymeleaf-extras-springsecurity

Maven등을 이용할 때에 https://mvnrepository.com/artifact/org.thymeleaf.extras 를 이용해서 필요한 라이브러리를 추가

## Thymeleaf에서 스프링 시큐리티 처리하기
Thymeleaf로 작성된 페이지에서는 다음과 같은 순서로 스프링 시큐리티를 이용할 수 있다
1. 시큐리티 관련 네임스페이스 추가
2. 네임스페이스를 이용한 태그 작성

'authorize-url': 특정한 권한을 가진 사용자에게만 버튼이나 링크가 보이도록 하는 기능
```html
<p sec:authorize-url="/admin/aaa">
    <a href="/admin/aaa">ADMIN AAA</a>
</p>

<p sec:authorize-url="/manager/aaa">
    <a href="/manager/aaa">MANAGER AAA</a>
</p>
```

특정한 권한을 가진 사용자들에게만 보여주여야 할 내용이 있다면 'hasRole()' 표현식을 그대로 적용
```html
<h1 sec:authorize="hasRole('ROLE_ADMIN')">
    This content is only for administrators.
</h1>
```

실제 데이터베이스 상의 사용자 정보를 포함하는 경우에는 principal 이라는 속성을 이용  
로그인된 회원의 정보는 #authentication.principal 내부에 존재 하므로  
#authentication.principal.member와 같이 '.'를 이용해서 접근

표현식을 `th:width`를 지정해서 짧게 줄여서 사용 가능 

## 6. Remember-Me 인증하기
스프링 시큐리티의 Remember-me 기능은 기본적으로 사용자가 로그인했을 때의 특정한 토큰 데이터를  
2주간 유지되도록 쿠키를 생성함  
브라우저에 전송된 쿠키를 이용해서 로그인 정보가 필요하면 저장된 토큰을 이용해서 다시 정보를 사용함

### 로그인 화면에서 'remember-me' 체크박스 처리
login.html에 remember-me 체크박스 추가

### SecurityConfig에서의 설정
'remember-me'기능을 설정하기 위해서는 UserDetailsService를 이용해야 함  
HttpSecurity 인스턴스에 간단히 rememberMe()를 이용해주면 처리가 가능함

rememberMe()에서는 쿠키의 값으로 암호화된 값을 전달하므로 암호의 '키(key)'를 지정하여 사용함

로그인 화면에서 'remember-me'를 선택한 후 로그인하면 브라우저상에는 기본적인 JSESSIONID(톰캣의 경우에 생성된 세션의 키)  
의 쿠키 외에도 'remember-me'라는 이름의 쿠키가 생성됨

생성된 'remember-me' 쿠키의 Expires(유효기간)는 '로그인 시간 + 2주'  
쿠키는 유효기간이 설정되면 브라우저 내부에 저장됨  
브라우저는 보관된 'remember-me'쿠키를 그대로 가지고 서버에 접근하게 됨

### remember-me를 데이터베이스에 보관하기
스프링 시큐리티는 기본적으로 'remember-me' 기능을 사용하기 위해서 'Hash-based Token 저장 방식'과  
'Persistent Token 저장 방식'을 사용할 수 있음  

기본은 'Hash-based' 방식

'remember-me' 쿠키의 생성은 기본적으로 'username'과 쿠키의 만료시간, 패스워드를 Base-64 방식으로 인코딩한 것  
사용자가 패스워드를 변경하면 정상적인 값이라도 로그인이 될 수 없다는 단점이 있음

이를 해결하기 위해서 가능하면 데이터베이스를 이용해서 처리하는 방식을 권장  
`org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl` 클래스를 이용  
`PersistenceTokenRepository`라는 인터페이스를 이용해 원하는대로 구현 가능  

토큰을 보관할 수 있는 테이블 생성
```sql
create table persistent_logins (
    username varchar(64) not null,
    series varchar(64) primary key,
    token varchar(64) not null,
    last_used timestamp not null
);
```

설정 추가 후 'remember-me'를 선택한 로그인을 하면 'persistent_logins'테이블에  
생성된 토큰의 값이 기록되는 것을 확인할 수 있음

브라우저에서는 해당 토큰을 이용한 쿠키가 생성됨

이제 쿠키의 생성은 패스워드가 아니라 series에 있는 값을 기준으로 하게 됨

## 7. 기타 시큐리티 설정
### MVC에 어노테이션 처리하기
URI에 간단한 어노테이션만을 이용해서 접근을 제한
- @Secured: 문자열의 배열이나 문자열을 이용해서 권한을 지정

제대로 작동 시키기 위해서 SecurityConfig에 `@EnableGlobalMethodSecurity`라는 어노테이션을 추가해 주고
securedEnabled 속성의 값을 true로 지정

### PasswordEncoder 사용하기
스프링 시큐리티는 패스워드를 쉽게 암호화할 수 있는 PasswordEncoder라는 인터페이스를 기준으로 제공

암호화와 관련된 내용 참고
http://d2.naver.com/helloworld/318732

적용하는 방식 
1. 구현 클래스 작성
2. 시큐리티 설정 추가
3. 관련 컨트롤러나 서비스와 연동하는 방식으로 구현

### 컨트롤러를 이용한 회원가입 처리
암호화를 테스트하기 위해 MemberController를 하나 작성하고 실제로 회원가입을 처리할 수 있는 기본 구조 생성