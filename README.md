# MGW-server

## 구조 예시
```text
com.mgw.server.activity
├── domain
│   ├── Activity.java               // 핵심 엔티티 및 비즈니스 로직 수행
│   ├── ActivityLike.java           // M:N 관계 엔티티 (좋아요 정보)
│   ├── Participant.java            // M:N 관계 엔티티 (참여자 상태 관리)
│   └── exception                   // 도메인 전용 예외 및 에러 코드
│       ├── ActivityDomainException.java
│       └── ActivityErrorCode.java
│
├── port                            // 데이터 영속성 계층 (Persistence)
│   ├── ActivityRepository.java     // 기본 CRUD 및 심플 쿼리 (JPA)
│   ├── ActivityQueryRepository.java // 복잡한 동적 조회 및 검색 (QueryDSL)
│   └── ParticipantRepository.java  // 참여 관계 데이터 처리
│
├── usecase                         // 기능 명세서 (Inbound Port)
│   ├── command                     // 데이터 상태 변경 로직 (6개 API 대응)
│   │   ├── CreateActivityUseCase.java
│   │   ├── ActivityLikeUseCase.java (Like/Unlike 포함)
│   │   └── ActivityParticipationUseCase.java (Join/Cancel/Accept 포함)
│   └── query                       // 데이터 조회 로직 (2개 API 대응)
│       ├── GetActivitiesUseCase.java
│       └── GetMyActivitiesUseCase.java
│
├── service                         // 유스케이스 인터페이스 실제 구현체
│   ├── ActivityCommandService.java // 명령 유스케이스 통합 구현 (@Transactional)
│   └── ActivityQueryService.java   // 조회 유스케이스 통합 구현 (@Transactional(readOnly=true))
│
├── controller                      // 외부 요청 수신 및 응답 반환
│   ├── ActivityController.java     // 순수 DTO 반환 (ResponseBodyAdvice가 공통 포맷 처리)
│   └── dto                         // 컨트롤러 전용 입출력 데이터 객체
│       ├── request/
│       └── response/
│
└── application                     // 도메인 외부 부가 기능
└── security
└── ActivityPermissionEvaluator.java // 권한 검증 전용 로직 (ABAC)
```
| 구조도 (Folder & File) | 계층별 역할 정의 (Responsibility) |
| :--- | :--- |
| **1. domain**<br>├── Activity.java<br>├── ActivityLike.java<br>├── Participant.java<br>└── exception/ | **핵심 비즈니스 모델 및 규칙**<br>* 비즈니스 로직을 포함합니다.<br>* 엔티티가 스스로 상태를 변경하고 검증하는 로직을 가집니다. |
| **2. port**<br>└── out/<br>├── ActivityRepository.java<br>└── ParticipantRepository.java | **외부 인프라 통신 관문**<br>* 데이터베이스(JPA)나 외부 시스템과의 통신 인터페이스입니다.<br>* **Repository**는 기본적인 CRUD 및 복잡한 조회를 수행합니다. |
| **3. usecase**<br>├── command/<br>│   └── CreateActivityUseCase.java<br>└── query/<br>    └── GetActivitiesUseCase.java | **기능 명세서 (Inbound Port)**<br>* 기획서의 요구사항(Activity 생성, 참여 등)을 인터페이스로 정의합니다.<br>* **Command**: 데이터 변경 작업<br>* **Query**: 데이터 조회 작업 |
| **4. service**<br>├── ActivityCommandService.java<br>└── ActivityQueryService.java | **비즈니스 흐름 제어 (Implementation)**<br>* 유스케이스 인터페이스를 실제로 구현하는 계층입니다.<br>* 트랜잭션을 관리하며 도메인 객체의 협력을 조율합니다. |
| **5. controller**<br>├── ActivityController.java<br>└── dto/ (request, response) | **외부 요청 엔드포인트**<br>* HTTP 요청을 받아 유스케이스로 전달합니다.<br>* 전용 DTO를 통해 외부 스펙 변경이 서비스에 영향을 주지 않도록 합니다. |

## 🏛️ Activity Domain Architecture Specification

### 1. 전체 구조도 및 계층별 명세

