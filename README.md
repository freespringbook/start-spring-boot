# 2. Spring DATA JPA 맛보기
### JPA Annotation
http://www.datanucleus.org/products/datanucleus/jpa/annotations.html

#### @id
> 각 엔티티를 구별할 수 있도록 식별 ID를 가지게 함
#### @Column
> 데이터베이스의 테이블을 구성할 때 인스턴스 변수가 칼럼이 되기 때문에 원한다면 칼럼명을  
> 별도로 지정하거나 칼럼의 사이즈, 제약 조건들을 추가하기 위해서 사용

| Attribute  | Type    | Description        | Default     |
| ---------- | ------- | ------------------ | ----------- |
| name       | String  | 칼럼 이름          |             |
| unique     | boolean | 유니크 여부        | true, false |
| nullable   | boolean | null 허용 여부     | true, false |
| insertable | boolean | insert 가능 여부   | true, false |
| updateable | boolean | 수정 가능 여부     | true, false |
| table      | String  | 테이블 이름        |             |
| length     | int     | 칼럼 사이즈        | 255         |
| precision  | int     | 소수 정밀도        | 0           |
| scale      | int     | 소수점 이하 자리수 | 0           |
  
#### @Table
> 클래스가 테이블이 되기 때문에 클래스의 선언부에 작성하여 테이블명을 어떻게 지정할지 결정  
> 만일 @Table이 지정되지 않으면 클래스 이름과 동일한 이름의 테이블이 생성됨

| Attribute         | Type                | Description             | Default |
| ----------------- | ------------------- | ----------------------- | ------- |
| name              | String              | 테이블 이름             |         |
| catalog           | String              | 테이블 카테고리         |         |
| schema            | String              | 테이블 스키마           |         |
| uniqueConstraints | UniqueConstraint[ ] | 칼럼값 유니크 제약 조건 |         |
| indexes           | index[ ]            | 인덱스 생성             |         |

#### @Entity
> 해당 클래스의 인스턴스들이 엔티티임을 명시

#### @GeneratedValue
##### strategy
###### AUTO
> 특정 데이터베이스에 맞게 자동으로 생성되는 방식
###### IDENTITY
> 기본키 생성 방식 자체를 데이터베이스에 위임하는 방식  
> 데이터베이스에 의존적인 방식, MySQL에서 주로 많이 사용
###### SEQUENCE
> 데이터베이스의 시퀀스를 이용해서 식별키 생성(오라클에서 사용)
###### TABLE
> 별도의 키를 생성해주는 채번 테이블(번호를 취할 목적으로 만든 테이블)을 이용하는 방식

##### generator
- @TableGenerator
- @SequenceGenerator

#### @CreationTimestamp
> 게시물 작성시간
#### @UpdateTimestamp
> 최종 수정 시간

### application.properties 옵션
```properties
# 스키마 생성(create)
spring.jpa.hibernate.ddl-auto=create
# DDL 생성 시 데이터베이스 고유의 기능을 사용하는가?
spring.jpa.generate-ddl=false
# 실행되는 SQL문을 보여줄 것인가?
spring.jpa.show-sql=true
# 데이터베이스는 무엇을 사용하는가?
spring.jpa.database=mysql
# 로그 레벨
logging.level.org.hibernate=info
# MySQL 상세 지정
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
```
### DDL 옵션
`spring.jpa.hibernate.ddl-auto` 옵션  
- create: 기존 테이블 삭제 후 다시 생성
- create-drop: create와 같으나 종료 시점에 테이블 DROP
- update: 변경된 부분만 반영
- validate: 엔티티와 테이블이 정상적으로 매핑되었는지만 확인
- none: 사용하지 않음

### CrudRepository 제공 메서드
| 메소드                                     | 설명                                    |
| ------------------------------------------ | --------------------------------------- |
| `log count()`                                | 모든 엔티티의 개수                      |
| `void delete(ID )`                           | 식별키를 통한 삭제                      |
| `void delete(Iterable<? extends T>)`         | 주어진 모든 엔티티 삭제                 |
| `void deleteAll( )`                          | 모든 엔티티 삭제                        |
| `boolean exists(ID)`                         | 식별키를 가진 엔티티가 존재하는지 확인  |
| `Iterable<T> findAll( )`                     | 모든 엔티티 목록                        |
| `Iterable<T> findAll(Iterable<ID>)`          | 해당 식별키를 가진 엔티티 목록 반환     |
| `T findOne(ID)`                              | 해당 식별키에 해당하는 단일 엔티티 반환 |
| `<S extends T>Iterable<S> save(Iterable<S>)` | 해당 엔티티들의 등록과 수정             |
| `<S extends T>S save(S entity)`              | 해당 엔티티의 등록과 수정               |

### Hibernate가 지원하는 Dialect
http://docs.jboss.org/hibernate/orm/current/javadocs/org/hibernate/dialect/package-summary.html