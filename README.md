# Thymeleaf 사용해 보기
#### application.properties 설정
템플릿 페이지를 수정하고 브라우저에서 별도의 서버 재시작 없이 바로 확인 가능
```properties
# Thymeleaf 캐싱 사용안함
spring.thymeleaf.cache=false
```

## 2. Thymeleaf 동작 확인하기
##### SampleController
```java
@Controller
public class SampleController {

    @GetMapping("/sample1")
    public void sample1(Model model) {
        // model.addAttribute("greeting", "Hello World");
        model.addAttribute("greeting", "안녕하세요");
    }
}
```

##### sample1.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thymeleaf3</title>
</head>
<body>
    <h1 th:text="${greeting}">Thymeleaf Test Page</h1>
</body>
</html>
```

## 3. Thymeleaf 간단한 예제 몇 가지
- 객체를 화면에 출력하기
- 리스트를 화면에 출력하기
- 변수나 제어문 처리
- request의 파라미터들을 사용하기
- 레이아웃 처리

### 객체를 화면에 출력하기
##### sample2 Controller
```java
@GetMapping("/sample2")
public void sample2(Model model) {
    MemberVO vo = new MemberVO(123, "u00", "p00", "홍길동", new Timestamp(System.currentTimeMillis()));
    model.addAttribute("vo", vo);
}
```

##### sample2.html
```html
<h1 th:text="${vo}">Thymeleaf Test Page</h1>
```

#### HTML 출력하기 - utext
`th:text`와 달리 `th:utext`는 문자열이 아니라 HTML 자체를 출력
```html
<div th:utext='${"<h3>"+vo.mid+"</h3>"}'></div>
<div th:text='${"<h3>"+vo.mid+"</h3>"}'></div>
```

### 리스트를 화면에 출력하기
화면에서 가장 많이 사용하는 루프 처리는 `th:each`를 이용해서 처리
`java.util.Iterable`, `Java.util.Map`, 배열등 사용가능
##### sample3 Controller
```java
@GetMapping("/sample3")
public void sample3(Model model) {

    List<MemberVO> list = new ArrayList<>();
    IntStream.range(0, 10).forEach(i -> {
        list.add(new MemberVO(123, "u0"+i, "p0"+i, "홍길동"+i, new Timestamp(System.currentTimeMillis())));
    });
    model.addAttribute("list", list);
}
```

##### sample3.html
`#dates.format()`은 Thymeleaf의 보조 객체로 날짜와 관련된 처리에 유용하게 사용됨
```html
<table border="1">
    <tr>
        <td>MID</td>
        <td>MNAME</td>
        <td>REGDATE</td>
    </tr>
    <tr th:each="member : ${list}">
        <td th:text="${member.mid}"></td>
        <td th:text="${member.mname}">Doe</td>
        <td th:text="${#dates.format(member.regdate, 'yyyy-MM-dd')}"></td>
    </tr>
</table>
```

`th:each`사용시 사용할 수 있는 추가 항목
- index: 0부터 시작하는 인덱스 번호
- count: 1부터 시작하는 번호
- size: 현재 대상의 length 혹은 size
- odd/even: 현재 번호의 홀수/짝수 여부
- first/last: 처음 요소인지 마지막 요소인지를 판단
```html
<table border="1">
    <tr>
        <td>INDEX</td>
        <td>COUNT</td>
        <td>SIZE</td>
        <td>ODD/EVEN</td>
        <td>FIRST/LAST</td>
        <td>MID</td>
        <td>MNAME</td>
        <td>REGDATE</td>
    </tr>
    <tr th:each="member, iterState : ${list}">
        <td th:text="${iterState.index}"></td>
        <td th:text="${iterState.count}"></td>
        <td th:text="${iterState.size}"></td>
        <td th:text="${'홀수: '+iterState.odd + ' 짝수: ' + iterState.even}"></td>
        <td th:text="${'처음: '+iterState.first + ' 마지막: ' + iterState.last}"></td>
        <td th:text="${member.mid}"></td>
        <td th:text="${member.mname}">Doe</td>
        <td th:text="${#dates.format(member.regdate, 'yyyy-MM-dd')}"></td>
    </tr>
</table>
```

### 지역변수의 선언 if~unless 제어 처리
특정한 범위에서만 유효한 지역변수를 `th:with`를 이용해서 선언할 수 있음

