package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController // 객체로 반환
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    //페이징 처리  /members?page=1&size=3&sort=id,desc    1페이지에서 3개만 불러와라
    //default는 20개인데 10개로 바꾸고 싶다면 yml에 가서 추가
    //@PageableDefault는 이 메소드에만 적용시키고 싶다 할때 사용한다.
    //@Qualifier를 통하여 두개의 페이징 처리도 가능하다.
    //MemberDto::new   파라미터를 멤버로 설정하여 생성
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {   //page리퀘스트 객체를 생성해서 값을 인젝션 해준다.
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    //애플리케이션 로딩 시점시 자동으로 생성시켜주는 것
    @PostConstruct
    public void init() {
        for(int i=0; i< 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
