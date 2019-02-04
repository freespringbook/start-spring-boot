# 4. 다양한 연관관계 처리
- 객체 간 연관관계 설정
- 단방향, 양방향 관계의 이해
- JPQL을 이용한 @Query 처리와 Fetch JOIN(스프링 부트 2.0.0)

## 1. 연관관계 처리의 순서와 사전 설계
1. 필요한 각각 클래스를 정의
2. 각 클래스의 연관관계에 대한 설정을 추가
   1. '일대일', '다대다'등의 연관관계 설정
   2. 단방향, 양방향 설정
3. 데이터베이스상에 원하는 형태의 테이블이 만들어지는지를 확인
4. 테스트 코드를 통해서 정상적으로 동작하는지를 확인

### 관계형 데이터베이스의 설계와 JPA
1. 가장 중심이 되는 사람이나 명사를 결정하고, 이에 대한 구조를 대략적으로 설계
2. 생성된 데이터들이 상호 작용을 하면서 만들어내는 새로운 데이터를 정의
3. 다시 세분화해서 설계

#### 중심이 되는 데이터 결정
가장 중심이 되는 데이터는 **아무 사전 조건 없는 순수한 데이터**
- 순수한 장부의 형태로 존재
- 중심이 되는 데이터는 독립적인 라이프사이클을 유지
- 고객의 요구사항에서 항상 모든 행위의 주어나 목적어가 됨

#### 중심 데이터 간의 상호 작용
'동사'에는 주로 중심이 되는 데이터들의 **동작과 히스토리**가 기록됨

#### 연관관계의 설정
##### 전통적인 관계형 데이터베이스
**ERD(Entity Relation Diagram)**
- 일대일(One To One 1:1)
- 일대다(One To Many 1:N)
- 다대다(Many To Many M:N)

##### JPA에서의 연관관계
- 일대일(@OneToOne)
- 일대다(@OneToMany)
- 다대일(@ManyToOne)
- 다대다(@ManyToMany)

JPA를 이용하는 경우 '방향'에 대한 설정이 필요
- 단방향(Unidirectional) 참조: 한쪽의 클래스만이 다른 클래스의 인스턴스를 참조하도록 설정
- 양방항(Bidirectional) 참조: 양쪽 클래스 모두 다른 클래스의 인스턴스를 참조

### 작성하려는 예제의 개요
- 회원과 프로필 사진들의 관계(일대다, 다대일, 단방향)
- 자료실과 첨부 파일의 관계(일대다, 다대일, 단방향)
- 게시물과 댓글의 관계(일대다, 다대일, 양방향)

동일한 관계의 예를 3번이나 드는 이유는 **참조에 대한 방향성** 문제

## 2. 회원과 프로필 사진의 관계 - 단방형 처리 1
### 예제 프로젝트 생성
Spring Data JPA를 이용한 개발 순서
- 각 엔티티 클래스의 설계
- 각 엔티티 간의 연관관계 파악 및 설정
- 단방향, 양방향 결정

##### Optional<T>
**Optional**을 가장 쉽게 이해하는 방식은 '`null`을 대신한다'라고 생각  
**Optional**은 말그대로 존재할 수도 있고, 안 할 수도 있다는 의미  
과거에는 코드를 통해서 결과 데이터에 `null` 값이 나오는지 직접 일일히 체크했어야 했지만  
**Optional**을 이용하면 `null`에 대해서 신경쓰지 않고 코드를 작성할 수 있음

**Optional**을 이용할 때에는 주로 `get()` 이나 `isPresent()`를 이용하는데  
이를 통해서 결과를 반환받거나 결과를 어떤 식으로 처리할 것인지를 지정하게 됨


**MySQL**의 경우 데이터베이스의 엔진을 **MyISAM**과 **InnoDB**로 구분  
**MyISAM** 예전 **MySQL**부터 사용되던 엔진으로 속도면에서 좀 더 나을 수 있지만  
데이터 무결성을 제대로 체크 히지 않으므로 **InnoDB**를 권장

`spring.jpa.database-platform`를 지정하지 않으면 기본적으로 **MyISAM**로 지정됨
**InnoDB**를 사용하기 위해서는 `org.hibernate.dialect.MySQL5InnoDBDialect`를 명시적으로 지정함
**InnoDB**로 지정하지 앟는 경우에는 외래키 대신에 인덱스가 생성되므로 주의해야함

### 각각의 엔티티 클래스 설계
**Member** 와 **Profile** 클래스 생성

