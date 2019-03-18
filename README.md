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

### 댓글 등록 후 목록 처리
WebReplyRepository 인터페이스에 댓글 리스트를 처리하기 위한 메소드를 설계
```java
public interface WebReplyRepository extends CrudRepository<WebReply, Long> {

    @Query("SELECT r FROM WebReply r WHERE r.board = ?1 AND r.rno > 0 ORDER BY r.rno ASC")
    List<WebReply> getRepliesOfBoard(WebBoard board);

}
```

WebReplyController에서는 getRepliesOfBoard()를 호출
```java
@Autowired // setter를 만들어서 처리하는 것이 정석이지만..
private WebReplyRepository replyRepo;

@Transactional
@PostMapping("/{bno}")
public ResponseEntity<List<WebReply>> addReply(@PathVariable("bno") Long bno, @RequestBody WebReply reply){

    log.info("addReply.............................");
    log.info("BNO: " + bno);
    log.info("REPLY: " + reply);

    WebBoard board = new WebBoard();
    board.setBno(bno);

    reply.setBoard(board);
    replyRepo.save(reply);

    return new ResponseEntity<>(getListByBoard(board), HttpStatus.CREATED);
}

private List<WebReply> getListByBoard(WebBoard board) throws RuntimeException{
    log.info("getListByBoard...."+ board);
    return replyRepo.getRepliesOfBoard(board);
}
```

테스트
```
POST - http://localhost:8080/replies/1
{
    "replyText":"댓글 추가",
    "replyer":"replyer1"
}
```

**addReply()**는 WebRepository에 **save()** 작업과 **findBoard...()**를 연속해서 호출하기 때문에 `@Transactional` 처리를 함

나중에 게시물의 댓글의 목록이 필요할 수 있으므로 **getListByBoard()**라는 메소드로 분리

### 댓글 삭제
댓글이 삭제된 후에는 다시 해당 게시물의 모든 댓글을 갱신하기 위해 댓글의 번호와 게시물의 번호가 같이 필요
```java
@Transactional
@DeleteMapping("/{bno}/{rno}")
public ResponseEntity<List<WebReply>> remove(@PathVariable("bno")Long bno, @PathVariable("rno")Long rno){
    log.info("delete reply: "+ rno);

    replyRepo.deleteById(rno);

    WebBoard board = new WebBoard();
    board.setBno(bno);

    return new ResponseEntity<>(getListByBoard(board), HttpStatus.OK);
}
```

테스트
```
DELTE - http://localhost:8080/replies/1/1
```

### 댓글 수정
댓글 수정 처리는 PUT 방식을 이용해서 처리
```java
@Transactional
@PutMapping("/{bno}")
public ResponseEntity<List<WebReply>> modify(@PathVariable("bno")Long bno, @RequestBody WebReply reply){
    log.info("modify reply: "+ reply);

    replyRepo.findById(reply.getRno()).ifPresent(origin -> {
        origin.setReplyText(reply.getReplyText());
        replyRepo.save(origin);
    });

    WebBoard board = new WebBoard();
    board.setBno(bno);

    return new ResponseEntity<>(getListByBoard(board), HttpStatus.CREATED);
}
```

테스트
```
PUT - http://localhost:8080/replies/1
{
    "rno": 65,
    "replyText":"리플 수정",
    "replyer":"replyer1"
}
```

### 댓글 목록
댓글 목록은 GET 방식으로 처리하고 게시물의 번호를 이용

## 4. 화면에서의 댓글 처리
JavaScript로 하나의 객체를 생성해서 처리하는 '모듈 패턴'을 이용

- static/js 폴더 내에 reply.js 파일을 작성

replyManager는 즉시 실행 함수로 구성되어 있고 단 한 번만 실행됨
리턴은 '키'와 '메소드'로 이루어진 객체를 반환하게 됨

- view.html 수정

서버 구동 후 테스트
http://localhost:8080/boards/view?page=1&size=10&type=&keyword=&bno=301

### 댓글 목록의 출력
**view.html**에서는 `$(document).ready()`를 이용해 **replyManager**를 호출하는 코드를 작성
```javascript
<script th:inline="javascript" th:src="@{'/js/reply.js'}"></script>
<script th:inline="javascript">
    $(function (e) {
        //load replies
        replyManager.getAll([[${vo.bno}]], function (list) {
        });
    });
</script>
```

`getJSON()`을 활용해 GET 방식으로 JSON 데이터를 가져옴
```javascript
var replyManager = (function () {
    var getAll = function (obj, callback) {
        console.log("get All....");

        $.getJSON('/replies/'+obj, callback);
    };

```

**view.html**에서 결과를 처리할 함수를 작성
```javascript
<script th:inline="javascript">
    $(function (e) {
        //load replies
        replyManager.getAll([[${vo.bno}]], function (list) {
            console.log('list........' + list);
        });
    });
</script>
```

