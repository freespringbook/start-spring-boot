package me.freelife.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString(exclude = "board")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_free_replies", indexes = {@Index(unique = false, columnList = "board_bno")})
@EqualsAndHashCode(of = "rno")
public class FreeBoardReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;
    private String reply;
    private String replayer;

    @ManyToOne
    private FreeBoard board;

    @CreationTimestamp
    private Timestamp replydate;

    @UpdateTimestamp
    private Timestamp updatedate;
}
