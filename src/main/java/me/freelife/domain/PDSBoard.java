package me.freelife.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "files")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_pds")
@EqualsAndHashCode(of = "pid")
public class PDSBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
    private String pname;
    private String pwriter;

    @OneToMany(cascade= CascadeType.ALL)
    @JoinColumn(name="pdsno")
    private List<PDSFile> files;

}