##### GenerationType.AUTO와 GenerationType.INDENTITY
엔티티의 식별키를 처리하는 여러 방식(전략)중에서 `GenerationType.AUTO`는  
데이터베이스에 맞게 자동으로 식별키를 처리하는 방식으로 동작

**AUTO**로 지정하면 `hibernate_sequence`라는 테이블을 생성하고 변호를 유지함  
`hibernate_sequence` 테이블은 자동으로 생성되는 모든 엔티티들이 공유하는 테이블

### 연관관계의 설정과 단방향/양방향
#### JPA의 연관관계 어노테이션 처리
Profile과 Member은 다대일 이므로 Profile 쪽 Member에 `@ManyToOne` 어노테이션 설정

#### 생성된 테이블 구조의 확인
연관관계를 맺은 후 프로젝트를 실행해서 올바른 구조로 생성되는지를 중간중간 확인

### Repository 작성
MemberRepository 와 ProfileRepository 생성

### 테스트를 통한 검증
- `@Log`: Lombok의 로그를 사용할 때 이용하는 어노테이션
- `@Commit`: 테스트 결과를 데이터베이스에 commit하는 용도로 사용

#### 특정 회원의 프로필 데이터 처리

### 단방향의 문제와 Fetch Join
**'회원 정보를 조회하면서 회원의 현재 프로필 사진도 같이 보여주어야 한다'**와 같은 복합적인 요구사항 처리를 위해  
JPA에서는 'Fetch Join'이라는 기법을 통해서 SQL에서 조인을 처리하는 것과 유사한 작업을 처리  
'Fetch Join'은 SQL과 달리 **JPQL**을 이용해서 처리

#### JPA의 Join 처리
`@Query`에서 사용하는 **JPQL**는 클래스를 보고 작성 하기 때문에 참조 관계가 없는 다른 엔티티 사용하는 것이 불가능 하지만
스프링 부트 2.0이상에서는 Hibernate 5.2.x에서는 참조 관계가 없어도 **'ON'** 구문을 이용해서 **LEFT OUTER JOIN**을 처리 할 수 있음

**"uid가 'user1'인 회원의 정보와 더불어 회원의 프로필 사진 숫자를 알고 싶다"** 라는 요구사항 처리 

SQL로 처리시
```sql
SELECT member.uid, count(fname)
FROM
  tbl_members member LEFT OUTER JOIN tbl_profile profile
  ON profile.member_uid = member.uid
WHERE member.uid = 'user1'
group by member.uid
```

`@Query`를 이용해서 처리시
```java
public interface MemberRepository extends CrudRepository<Member, String> {

    @Query("SELECT m.uid, count(p) FROM Member m LEFT OUTER JOIN Profile p ON m.uid = p.member WHERE m.uid = ?1 GROUP BY m")
    List<Object[]> getMemberWithProfileCount(String uid);
}
```

```java
@Test
public void FETCH_JOIN_테스트1() {
    List<Object[]> result = memberRepo.getMemberWithProfileCount("user1");
    result.forEach(arr -> System.out.println(Arrays.toString(arr)));
}
```

메소드 리턴 타입은 `List<Object[]>`로 처리됨  
**JPQL**에서는 엔티티 타입 뿐 아니라 다른 자료형들도 반환할 수 있기 때문에  
**List**는 결과의 Row 수를 의미  
**Object[]**는 칼럼들을 의미

**'회원 정보와 현재 사용 중인 프로필에 대한 정보'**를 얻고자하는 **JPQL**
```java
@Query("SELECT m, p FROM Member m LEFT OUTER JOIN Profile p ON m.uid = p.member WHERE m.uid = ?1 AND p.current = true")
List<Object[]> getMemberWithProfile(String uid);
```

```java
@Test
public void FETCH_JOIN_테스트2() {
    List<Object[]> result = memberRepo.getMemberWithProfile("user1");
    result.forEach(arr -> System.out.println(Arrays.toString(arr)));
}
```

> Hibernate 5.0 이하에서는 반드시 참조를 해주어야만 함

## 3. 자료실과 첨부 파일의 관계 - 단방향 2
**Member**에서 **Profile**에 대한 참조를 이용하고 **Profile**에서는 참조를 하지 않는 설계 역시 가능
이 경우 `@JoinTable`이라는 설정을 이용

### 엔티티 클래스 작성
**PDSFile** 클래스는 자료에 첨부된 파일을 의미하므로 파일의 이름만 저장

