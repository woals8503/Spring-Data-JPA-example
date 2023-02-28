package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

// 스프링 데이터 JPA를 이용한 등록, 수정 시간 반영
@EntityListeners(AuditingEntityListener.class)  // 이벤트를 기반으로 동작한다는 뜻
@MappedSuperclass   // 테이블에 반영
@Getter
public class BaseEntity extends BaseTimeEntity{

    //값을 넣으려면 main 컨트롤러에서 auditorProvider 메소드 Bean등록해야한다.
    // 등록되거나 수정될 때마다 auditorProvider를 호출해서 결과물을 꺼내간다.
    //등록자
    @CreatedBy
    @Column(updatable = false)  //등록자는 수정 불가하게 설정
    private String createdBy;

    //수정자
    @LastModifiedBy
    private String lastModifiedBy;
}
