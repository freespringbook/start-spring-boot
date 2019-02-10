## 6. Spring MVC를 이용한 통합
## 1. 프로젝트의 기본 구조 생성하기
- DevTools
- Lombok
- JPA
- MySQL
- Thymeleaf
- Web 

### 이전 프로젝트 사항 
https://html5boilerplate.com/
- HTML5 boilerplate 설치 및 layout 구성

### 부트 스트랩 추가
https://getbootstrap.com/docs/3.4/getting-started/
```html
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css" integrity="sha384-PmY9l28YgO4JwMKbTvgaS7XNZJ30MK9FAZjjzXtlqyZCqBY6X6bXIkM++IkyinN+" crossorigin="anonymous">

<!-- Optional theme -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap-theme.min.css" integrity="sha384-jzngWsPS6op3fgRCDTESqrEJwRKck+CILhJVO5VvaAZCq8JYf8HsR/HPpBOOPZfR" crossorigin="anonymous">

<!-- Latest compiled and minified JavaScript -->
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js" integrity="sha384-vhJnz1OVIdLktyixHY4Uk3OHEwdQqPppqYR8+5mjsauETgLOcEynD9oPHhhz18Nw" crossorigin="anonymous"></script>
```

### 컨트롤러 생성 및 화면 확인하기
### 엔티티 클래스와 Repository 설계
### Querydsl 설정
### 테스트 코드 작성
#### 더미 데이터 추가

## 2. 페이징, 검색 처리
### Repository 페이징 테스트
`QuerydslPredicateExecutor`의 `findAll()`은  
`Predicate`타입과 파라미터와 `Pageable`를 파라미터로 전달 받을 수 있음

#### 검색 조건 처리

### 컨트롤러 페이징 처리
웹 화면에서 전달되는 데이터
- 페이지 관련
  - 페이지 번호(page-0,1,2,3...)
  - 페이지당 사이즈(size-Pagerequest의 기본 size는 20)
- 검색 관련
  - 검색 종류(type)
  - 검색 키워드(keyword)

#### @PageableDefault를 이용한 페이지 처리
Spring Data 모듈에서는 컨트롤러에서 파라미터 처리에 편리하도록 만들어진  
`@PageableDefault` 어노테이션을 이용하면 간단하게 **Pageable** 타입의 객체를 생성할 수 있음

URL 뒤에 `sort=aaa:desc`와 같은 파라미터를 추가하면 정렬 관련 설정이 추가 됨
```java
@GetMapping("/list")
public void list(@PageableDefault(direction = Sort.Direction.DESC, sort = "bno", size = 10, page = 0) Pageable page) {
    log.info("list() called..." + page);
}
```

#### PageVO를 생성하는 방식
`@PageableDefault` 단점
- 페이지 번호가 0부터 시작하기 때문에 일반 사용자들에게는 직관적이지 않음
- 파라미터를 이용해서 size를 지정할 수 있기 때문에 고의적으로 size 값을 크게 주는 것을 막을 수 없음
- 기타 정렬 방향이나 속성 역시 모두 브라우저에서 전달되는 값을 통해서 조절할 수 있기 때문에 고의적인 공격에 취약

`@PageableDefault`를 이용하는 방식 보다는 별도로 파라미터를 수집해서 처리하는 **Value Object**를 생성하는 방식이 
이러한 문제를 조금은 줄여줄 수 있음

내부에서는 페이지 번호가 자동으로 `1`이 감소된 형태로 **Pageable** 타입의 객체를 사용하도록 `-1`하여 리턴
```java
public Pageable makePageable(int direction, String... props) {
    Sort.Direction dir = direction == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
    return PageRequest.of(this.page - 1, this.size, dir, props);
}
```