#### 연관관계 설정
단방향 연관관계를 설정하고 프로젝트를 실행했을 때  
**tbl_pds**와 **tbl_pdsfile**은 엔티티 클래스 작성 시에 지정했지만 **tbl_pds_files**는 자동으로 생성된 테이블임

단방향 `@OneToMany`에서 별도의 테이블이 생성되는 것이 싫다면 별도의 테이블 없이 특정한 테이블을 조인할 것이라고  
명시하거나(`@JoinTable`), `@JoinColumn`을 명시해 기존 테입ㄹ을 이용해서 조인한다고 표현해 주어야 함

- `@JoinTable`: 자동으로 생성되는 테이블 대신에 별도의 이름을 가진 테이블을 생성하고자 할 때 사용
- `@JoinColumn`: 이미 존재하는 테이블에 칼럼을 추가할 때 사용

```java
@OneToMany
@JoinColumn(name="pdsno")
private List<PDSFile> files;
```
```java
@ToString(exclude = "files")
```

`@JoinColumn`을 이용해 변경하고 `@ToString`을 변경 하면
**tbl_pdsfiles** 테이블에 **pdsno**라는 이름의 칼럼이 추가 됨

#### 연관관계에 따른 Repository
JPA에서 처리하려는 엔티티 객체의 상태에 따라서 종속적인 객체들의 영속성도 같이 처리 되는 것을 **'영속성 전이'**라고 한다  
**영속성 전이**는 부모 엔티티나 자식 엔티티의 상태 변화가 자신과 관련 있는 엔티티에 영향을 주는 것을 의미

JPA 종속적인 엔티티의 영속성 전이에 대한 설정
- ALL: 모든 변경에 대해 전이
- PERSIST: 저장 시에만 전이
- MERGE: 병합 시에만 전이
- REMOVE: 삭제 시에만 전이
- REFRESH: 엔티티 매니저의 refresh() 호출 시 전이
- DETACH: 부모 엔티티가 detach되면 자식 엔티티 역시 detach

ALL을 사용하려면 **PDSBoard**에 `@OneToMay` 속성에 cascade 속성을 다음과 같이 지정
```java
@OneToMany(cascade= CascadeType.ALL)
@JoinColumn(name="pdsno")
private List<PDSFile> files;
```

### 첨부 파일 수정과 @Modifying, @Transactional
`@Query`는 기본적으로 'select' 구문만을 지원  
`@Modifying`을 이용해서 DML(insert, update, delete) 작업을 처리
```java
public interface PDSBoardRepository extends CrudRepository<PDSBoard, Long> {

    @Modifying
    @Query("UPDATE FROM PDSFile f set f.pdsfile = ?2 WHERE f.fno = ?1 ")
    int updatePDSFile(Long fno, String newFileName);
}
```

`@Query`를 이용해서 '`update`', '`delete`'를 사용하는 경우에는 반드시 `@Transactional` 처리를 필요로 함
```java
@Transactional
@Test
public void 첨부파일_이름_수정(){
    Long fno = 1L;
    String newName = "updatedFile1.doc";

    int count = repo.updatePDSFile(fno, newName);
    // @Log 설정된 이후 사용 가능
    log.info("update count: " + count);
}
```

테스트에서 `@Transactional`이 기본적으로 롤백 처리를 시도  
`@Commit`을 추가해서 자동으로 commit 처리

#### 순수 객체를 통한 파일 수정
전통적인 방식으로 처리
```java
@Transactional
@Test
public void 첨부파일_이름_수정2(){
    String newName = "updatedFile2.doc";
    // 반드시 번호가 존재하는지 확인할 것
    Optional<PDSBoard> result = repo.findById(2L);

    result.ifPresent(pds -> {
        log.info("데이터가 존재하므로 update 시도");
        PDSFile target = new PDSFile();
        target.setFno(2L);
        target.setPdsfile(newName);

        //fno 값으로 equals()와 hashcode() 사용
        int idx = pds.getFiles().indexOf(target);

        if(idx > -1){
            List<PDSFile> list = pds.getFiles();
            list.remove(idx);
            list.add(target);
            pds.setFiles(list);
        }
        repo.save(pds);
    });
}
```

### 첨부 파일 삭제
```java
@Modifying
@Query("DELETE FROM PDSFile f WHERE f.fno = ?1")
int deletePDSFile(Long fno);
```

