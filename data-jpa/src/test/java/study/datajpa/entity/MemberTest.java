package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("member1", 10, teamA);
        Member memberB = new Member("member2", 20, teamA);
        Member memberC = new Member("member3", 30, teamB);
        Member memberD = new Member("member4", 40, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        //given
        Member member = new Member("member1");
        memberRepository.save(member);  // 이 시점에 PrePersist 발생

        Thread.sleep(100);
        member.setUsername("member2");
        //@PreUpdate
        em.flush();
        em.clear();

        //when

        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember cre = " + findMember.getCreatedDate());
        System.out.println("findMember upd = " + findMember.getLastModifiedDate());
        System.out.println("findMember upd = " + findMember.getCreatedBy());
        System.out.println("findMember upd = " + findMember.getLastModifiedBy());
    }
}