댓글 목록 테이블 구조 생성
```html
<div class='container'>
    <table class="table table-striped table-bordered table-hover">
        <thead>
        <tr>
            <th>RNO</th>
            <th>REPLY TEXT</th>
            <th>REPLER</th>
            <th>REPLY DATE</th>
        </tr>
        </thead>
        <tbody id="replyTable" >
        </tbody>
    </table>
</div>
```

`<tbody>` 내용 함수로 작성
```javascript
 <script th:inline="javascript">
    $(function (e) {

        (function getAllReplies(){
            //load replies
            replyManager.getAll([[${vo.bno}]], printList);
        })();


        function printList(list){
            var str = "";
            var replyObj;
            for(var i = 0; i < list.length; i++){
                replyObj = list[i];

                str += "<tr>" +
                    "<td>"+ replyObj.rno+" </td>" +
                    "<td>"+ replyObj.replyText+" </td>" +
                    "<td>"+ replyObj.replyer+" </td>" +
                    "<td>"+ formatDate(replyObj.regdate)+" </td>" +
                    "</tr>";
            }
            $("#replyTable").html(str);
        }

        function formatDate(timeValue){
            var date = new Date(timeValue);
            return  date.getFullYear()
                + "-" + (date.getMonth()+1 >= 10?date.getMonth()+1 : '0'+ (date.getMonth()+1)  )
                + "-" + date.getDate()
        }
    });
</script>
```

### 댓글 추가
- Modal 및 Add Reply 버튼 추가
- 관련 스크립트 추가
- Bootstrap4 관련 수정

#### 댓글 저장
- 댓글 추가 스크립트 추가
- 댓글 추가 POST 전송 처리

### 댓글 삭제 처리
- 댓글 삭제 스크립트 추가
- 댓글 삭제 Modal 버튼 추가
- 댓글 삭제 DELETE 전송 처리

### 댓글 수정 처리
- 댓글 수정 스크립트 추가
- 댓글 수정 PUT 전송 처리

## 5. 게시물 리스트에서의 댓글 개수 처리
### 게시물과 댓글 수의 문제
양방향으로 처리된 현재에는 아주 간단히 댓글의 개수만 숫자로 출력

'N+1 검색'이라고 하는 상황으로 이와 같은 처리는 성능에 치명적인 문제가 발생할 수 있음

'N+1'에서 많은 쿼리가 실행되는 가장 큰 이유는 게시물의 목록을 가져오는 쿼리가 단순히 tbl_webboards 테이블에만
접근해서 처리하기 때문

### @Query의 한계
'N+1'을 처리하기 위한 가장 쉬운 접근 방식은 @Query를 이용해서 직접 필요한 엔티티들 간의 관계를 처리하는 것

@Query의 가장 큰 한계는 **'JPQL의 내용이 고정된다'**는 점
동적으로 변하는 사오항에 대한 처리가 어려움

### 사용자 정의 쿼리 - 동적으로 JPQL 처리
1. 원하는 기능을 별도의 사용자 정의 인터페이스로 설계
2. 엔티티의 Repository 인터페이스를 설계할 때 사용자 정의 인터페이스 역시 같이 상속하도록 설계
3. 엔티티 Repository를 구현하는 클래스를 생성. 이때에는 반드시 'Repository 이름'+'impl'로 클래스 이름 지정.
   클래스 생성시에 부모 클래스를 QuerydslRepositorySupport로 지정
4. Repository 인터페이스 impl 클래스에 JPQLQuery 객체를 이용해서 내용을 작성

#### 사용자 정의 인터페이스 설계
`org.zerock.persistence` 패키지에 CustomWebBoard라는 인터페이스를 설계

#### 엔티티의 Repository 인터페이스 설계
`org.zerock.persistence` 패키지에 CustomCrudRepository라는 인터페이스를 설계

#### 사용자 정의 인터페이스의 구현
실제로 JPQL을 코드로 처리하는 작업은 '엔티티 Repository 이름'+'Impl'로 작성

클래스를 만들 때 주의할 점은 클래스의 이름과 QuerydslRepositorySupport를 부모 클래스로 지정
QuerydslRepositorySupport 클래스는 생성자를 구현

#### 테스트 코드의 작성 및 완성
CustomRepositoryTests를 작성

테스트 코드 실행 시 구현된 CustomCrudRepositoryImpl의 내용이 처리되면서 로그가 출력됨

다음 단계로는 기존의 페이징 처리를 구현
이때에는 Querydsl의 Qdomain등을 이용

최종 테스트 확인 후 최종적으로 WebReply와의 조인을 처리 해주고, 검색 조건들을 처리

### 컨트롤러와 화면 처리
WebBoardController는 CustomCrudRepository를 주입받도록 수정하고, list()를 수정

SQL에서 서버에서 실행되는 쿼리가 기존과 달리 쿼리문이 한 번 실행 됨

사용자 정의 쿼리를 생성하는 방식은 단계가 조금 복잡하기는 하지만, 코드를 이용해서
마은대로 조작할 수 있다는 장점이 있음
리플렉션 등을 이용한다면 좀 더 유연한 JQPL을 생성해서 처리할 수 있음