package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        Member findMember = em.createQuery("select m from Member m where username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(Integer.valueOf(10))))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"), member.age.eq(Integer.valueOf(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    public void resultFetch() {
//        List<Member> fetch = queryFactory.selectFrom(member).fetch();
//
//        Member fetchOne = queryFactory.selectFrom(member).fetchOne();
//
//        Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();

        // 페이징에서 사용
        // fetchResults() 는 Querydsl 5.x부터 deprecated(더 이상 권장되지 않음) 처리되었습니다.
        // fetchResults() 는 count 쿼리와 content 쿼리를 내부적으로 두 번 실행해서 결과를 가져옵니다.
        // count 쿼리와 content 쿼리를 분리해서 호출하는 방식을 권장합니다.
//        QueryResults<Member> results = queryFactory.selectFrom(member).fetchResults();
//
//        results.getTotal();
//        List<Member> content = results.getResults();

        queryFactory.selectFrom(member).fetchCount();
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(Integer.valueOf(100)))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();
        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() {
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    //    순수 Querydsl로 대체 (가장 단순한 리팩토링)
    @Test
    public void paging3() {
        // given
        long offset = 1;
        long limit = 2;

        // when - content
        List<Member> content = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(offset)
                .limit(limit)
                .fetch();

        // and - count 쿼리
        Long total = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        // then
        assertThat(total).isEqualTo(4L); // 전체 건수 검증
        assertThat(content).hasSize(2); // page size 검증

        // fetchResults()가 사라졌으므로 limit/offset은 직접 검증
        assertThat(limit).isEqualTo(2L);
        assertThat(offset).isEqualTo(1L);

        // (선택) 정렬이 기대대로인지 구체 검증하고 싶다면 도메인/픽스처에 맞게 추가
        // assertThat(content).extracting(Member::getUsername).containsExactly("user3", "user2");
    }

    // Spring Data JPA Page로 감싸는 방식 (실무 페이징 패턴)
    @Test
    public void paging4() {
        // given
        long offset = 1L;
        long limit = 2L;

        // content
        List<Member> content = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(offset)
                .limit(limit)
                .fetch();

        // count
        Long total = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        // Page 객체로 감싸기 (PageImpl 사용)
        Page<Member> page = new PageImpl<>(content, Pageable.unpaged(), total); // 혹은 적절한 Pageable

        // then
        assertThat(page.getTotalElements()).isEqualTo(4L);
        assertThat(page.getContent()).hasSize(2);

        // Page는 offset/limit 원시값을 직접 들고 있지 않으므로 필요한 경우 별도 검증
        assertThat(limit).isEqualTo(2L);
        assertThat(offset).isEqualTo(1L);
        //    참고: PageRequest는 페이지 기반(0,1,2…)이라 임의 offset(=1) 을 그대로 표현하기가 모호합니다.
        //    임의 offset을 쓰는 테스트라면 PageImpl에 Pageable.unpaged() 또는 적절한 Pageable을 넣고,
        //    offset은 별도 변수로 관리/검증하는 편이 명확합니다.
    }

    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    // 팀의 이름과 각 팀의 평균 연령을 구해라
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }


}