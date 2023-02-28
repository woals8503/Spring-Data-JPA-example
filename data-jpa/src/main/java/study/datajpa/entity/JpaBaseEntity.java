package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass   //속성을 내려서 테이블에 같이 쓸 수 있게 해주는 기능
public class JpaBaseEntity {

    @Column(updatable = false)  //create는 변경되지 않는다.
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    // Persist 저장하기 전 이벤트가 발생
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;  // 데이터를 넣어놔야 나중에 수정 편리
    }

    @PreUpdate  //변경될 때 실행
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
