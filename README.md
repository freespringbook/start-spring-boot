# 9. Spring MVC와 Web Security의 통합
## 1. 프로젝트 구성
- Security
- DevTools
- Lombok
- JPA
- MySQL
- Thymeleaf
- Web

### 추가 라이브러리
#### Thymeleaf-layout
```xml
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
</dependency>
```

#### Querydsl
```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
</dependency>
```

#### Thymeleaf-security
```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity5</artifactId>
</dependency>
```

### Querydsl을 활용하기 위한 <plugin> 추가
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>com.mysema.maven</groupId>
            <artifactId>apt-maven-plugin</artifactId>
            <version>1.1.3</version>
            <executions>
                <execution>
                    <goals>
                        <goal>process</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>target/generated-sources/java</outputDirectory>
                        <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 기존 프로젝트 통합
#### 1. application.properties
```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/jpa_ex?useSSL=false
spring.datasource.username=jpa_user
spring.datasource.password=jpa_user

# 스키마 생성(create)
#spring.jpa.hibernate.ddl-auto=create
spring.jpa.hibernate.ddl-auto=update
# DDL 생성 시 데이터베이스 고유의 기능을 사용하는가?
spring.jpa.generate-ddl=true
# 실행되는 SQL문을 보여줄 것인가?
spring.jpa.show-sql=true
# 데이터베이스는 무엇을 사용하는가?
spring.jpa.database=mysql
# 로그 레벨
logging.level.org.hibernate=info
# SQL 파라메터 값까지 보기
#logging.level.org.hibernate.type.descriptor.sql=trace
# MySQL 상세 지정
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect

# Thymeleaf 캐싱 사용안함
spring.thymeleaf.cache=false
logging.level.org.springframework.web=debug
logging.level.org.springframework.security=debug
logging.level.me.freelife=debug
```

#### 2. 패키지 복사
#### 3. resources의 static, templates 폴더 복사
#### 4. maven compile 수행하여 Querydsl QWebBoard 생성
#### 5. 프로젝트 실행
security 기본 user 계정으로 로그인 '/board/list' 화면 출력확인

### 시큐리티 설정
#### domain 패키지
Member, MemberRole 추가

#### persistence 패키지
MemberRepository 추가

#### controller 패키지
LoginController, MemberController 추가

#### security 패키지 복사

## 2. 게시물 작성 부분
로그인한 사용자만 특정 URI에 접근이 가능하도록 설정

- 아래의 시큐리티 설정 추가
'/boards/list' 모든 사용자가 접근
'/boards/register' 로그인한 사용자만 접근

login.html 파일을 추가

로그인 하지 않은 사용자가 로그인한 사용자만 접근 가능한 페이지에 접근시 로그인 요청

### 게시물 작성 시 사용자 아이디 편집
로그인한 사용자 아이디를 작성자의 입력란에 자동으로 처리

Thymeleaf-security 이용해서 처리

스프링 시큐리티 적용시 GET 방식을 제외한 POST 등의 방식으로 전송할 경우
CSRF 토큰 값이 반드시 필요

`<form>` 태그에 Thymeleaf의 속성을 추가하면 자동으로 'CSRF' 필터가 적용되어 있음
때문에 직접 CSRF 값을 지정할 필요없이 `<form>` 데이터를 전송할 수 있음

```html
<form action="register" method="post"><input type="hidden" name="_csrf" value="10a0f3b7-cd1d-44fc-adc8-91a05ae9fba1">
<input type="hidden" name="_csrf" value="10a0f3b7-cd1d-44fc-adc8-91a05ae9fba1">
```

## 3. 게시물 조회
- 현재 게시물의 작성자만이 수정/삭제가 가능하도록 제어
- 게시물의 댓글 처리 시에 대한 제어

### 게시물 수정/삭제 버튼의 제어
1. 화면상에서 버튼 자체를 안 보이도록 처리하는 방식
2. 버튼은 보이고, JavaScript를 이용해서 로그인을 유도하는 방식이 일반적

## 4. 게시물의 수정/삭제
게시물 수정/삭제가 로그인한 사용자들만 가능하도록  
SecurityConfig에 추가 하거나 WebBoardController에 @Secure를 이용해서 처리

## 5. Ajax의 시큐리티 처리
댓글은 Ajax로 처리되기 때문에 별도로 처리가 필요한데  
Ajax로 호출하는 작업에 CSRF 값이 같이 전송 되어야 함

### 댓글 추가
화면상에 댓글을 추가하는 버튼을 누르면 로그인을 하도록 유도하기 위해 JavaScript를 이용

가장 중요한 부분은 Ajax 전송 시 'X-CSRF-TOKEN'헤더를 지정해 주는 것  
csrf 객체에서 headerName과 token 값을 이용해서 HTTP 헤더 정보를 구성

#### illegalStateException 에러 대비
```
java.lang.illegalStateException: Cannot create a session after the response has been committed
```
시큐리티 설정이 필요한 페이지에서 사용할 때에는 서버에서 illegalStateException 메시지가 출력되는 경우가 있음

이러한 메시지가 출력되는 이유는 CSRF 토큰이 만들어지기 전에 사용하면서 발생  
이를 해결하는 가장 간단한 방법은 페이지에 `<form th:action="${'/login'}"></form>`과 같이 의미 없는 `<form>`태그를 추가하는 것

Thymeleaf의 th:action을 처리하기 위해서는 반드시 CRSF 값을 생성해 내기 때문에 이와 같은 상황에서 유용

### 댓글 수정/삭제
댓글의 수정과 삭제는 현재 로그인한 사용자가 작성한 댓글만 가능하므로  
화면에서 버튼을 제어할 필요가 있음

댓글 추가와 동일하게 csrf 객체 전달 하도록 수정

## 6. 기타 설정 - 로그인 후 페이지 이동
로그인이 필요하지 않은 페이지에서 특정한 작업을 하기 위해 로그인을 하는 경우의 처리

로그인을 호출하는 페이지의 정보를 저장했다가 로그인이 성공한 후에 특정한 URL로 이동하도록 처리할 필요가 있음  
예제를 위해 스프링 MVC의 인터셉터와 Spirng Security의 AuthenticatioSuccessHandler를 이용

1. '/login' 경로를 호출할 때 특정한 파라미터(dest)를 추가하고, 파라미터의 값으로 로그인 후에 이동할 경로를 지정
2. '/login'의 호출을 감지하는 인터셉터를 추가해서 호출 시 파라미터가 존재하는지 확인하고, 만일 존재한다면 HttpSession에 값을 추가해둠
3. 로그인이 성공한 후에 HttpSession에 특정한 값이 보관되어 있었다면 이를 이용해서 redirect 함

### 인터셉터 추가
인터셉터는 컨트롤러의 호출을 사전 혹은 사후에 가로챌 수 있고, 컨트롤러가 HttpServletRequest나 HttpServletResponse를  
이용하지 않는 데 비해 인터셉터는 서블릿 관련 자원들을 그대로 활용할 수 있다는 장점이 있음

### 인터셉터 설정
스프링 부터 프로젝트에 MVC 관련된 설정을 하기 위해서는 WebMvcConfigurerAdapter라는 추상 클래스를 이용했었음  
하지만 스프링 부터 1.4이후에 deprecated 되었기 때문에 과거와는 조금 다른 방식으로 구현하게 됨

### AuthenticationSuccessHandler 추가
시큐리티 로그인 처리후 이동을 제어  
AuthenticationSuccessHandler 인터페이스를 구현하면 로그인 처리 후 원하는 동작을 제어할 수 있음