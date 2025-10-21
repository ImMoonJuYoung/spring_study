package study.querydsl.controller;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    private final initMemberService initMemberService;

    /*
    @Transactional을 @PostConstruct 메서드에 직접 붙이지 않는 이유는,
    스프링의 트랜잭션 AOP 프록시가 아직 완전히 초기화되지 않은 시점에 @PostConstruct가 실행되기 때문
    따라서 트랜잭션이 적용되지 않고 단순한 메서드 호출로 처리되어 영속성 컨텍스트가 정상적으로 작동하지 않음
     */
    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class initMemberService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);
            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, selectedTeam));
            }
        }

    }
}