### 조인 처리
**자료와 첨부 파일의 수를 자료 번호의 역순으로 출력**
```java
@Query("SELECT p, count(f) FROM PDSBoard p LEFT OUTER JOIN p.files f ON p.pid = f WHERE p.pid > 0 GROUP BY p ORDER BY p.pid DESC ")
List<Object[]> getSummary();
```

```java
@Test
public void 자료와첨부_파일의수_자료번호의_역순출력() {
    repo.getSummary().forEach(arr -> log.info(Arrays.toString(arr)));
}
```

## 4. 게시물과 댓글의 관계 - 양방향
### 연관관계의 설정
FreeBoard 클래스는 '일대다'관계이므로 `@OneToMany`  
```java
@OneToMany
private List<FreeBoardReply> replies;
```
FreeBoardReply는 '다대일'의 관계이므로 `@ManyToOne`
```java
@ManyToOne
private FreeBoard board;
```

양쪽 테이블 중간에 지정하지 않은 테이블 하나가 생성되는 이유는 `@OneToMany` 때문  
`@OneToMany` 관계를 저장하려면 중간에 '다(Many)'에 해당하는 정보를 보관하기 위해서 JPA의 구현체는 별도의 테이블을 생성함

#### mappedBy 속성
JPA에서는 관계를 설정할 때 **PK** 쪽이 `mappedBy`라는 속성을 이용해서 자신이 다른 객체에게 '매여있다'는 것을 명시하게 됨  
'해당 엔티티가 관계의 주체가 되지 않는다는 것을 명시한다'고 한다
```java
@OneToMany(mappedBy = "board")
private List<FreeBoardReply> replies;
```

#### 양방향 설정과 toString()
양방향 참조를 사용하는 경우에는 반드시 한쪽은 `toString()`에서 참조하는 객체를 출력하지 않도록 수정해주어야 함  
**Lombok**의 **@ToString**에는 `exclude` 속성을 이용해 특정 인스턴스 변수를 `toString()`에서 제외
```java
@ToString(exclude = "replies")
```
```java
@ToString(exclude = "board")
```

#### Repository 작성
각 엔티티가 별도의 라이프사이클을 가진다면 별도의 **Repository**를 생성하는 것이 좋음

**FreeBoard** 와 **FreeBoardReply** **Repository** 각각 생성
```java
public interface FreeBoardRepository extends CrudRepository<FreeBoard, Long> {
}
```
```java
public interface FreeBoardReplyRepository extends CrudRepository<FreeBoardReply, Long> {
}
```

#### 테스트 코드
게시물에 댓글을 추가하는 방식
- 단방향에서 처리하듯이 **FreeBoardReply**를 생성하고 **FreeBoard** 자체는 새로 만들어서 bno 속성만을 지정하여 처리하는 방식
- 양방향이므로 **FreeBoard** 객체를 얻어온 후 **FreeBoardReply**를 댓글 리스트에 추가한 후에 **FreeBoard** 자체를 저장하는 방식
```java
@Transactional
@Test
public void 양방향_댓글등록() {
    Optional<FreeBoard> result = boardRepo.findById(200L);
    result.ifPresent(board -> {
        List<FreeBoardReply> replies = board.getReplies();
        FreeBoardReply reply = FreeBoardReply.builder()
                .reply("REPLY................")
                .replayer("replyer00")
                .board(board)
                .build();
        replies.add(reply);
        board.setReplies(replies);
        boardRepo.save(board);
    });
}
```

##### 1. 게시물이 저장될 때 댓글이 같이 저장되도록 cascading 처리가 되어야 함
```java
@OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
private List<FreeBoardReply> replies;
```

##### 2. 댓글 쪽에도 변경이 있기 때문에 트랜잭션을 처리해 주어야 함
```java
@Transactional
@Test
```

#### 단방향 방식의 댓글 추가
```java
@Test
public void 단방향_댓글등록() {
    FreeBoard board = new FreeBoard();
    board.setBno(199L);

    FreeBoardReply reply = FreeBoardReply.builder()
            .reply("REPLY................")
            .replayer("replyer00")
            .board(board)
            .build();
    replyRepo.save(reply);
}
```

### 게시물의 페이징 처리와 @Query
- 쿼리 메소드를 이용하는 경우의 '게시물 + 댓글의 수'
- `@Query`를 이용하는 경우의 '게시물 + 댓글의 수'