| 계층 (Folder) | 역할 및 책임 (Responsibility) | 상세 내용 |
| :--- | :--- | :--- |
| **1. domain** | 핵심 비즈니스 모델 | 데이터와 비즈니스 규칙을 소유하며, 스스로 상태를 검증하고 변경합니다. |
| **2. usecase** | 기능 명세 (Inbound Port) | 기획 요구사항을 인터페이스로 정의합니다. Command(변경)와 Query(조회)를 엄격히 분리합니다. |
| **3. service** | 비즈니스 로직 구현 | 유스케이스 인터페이스를 구현하며, 트랜잭션 관리 및 도메인 객체 간 흐름을 제어합니다. |
| **4. port** | 데이터 영속성 (Repository) | 단순 CRUD는 JPA(`Repository`)가, 복잡한 동적 조회는 QueryDSL(`QueryRepository`)이 담당합니다. |
| **5. controller** | 외부 인터페이스 및 검증 | HTTP 요청을 수신하여 유스케이스로 전달하고, 컨트롤러 전용 DTO를 관리합니다. |

### 2. 레이어별 구현 예시 (Code Reference)

### 2.1 Domain (Entity & Logic)

엔티티와 도메인 객체를 하나로 합쳐 실용성을 높이며, 비즈니스 검증 로직을 내부에 포함합니다.

```java
@Entity
@Getter
@NoArgsConstructor
public class Activity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int maxParticipants;

    // 도메인 정책 유효성 검사 및 비즈니스 로직
    public void validateJoinable() {
    if (this.currentParticipants >= this.maxParticipants) {
        throw new ActivityDomainException(ActivityErrorCode.FULL_CAPACITY);
    }
}
}
```

### 2.2 UseCase (Interface)

기획 명세서의 API 단위로 인터페이스를 정의합니다. 하나의 서비스 클래스가 여러 인터페이스를 구현할 수 있습니다.

```java
// usecase/command/JoinActivityUseCase.java
public interface JoinActivityUseCase {
    void join(Long activityId, Long memberId);
}

// usecase/command/AcceptActivityJoinUseCase.java
public interface AcceptActivityJoinUseCase {
    void accept(Long activityId, Long memberId);
}
```

### 2.3 Service (Implementation)

성격이 같은 명령(Command) 유스케이스들을 하나의 서비스에서 처리하여 관리 포인트를 줄입니다.

```java
@Service
@Transactional
@RequiredArgsConstructor
public class ActivityCommandService implements JoinActivityUseCase {

    private final ActivityRepository activityRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public void join(Long activityId, Long memberId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ActivityDomainException(ActivityErrorCode.ACTIVITY_NOT_FOUND));

        // 도메인 내부 규칙 검증 (인원 초과 등)
        activity.validateJoinable(); 

        // 중복 참여 검증
        if (participantRepository.existsByActivityIdAndMemberId(activityId, memberId)) {
            throw new ActivityDomainException(ActivityErrorCode.ALREADY_PARTICIPATED);
        }

        Participant participant = Participant.create(activity, memberId);
        participantRepository.save(participant);
    }
}
```

### 2.4 Port (Persistence)

단순 기본 기능과 복잡한 조회 기능을 물리적으로 분리하여 유지보수성을 확보합니다.

```java
// port/ActivityRepository.java (JPA)
public interface ActivityRepository extends JpaRepository<Activity, Long> { }

// port/ActivityQueryRepository.java (QueryDSL)
@Repository
@RequiredArgsConstructor
public class ActivityQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<Activity> findAllByCondition(ActivitySearchQuery query, Pageable pageable) {
        // QueryDSL을 이용한 복잡한 동적 쿼리 및 페이징 구현
        return ...
    }
}
```

### 2.5 Controller (With ResponseBodyAdvice)

`ResponseBodyAdvice`를 활용하여 공통 응답 포맷(Success/Error Wrapper)을 전역에서 처리합니다. 이에 따라 컨트롤러는 `ResponseEntity`나 별도의 `ApiResponse` 래퍼 클래스를 명시적으로 반환하지 않고 **순수 데이터(DTO)**만 반환합니다.

```java
// controller/ActivityController.java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final JoinActivityUseCase joinActivityUseCase;
    private final GetActivitiesUseCase getActivitiesUseCase;

    // 1. Command: 성공 시 결과 데이터가 없다면 void 반환 (Advice가 200 OK 공통 응답 생성)
    @PostMapping("/{activityId}/join")
    public void join(@PathVariable Long activityId) {
        joinActivityUseCase.join(activityId, getCurrentMemberId());
    }

    // 2. Query: 순수 DTO 반환 (Advice가 자동으로 { "isSuccess": true, "result": { ... } } 형태로 래핑)
    @GetMapping("/{activityId}")
    public ActivityResponse getDetail(@PathVariable Long activityId) {
        return getActivitiesUseCase.getDetail(activityId);
    }
}
```