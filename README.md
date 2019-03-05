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

#### @JsonIgnore 어노테이션
양방향의 경우 **JSON** 변환이 상호 호출 되므로 무한히 방복해서 생성하는 문제가 생길 수 있음  
이 때문에 객체를 **JSON** 형태로 만들 때 제외 시키기 위해 `@JsonIgnore` 어노테이션을 적용

최종적으로 **WebReply** 객체를 변환한 **JSON** 데이터에서는 **WebBoard**와 관련된 내용은 제외됨

### ReplyRepository 추가
**WebReply**는 단독으로 CRUD 작업이 가능하기 때문에 별도의 Repository를 생성

댓글의 내용은 특별히 검색을 처리 하지 않을 것이므로 **QuerydslPredicateExcuter**를 추가하지 않음
```java
public interface WebReplyRepository extends CrudRepository<WebReply, Long> {
}
```

### WebReplyRepository의 테스트
지정된 게시물의 번호를 이용해서 댓글을 10개씩 추가하는 테스트
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@Commit
public class WebReplyRepositoryTest {

    @Autowired
    WebReplyRepository repo;

    @Test
    public void 댓글_더미데이터_생성() {
        Long[] arr = {302L, 301L, 296L};

        Arrays.stream(arr).forEach(num ->{

            WebBoard board = new WebBoard();
            board.setBno(num);

            IntStream.range(0, 10).forEach(i -> {

                WebReply reply = new WebReply();
                reply.setReplyText("REPLY ..." + i);
                reply.setReplyer("replyer" + i);
                reply.setBoard(board);

                repo.save(reply);
            });
        });
    }
}
```

## 3. ReplyController의 설계
REST 방식에서 자원은 보통 복수형을 사용하므로 '/replies'를 이용하는 형태로 시작

| 기능                    | 전송방식 | URI 예                         |
| ----------------------- | -------- | ------------------------------ |
| 특정 게시물의 댓글 추가 | POST     | /replies/게시물 번호           |
| 특정 게시물의 댓글 삭제 | DELETE   | /replies/게시물 번호/댓글 번호 |
| 특정 게시물의 댓글 수정 | PUT      | /replies/게시물 번호           |
| 특정 게시물의 모든 댓글 | GET      | /replies/게시물 번호           |

##### WebReplyController.java 추가
```java
@RestController
@RequestMapping("/replies")
public class WebReplyController {

    @Autowired // setter를 만들어서 처리하는 것이 정석이지만..
    private WebReplyRepository replyRepo;

}
```

### 특정 게시물의 댓글 등록 처리
- `@PathVariable`: URI의 일부를 파라미터로 받기 위해서 사용하는 어노테이션
- `@RequestBody`: JSON으로 전달되는 데이터를 객체로 자동으로 변환하도록 처리하는 역할

리턴타입은 `ReponseEntity`를 이용
`ResponseEntity`: 코드를 이용해서 직접 Http Response의 상태 코드와 데이터를 직접 제어해서 처리할 수 있음

addReply()에서는 우선적으로 HTTP의 상태 코드 중에 201을 의미하는 'created'라는 메시지를 전송하도록 함
```java
@PostMapping("/{bno}")
public ResponseEntity<Void> addReply(@PathVariable("bno") Long bno, @RequestBody WebReply reply){
    log.info("addReply.............................");
    log.info("BNO: " + bno);
    log.info("REPLY: " + reply);

    return new ResponseEntity<>(HttpStatus.CREATED);
}
```

### REST 방식 테스트
'Yet Another REST Client(이하 YARC)'라는 도구를 이용해서 작성된 ReplyController를 확인
'/boards/list' GET 방식 API 호출 테스트 200 응답 확인

#### 댓글 등록 처리 호출
YARC로 '/replies/303' POST 방식 API 호출 테스트

Payload 테스트 JSON 데이터로 201 응답 확인
```json
{
    "replyText":"샘플 댓글",
    "replyer":"user00"
}
```