##### sample4 Controller
```java
@GetMapping("/sample4")
public void sample4(Model model) {

    List<MemberVO> list = new ArrayList<>();
    IntStream.range(0, 10).forEach(i -> {
        list.add(new MemberVO(i, "u000"+i%3, "p0000"+i%3, "홍길동"+i, new Timestamp(System.currentTimeMillis())));
    });
    model.addAttribute("list", list);
}
```
target는 `table` 내에서만 유효한 지역 변수
```html
<table border="1" th:with="target='u0001'">
    <tr>
        <td>MID</td>
        <td>MNAME</td>
        <td>REGDATE</td>
    </tr>
    <tr th:each="member : ${list}">
        <td th:text="${member.mid == target ? 'SECRET': member.mid}">Doe</td>
        <td th:text="${member.mname}"></td>
        <td th:text="${#dates.format(member.regdate, 'yyyy-MM-dd')}"></td>
    </tr>
</table>
```

##### if~else 처리
##### sample4.html
```html
<table border="1" th:with="target='u0001'">
    <tr>
        <td>MID</td>
        <td>MNAME</td>
        <td>REGDATE</td>
    </tr>
    <tr th:each="member : ${list}">
        <td th:if="${member.mid}">
            <a href="/modify" th:if="${member.mid == target}">MODIFY</a>
            <p th:unless="${member.mid == target}">VIEW</p>
        </td>
        <td th:text="${member.mname}"></td>
        <td th:text="${#dates.format(member.regdate, 'yyyy-MM-dd')}"></td>
    </tr>
</table>
```

### 인라인 스타일로 Thymeleaf 사용하기
인라인 가능한 영역에 `th:inline='text'` 추가
##### sample5 Controller
```java
@GetMapping("/sample5")
public void sample5(Model model){
    String result = "SUCCESS";
    model.addAttribute("result", result);
}
```

##### sample5.html
```html
<script th:inline="javascript">
    var result = [[${result}]]
</script>
<script>
    var result = [[${result}]]
</script>
```

##### sample6 Controller
```java
@GetMapping("/sample6")
public void sample6(Model model) {
    List<MemberVO> list = new ArrayList<>();
    IntStream.range(0, 10).forEach(i -> {
        list.add(new MemberVO(i, "u0"+i, "p0"+i, "홍길동"+i, new Timestamp(System.currentTimeMillis())));
    });
    model.addAttribute("list", list);

    String result = "SUCCESS";
    model.addAttribute("result", result);
}
```

##### sample6.html
```html
<table border="1" th:inline="text">
    <tr>
        <td>MID</td>
        <td>MNAME</td>
        <td>REGDATE</td>
    </tr>
    <tr th:each="member : ${list}">
        <td>[[${member.mid}]]</td>
        <td>[[${member.mname}]]</td>
        <td>[[${member.regdate}]]</td>
    </tr>
</table>
<script th:inline="javascript">
    var result = [[${result}]];
</script>
```

## 4. Thymeleaf의 유틸리티 객체
- **Expression Basic Objects(표현식 기본 객체)**
  - #ctx
  - #vars
  - #locale
  - #httpServletRequest
  - #httpSession
- **Expression Utility Objects(표현식 유틸 객체)**
  - #dates
  - #calendars
  - #numbers
  - #strings
  - #objects
  - #bools
  - #arrays
  - #lists
  - #sets
  - #maps
  - #aggregates
  - #messages

`#vars`의 경우 생략한 상태로 주로 사용됨
```html
<div>[[${result}]]</div>
<div>[[${#vars.result}]]</div>
```

### 유틸리티 객체
Thymeleaf의 표현식은 OGNL(Object-Graph Navigation Language) 표현식을 이용해서 데이터를 출력
##### sample7 Controller
```java
@GetMapping("/sample7")
public void sample7(Model model) {
    model.addAttribute("now", new Date());
    model.addAttribute("price", 123456789);
    model.addAttribute("title", "This is a just sample");
    model.addAttribute("options", Arrays.asList("AAAA","BBB","CCC","DDD"));
}
```

##### sample7.html
```html
<h1 th:text="${now}"></h1>
<h1 th:text="${price}"></h1>
<h1 th:text="${title}"></h1>
<h1 th:text="${options}"></h1>
```

