package me.freelife.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_members")
@EqualsAndHashCode(of = "uid")
public class Member {
    @Id
    private String uid;
    private String upw;
    private String uname;
}
