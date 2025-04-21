# SPRING ADVANCED




<details>
<summary style="font-size: 16px;"><strong>Lv 1. 코드 개선</strong></summary>

1. 코드 개선 퀴즈 - Early Return

![lv1-1.png](readme%2Flv1-1.png)

->

```java
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_MAIL);
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
                signupRequest.getEmail(),
                encodedPassword,
                userRole
        );
        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        return new SignupResponse(bearerToken);
    }
```

2. 리팩토링 퀴즈 - 불필요한 if-else 피하기

![lv1-2.png](readme%2Flv1-2.png)

->

```java

public String getTodayWeather() {
    ResponseEntity<WeatherDto[]> responseEntity =
            restTemplate.getForEntity(buildWeatherApiUri(), WeatherDto[].class);

    WeatherDto[] weatherArray = responseEntity.getBody();

    if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
        throw new CustomException(ErrorCode.FAIL_GET_WEATHER);
    }

    if (weatherArray == null || weatherArray.length == 0) {
        throw new CustomException(ErrorCode.NOT_FOUND_WEATHER);
    }
    //...생략
}
```

3. 코드 개선 퀴즈 - Validation

![lv1-3.png](readme%2Flv1-3.png)

->

```java
public class UserChangePasswordRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다."
    )
    private String newPassword;
}
```

```java
@PutMapping("/users") 컨트롤러에 @Valid 추가
```

</details>

<details>
<summary style="font-size: 16px;"><strong>Lv2. N+1 문제</strong></summary>

```java
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t ORDER BY t.modifiedAt DESC")
    @EntityGraph(attributePaths = {"user"})
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Todo> findById(@Param("todoId") Long todoId);

    int countById(Long todoId);
}
```
</details>

<details>
<summary style="font-size: 16px;"><strong>Lv3. 테스트코드 연습(+ Lv6. 테스트 커버리지)</strong></summary>

1. PasswordEncoderTest
2. CommentServiceTest
3. ManagerServiceTest
4. TodoServiceTest
5. UserServiceTest

![Lv3.png](readme%2FLv3.png)

![Lv6.png](readme%2FLv6.png)

</details>


<details>
<summary style="font-size: 16px;"><strong>Lv4. API 로깅</strong></summary>

Interceptor 구현
```java
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

    String requestUri = request.getRequestURI();
    Long userId = (Long) request.getAttribute("userId");
    String userRole = (String) request.getAttribute("userRole");

    log.info(
            "Admin API 호출 !! 시간 : {}\nURL : {}\nuserId : {}\nuserRole : {}"
            ,LocalDateTime.now(),requestUri,userId,userRole
    );

    return true;

}
```

```java
public void addInterceptors(InterceptorRegistry registry){
    registry.addInterceptor(adminApiInterceptor)
            .addPathPatterns("/admin/**");
}
```

</details>

<details>
<summary style="font-size: 16px;"><strong>Lv5. 위 제시된 기능 이외 ‘내’가 정의한 문제와 해결 과정</strong></summary>

1. [문제 인식 및 정의]  
- 프로젝트가 커질수록 예외처리는 다양하게 일어나게 되는데 여러곳에서 일어나는 예외처리를 확인하고 수정하기가 쉽지 않음

2. [ 해결방안 ]
- 예외처리를 GlobalExceptionHandler에서 CustomException으로 처리
> GlobalExceptionHandler, CustomException, ErrorCode(Enum) 사용  
> InvalidRequestException, ServerException, AuthException 삭제

3. [ 해결완료 ]

```java
@ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode.getStatus();

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.name());
        errorResponse.put("code", errorCode.getCode());
        errorResponse.put("message", errorCode.getMessage());
        errorResponse.put("time", errorCode.getLocalDateTime());

        return new ResponseEntity<>(errorResponse, status);
    }
```

```java
public enum ErrorCode {

    TEST_ERROR("TEST-001", HttpStatus.NOT_FOUND, "테스트 에러"),
    TEST_ERROR2("TEST-002", HttpStatus.NOT_FOUND, "테스트 에러"),

    //AUTH
    ALREADY_MAIL("AUTH-001", HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NOT_FOUND_EMAIL("AUTH-002", HttpStatus.NOT_FOUND, "가입되지 않은 유저입니다."),
    WRONG_PASSWORD("AUTH-003", HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다.")
//생략
}
```
예외처리 반환값

```json
{
    "code": "AUTH-001",
    "time": "2025-04-21T13:34:01.738003",
    "message": "이미 존재하는 이메일입니다.",
    "status": "BAD_REQUEST"
}
```

Test코드 또한 Enum 결과값에 맞게 수정

```java
    public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
    // given
    long todoId = 1;
    CommentSaveRequest request = new CommentSaveRequest("contents");
    AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

    given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
        commentService.saveComment(authUser, todoId, request);
    });

    // then
    assertEquals(ErrorCode.NOT_FOUND_TODO, exception.getErrorCode());
}
```
**결과**

1. 예외처리의 일관성 유지
- 모든 예외는 CustomException으로 처리되고, Enum의 예외 목록에서 같은 서식으로 반환된다.

2. 유지보수 향상
- 예외처리 로직이 분산되어있지 않아 추가, 수정, 삭제가 한곳에서 일어나기 때문에 편리하다.

3. 가독성 증가
- throw new RuntimeException, new IllegalArgumentException 등을 제거하고 CustomException(Errorce.XXX) 로 처리가능

4. 테스트 코드 편리성
- 테스트 코드에서 CustomException과 ErrorCode를 직접 비교하기때문에 검증에 편리함

</details>