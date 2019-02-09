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