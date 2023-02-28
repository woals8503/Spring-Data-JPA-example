package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

//  @Query(name = "Member.findByUsername")  생략가능
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query(value = "select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);
    
    List<Member> findListByUsername(String username);   //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건


    // join해서 카운트 수를 가져오는 것이 아닌 카운트는 단지 Member 갯수만 가져와 분할하여 쿼리를 만들어 최적화 하는 방법
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);


    // 순수 JPA가 아닌 스프링 데이터 JPA로 연봉값을 한번에 올리는 방법
    @Modifying(clearAutomatically = true)  // 변경한다고 명시적으로 추가해야함.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // Member를 조회할 때 연관된 팀을 한쿼리에 조회
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // EntityGraph로 fetch 조인의 역할을 대신한다.
    // 장점 - jpql 쿼리를 작성하지 않아도 된다.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    
    // jpql도 작성하면서 fetch join기능도 사용하고 싶을 경우 둘다 적어주는 방법
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 스프링 데이터 JPA의 관례를 따른 메소드와 EntityGraph의 조합
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // 더티체킹이 감지되지 않음
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

}