#### 날짜 관련 #dates, #calendars
날짜 관련 기능은 `java.util.Date`와 `java.util.Calendar`의 기능을 이용한다고 생각하면 됨
```html
<h2 th:text="${#dates.format(now, 'yyyy-MM-dd')}"></h2>
<div th:with="timeValue=${#dates.createToday()}">
    <p>[[${timeValue}]]</p>
</div>
```

#### 숫자 관련 #numbers
Integer나 Double, Float에 대한 포매팅을 처리할 때 주로 사용  
수소점의 경우 formatInteger를 이용하면 정수 처리가 되기 때문에 주의해서 사용
```html
<h2 th:text="${#numbers.formatInteger(price,3,'COMMA')}"></h2>
<div th:with="priceValue=99.87654">
    <p th:text="${#numbers.formatInteger(priceValue,3,'COMMA')}"></p>
    <p th:text="${#numbers.formatDecimal(priceValue,5,10,'POINT')}"></p>
</div>
```

#### 문자 관련 #strings
문자열과 관련해서는 대소문자 변환이나 `contains()`등 기본적인 기능들 외에 문자열을 결합하는 `join`이나  
리스트로 나누는 `listsplit`등의 기능들이 지원됨
```html
<h1 th:text="${title}"></h1>
<span th:utext="${#strings.replace(title,'s','<b>s</b>')}"></span>
<ul>
    <li th:each="str:${#strings.listSplit(title,' ')}">[[${str}]]</li>
</ul>
```

## 5. Thymeleaf 링크 처리
일반적인 웹 페이지의 링크는 'http://www...'와 같은 형태의 절대(absolute path)경로와  
현재 URL을 기준으로 이동하는 상대(context-relative) 경로 두가지로 구분됨

WAS상에서 특정 경로에서 프로젝트가 실행되는 경우에 문제가 될 수 있음

Thymeleaf는 이러한 문제 해결을 위해 `@{}`을 이용해 경로에 대한 처리를 자동으로 처리
##### sample8 Controller
```java
@GetMapping("/sample8")
public void sample8(Model model) {
    
}
```

##### sample8.html
```html
<ul>
    <!-- 절대 경로 처리 http://localhost:8080/sample1 -->
    <li><a th:href="@{http://localhost:8080/sample1}">sample1</a></li>
    <!-- 컨텍스트 경로 처리 /boot05/sample1 -->
    <li><a th:href="@{/sample1}">sample1</a></li>
    <!-- 현재 프로젝트 경로 /sample1 -->
    <li><a th:href="@{~/sample1}">sample1</a></li>
    <!-- 파라메터 전달 -->
    <li><a th:href="@{/sample1(p1='aaa', p2='bbb')}">sample1</a></li>
</ul>
```

## 6. Thymeleaf의 레이아웃 기능
`th:replace`, `th:include`와 같은 속성들을 이용해서 기존 페이지의 일부분을 다른 내용으로 쉽게 변경할 수 있음

Thymeleaf는 부분적인 화면 처리 기능과 더불어 화면 전체의 레이아웃을 지정하고  
페이지를 작성할 때 필요한 부분만을 교체해서 사용하는 템플릿 기능이 지원됨
##### header.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <div th:fragment="header">
        헤더 파일입니다
    </div>
</html>
```

##### footer.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <div th:fragment="footer">
        Footer 파일입니다
    </div>
</html>
```

##### sample8.html
```html
<div th:insert="~{fragments/header::header}"></div>
<div>
    <ul>
        <!-- 절대 경로 처리 http://localhost:8080/sample1 -->
        <li><a th:href="@{http://localhost:8080/sample1}">sample1</a></li>
        <!-- 컨텍스트 경로 처리 /boot05/sample1 -->
        <li><a th:href="@{/sample1}">sample1</a></li>
        <!-- 현재 프로젝트 경로 /sample1 -->
        <li><a th:href="@{~/sample1}">sample1</a></li>
        <!-- 파라메터 전달 -->
        <li><a th:href="@{/sample1(p1='aaa', p2='bbb')}">sample1</a></li>
    </ul>
</div>
<div th:insert="~{fragments/footer::footer}"></div>
```

### Thymeleaf layout dialect를 이용한 레이아웃 재사용하기
**Thymeleaf layout dialect**를 이용하면 하나의 레이아웃을 작성하고 이를 재사용해서 여러 페이지에 동일한 레이아웃을 적용시킬 수 있음  
**템플릿 상속**이라고 부르기도 함
##### 의존성 추가
```xml
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
    <version>2.3.0</version>
</dependency>
```

