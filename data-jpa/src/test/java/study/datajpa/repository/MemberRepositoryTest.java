package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("memberA");
        Member member2 = new Member("memberB");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member!!!!!!!!!");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
        //then
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void namedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
    
        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMember = " + findMember);
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        int age = 10;
        //스프링 데이터 JPA는 페이지를 0부터 시작
        //0페이지에서 3개 가져와
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "username"));

        //when
        //totalCount는 필요가 없다
        //반환 타입을 Page로 받으면 자기 혼자서 totalCount쿼리까지 같이 실행된다.
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        
        //map으로 DTO로 감싸서 내보내는 작업
        page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        /**
          Page가 아니라 Slice라면 내가 만약 3개를 가져오고 싶다면 1개 더 조회한다.
          1개가 더 있으면 다음 페이지가 있는지 없는지 여부를 판단할 수 있다.
          그리고 totalCount가 조회가 되지 않는다.
          List도 가능하다.
          주의할 점은 totalCount가 많으면 성능이 안나올 수 있다.
          그리고 DTO로 변환하여 넘기는 것이 중요하다.
         **/

        //페이지에서 내용을 꺼내고 싶다면 getContent() 사용
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();   //totalCount랑 같다,

        assertThat(content.size()).isEqualTo(3);           // 한페이지 안에 몇개의 내용이 들어있는지?
        assertThat(page.getTotalElements()).isEqualTo(6);  // 전체 내용이 몇개인지?
        assertThat(page.getNumber()).isEqualTo(0);         // 페이지 번호가 몇번인지?
        assertThat(page.getTotalPages()).isEqualTo(2);     // 페이지 수가 몇개인지?
        assertThat(page.isFirst()).isTrue();                       // 페이지가 첫번째 페이지인지?
        assertThat(page.hasNext()).isTrue();                       // 다음 페이지가 있는지?
    }

    //스프링 데이터 JPA로 연봉의 값을 한번의 쿼리로 바꾸는 작업
    @Test
    public void bulkUpdate() {
        //기본적으로 jpql이 실행되기 전 DB에 먼저 보낸다.
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        //DB에는 반영이 됬겠지만 영속성 컨텍스트에는 아직 반영이 되지 않은 상태이다.
        int resultCount = memberRepository.bulkAgePlus(20);

        // 기본적으로는 em.flush, clear를 하여 데이터를 방출 시키는게 맞다.
        // 하지만 인터페이스에서 clearAutomatically = true를 Modify에 명시해 주면 자동으로 이 기능을 수행한다.

        //벌크연산 시 주의해야할 점
        /**
         스프링 데이터 JPA는 JPA가 관리하지 않기 때문에 도중 값이 필요하다거나 그럴 경우
         em.flush로 DB에 반영하는 작업이 필요하다.
         **/
        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0);
        System.out.println("member = " + member.getAge());  

        assertThat(resultCount).isEqualTo(3);
    }

    //fetch조인의 장점과 EntityGraph 적용
    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA 참조
        //member2 -> teamB 참조

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getClass());
            System.out.println("member Team = " + member.getTeam().getName());
        }
    }

    // Hint & Lock 더티 체킹
    @Test
    public void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();
        // 영속성 컨텍스트가 비어졌기 때문에 밑으로는 무조건 DB에서 조회

        //when
        //디비에서 가져왔기 때문에 영속성 컨텍스트엔 초기값 snapShot의 기록이 존재하지 않음
        Member findMember = memberRepository.findById(member1.getId()).get();
        //그렇기 때문에 더디체킹이 발생하지 않는다.
        findMember.setUsername("member2");

        em.flush();
    }

    //사용자 정의 리포지토리 ( 국비때 쓰던 방법 )
    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }
}