#### 쿼리 메소드를 이용하는 경우
페이징 처리를 하기 위해서 tbl_freeboards만을 이용
```java
public interface FreeBoardRepository extends CrudRepository<FreeBoard, Long> {
    List<FreeBoard> findByBnoGreaterThan(Long bno, Pageable page);
}
```
```java
@Test
public void 쿼리메소드_게시물페이징() {
    Pageable page = PageRequest.of(0, 10, Sort.Direction.DESC, "bno");
    boardRepo.findByBnoGreaterThan(0L, page).forEach(board -> {
        log.info(board.getBno() +": "+board.getTitle());
    });

}
```

#### 지연로딩(lazy loading)
JPA는 연관관계가 있는 엔티티를 조회할 때 기본적으로 **'지연 로딩(lazy loading)'**이라는 방식을 이용  
정보가 필요하기 전까지는 최대한 테이블에 접근하지 않는 방식을 의미  

지연 로딩을 하는 가장 큰 이유는 성능  
JPA에서는 연관관계의 **Collection** 타입을 처리할 때 **'지연 로딩'**을 기본으로 사용

**지연 로딩**의 반대 개념은 **'즉시 로딩(eager loading)'**  
**즉시 로딩**은 일반적으로 조인을 이용해서 필요한 모든 정보를 처리하게 됨  
**즉시 로딩**을 사용하려면 `@OneToMany`에 'fetch'라는 속성값으로 `FetchType.EAGER`를 지정하면 됨
```java
@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
private List<FreeBoardReply> replies;
```
성능에 영향을 줄 수 있는 **즉시 로딩**을 이용하는 것은 주의할 필요가 있음  
**지연 로딩**과 **즉시 로딩**을 사용할 때에는 반드시 해당 작업을 위해서 어떠한 SQL들이 실행되는지를 체크 해야 함

**지연 로딩**을 이용하면서 댓글을 같이 가져오고 싶다면 `@Transactional`을 이용해서 처리
```java
@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<FreeBoardReply> replies;
```

```java
@Transactional
@Test
```

#### @Query와 Fetch Join을 이용한 처리
**지연 로딩**의 문제를 해결하는 가장 좋은 방법은 `@Query`를 이용해서 조인 처리를 하는 것

`@Query`를 이용해서 엔티티의 일부 속성이나 다른 엔티티의 조회할 때의 리턴 타입은 **`컬렉션<배열>`**의 형태가 됨  
이 경우 `List<Object[]>`에서 **List**는 결과 데이터의 '행(row)'을 의미하고 **Object[]**는 '열(column)'을 의미

**FreeBoard**에 **replies**가 **지연 로딩**으로 처리되어 있는지 확인  
기본 옵션이 **지연 로딩**이므로 삭제해도 무방
```java
@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<FreeBoardReply> replies;
```

```java
@Query("SELECT b.bno, b.title, count(r) FROM FreeBoard b LEFT OUTER JOIN b.replies r WHERE b.bno > 0 GROUP BY b")
List<Object[]> getPage(Pageable page);
```

```java
@Test
public void Query와_FETCH_JOIN처리() {
    Pageable page = PageRequest.of(0, 10, Sort.Direction.DESC, "bno");
    boardRepo.getPage(page).forEach(arr -> {
        log.info(Arrays.toString(arr));
    });
}
```

### 게시물 조회와 인덱스
**지연 로딩**은 필요할 때까지 댓글 관련 데이터를 로딩하지 않기 때문에 성능면에서 장점을 가지고 있음  
한 번에 게시물과 댓글의 내용을 같이 보여주는 상황이라면 SQL이 한번에 처리되지 않기 때문에 여러 번 데이터 베이스를 호출하는 문제  

해결책은 **지연 로딩**을 그대로 이용하고 댓글 쪽에는 필요한 순간에 데이터가 좀 더 빨리 나올 수 있도록 신경 쓰는 방식

#### 인덱스 처리
댓글 목록의 경우는 특정한 게시물 번호에 영향을 받기 때문에 게시물 번호에 대한 인덱스를 생성해 두면 데이터가 많을 때 성능의 향상을 기대할 수 있음

`@Table`에는 인덱스를 설계할 때 `@Index`와 같이 사용해서 테이블 생성 시에 인덱스가 설계되도록 지정할 수 있음
```java
@Table(name = "tbl_free_replies", indexes = {@Index(unique = false, columnList = "board_bno")})
@EqualsAndHashCode(of = "rno")
public class FreeBoardReply {
```