상속 기능 적용을 위해 **HTML5 Boilerplate** 적용  
**HTML5 Boilerplate**는 웹 페이지에서 가장 기본적으로 필요한 구조를 템플릿으로 만들어 둔 것

https://html5boilerplate.com/

다운로드하여 `static` 디렉토리에 붙여넣고 **index.html** 내용을 **'layout/layout1.html'**에 붙여 넣기
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link rel="manifest" href="site.webmanifest">
    <link rel="apple-touch-icon" href="icon.png">
    <!-- Place favicon.ico in the root directory -->

    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/main.css">
</head>

<body>
    <!--[if lte IE 9]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="https://browsehappy.com/">upgrade your browser</a> to improve your experience and security.</p>
    <![endif]-->


    <!-- content body -->
    <div>

    </div>

    <script src="js/vendor/modernizr-3.6.0.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
    <script>window.jQuery || document.write('<script src="js/vendor/jquery-3.3.1.min.js"><\/script>')</script>
    <script src="js/plugins.js"></script>
    <script src="js/main.js"></script>

    <!-- custom javascript -->
    <script></script>

    <!-- Google Analytics: change UA-XXXXX-Y to be your site's ID. -->
    <script>
        window.ga = function () { ga.q.push(arguments) }; ga.q = []; ga.l = +new Date;
        ga('create', 'UA-XXXXX-Y', 'auto'); ga('send', 'pageview')
    </script>
    <script src="https://www.google-analytics.com/analytics.js" async defer></script>
</body>
</html>
```

#### th:block과 layout:fragment
매번 페이지 제작 시 변경되는 영역은 **'content-body'**와 **'custom javascript'** 영역

`layout:fragment`는 `<div>` 와 같이 실제로 보이는 영역에 적용  
`th:block`은 아무런 태그가 없는 영역을 표시할 때 사용

아래와 같이 변경
```html
<!-- content body -->
<div layout:fragment="content">

</div>

<!-- custom javascript -->
<th:block layout:fragment="script"></th:block>
```

##### sample/hello.html
```html
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{/layout/layout1}">

    <header layout:fragment="header">
        <h1>Header</h1>
    </header>
    <div layout:fragment="content">
        <h1>Hello Page</h1>
    </div>
    <footer layout:fragment="footer">
        <h1>Footer</h1>
    </footer>

    <th:block layout:fragment="script">
        <script th:inline="javascript">
            console.log("Java Script Block!!!")
        </script>
    </th:block>
</html>
```

##### layout/layout1.html `th:href` 로 링크 경로 수정
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <title></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <link rel="manifest" href="site.webmanifest">
        <link rel="apple-touch-icon" href="icon.png">
        <!-- Place favicon.ico in the root directory -->

        <link rel="stylesheet" th:href="@{/css/normalize.css}">
        <link rel="stylesheet" th:href="@{/css/main.css}">
        <script th:src="@{/js/vendor/modernizr-3.6.0.min.js}"></script>
    </head>

    <body>
        <!--[if lte IE 9]>
        <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="https://browsehappy.com/">upgrade your browser</a> to improve your experience and security.</p>
        <![endif]-->

        <header layout:fragment="header">
            <h1>My website</h1>
        </header>
        <!-- content body -->
        <div layout:fragment="content">
            <p>Page content goes here</p>
        </div>
        <footer layout:fragment="footer">
            <h1>My Footer</h1>
        </footer>

        <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
        <script>window.jQuery || document.write('<script th:src="@{/js/vendor/jquery-3.3.1.min.js}"><\/script>')</script>
        <script th:src="@{/js/plugins.js}"></script>
        <script th:src="@{/js/main.js}"></script>

        <!-- custom javascript -->
        <th:block layout:fragment="script"></th:block>

        <!-- Google Analytics: change UA-XXXXX-Y to be your site's ID. -->
        <script>
            window.ga = function () { ga.q.push(arguments) }; ga.q = []; ga.l = +new Date;
            ga('create', 'UA-XXXXX-Y', 'auto'); ga('send', 'pageview')
        </script>
        <script src="https://www.google-analytics.com/analytics.js" async defer></script>
    </body>

</html>
```