package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//프록시를 사용핧 때 private로 막아놓으면 프록시를 사용할 수 없어서 protect를 사용했다.
@ToString(of = {"id", "username", "age"})
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
public class Member extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "memberId")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null) changeTeam(team);
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

}
