package me.freelife.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString(exclude = "member")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_profile")
@EqualsAndHashCode(of = "fno")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fno;

    private String fname;

    private boolean current;

    @ManyToOne
    private Member member;
}
