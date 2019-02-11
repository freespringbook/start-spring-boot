# 7. REST 방식의 댓글 처리와 JPA 처리
- 게시물의 리스트 화면에서 댓글의 개수를 볼 수 있다
- 게시물의 조회 화면에서 댓글 목록을 출력한다
- 댓글과 관련된 모든 기능이 특정한 게시물의 조회 화면에서 이루어진다

## 1. 프로젝트의 구성과 @RestController
### REST 방식과 @RestController
HTTP 프로토콜에서 데이터를 전송하는 방식
- **GET 방식 데이터를 보여주거나 다른 사람들에게 알리는 방식**
  - 기본적으로는 정보의 확산을 목적으로 설계됨
  - 인터넷상의 URL은 하나의 고유한 데이터를 찾는 이름이나 태그가 됨
- **POST 방식 데이터를 이용해서 특별한 작업을 처리하는 방식**
  - 정보의 가공이 목적
  - 정확한 목적을 가지고 특정한 작업을 수행하기 위해 사용

**REST** 방식이란, **GET/POST** 방식을 이용하던 시대를 지나오면서 작업을 할 때 좀 더  
범용적인 규칙을 갖고자 하는 움직임

전송 방식과 역할
- `GET`: 특정 리소스를 조회(read)하는 용도로 사용 
  - ex) /products/123
- `POST`: 특정 리소스를 생성(create)하는 용도로 사용
  - ex) /products/ 혹은 /member/123
- `PUT`: 특정 리소스를 수정
- `DELETE`: 특정 리소스를 삭제

Spring MVC에서 REST 방식의 설계와 사용을 위해서 제공하는 어노테이션
- `@RequestBody`: 클라이언트가 보내는 **JSON** 데이터의 수집 및 가공
- `@ResponseBody`: 클라이언트에게 전송되는 데이터에 맞게 **MIME** 타입을 결정
- `@PathVariable`: URL의 경로에 포함된 정보 추출
- `@RestController`: 컨트롤러의 모든 메소드 리턴 타입으로 `@ResponseBody`를 기본으로 지정

## 2. JPA의 설계와 Repository의 설계/테스트
게시글과 댓글의 관계는 '일대다', '다대일' 이라고 볼 수 있음  
'양방향'으로 설계하는 방식을 이용
- WebReply 댓글 엔티티 클래스 작성

### 연관관계의 설정
**WebReply**에 **WebBoard**를 `@ManyToOne` 관계로 설정
```java
@JsonIgnore
@ManyToOne(fetch = FetchType.LAZY)
private WebBoard board;
```

**WebBoard**에는 `@OneToMany` 관계를 설정  
불필요하게 양쪽 테이블을 조회 하지 않도록 양쪽 모두 '지연 로딩' 방식 설정
```java
@JsonIgnore
@OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
private List<WebReply> replies;
```