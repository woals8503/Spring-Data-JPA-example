package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
@EntityListeners(AuditingEntityListener.class)  // 이벤트를 기반으로 동작한다는 뜻
@MappedSuperclass   // 테이블에 반영
@Getter
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)  //변경은 x
    private LocalDateTime createdDate;

    @LastModifiedDate   // 마지막 수정일
    private LocalDateTime lastModifiedDate;

}
