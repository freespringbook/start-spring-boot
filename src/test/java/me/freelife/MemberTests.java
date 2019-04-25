package me.freelife;

import lombok.extern.java.Log;
import me.freelife.domain.Member;
import me.freelife.domain.MemberRole;
import me.freelife.persistence.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@Commit
public class MemberTests {

    @Autowired
    private MemberRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testInsert() {

        IntStream.rangeClosed(0, 100).forEach(i -> {

            Member member = new Member();
            member.setUid("user"+i);
            member.setUpw("pw"+i);
            member.setUname("사용자"+i);

            MemberRole role = new MemberRole();
            if(i <= 80)
                role.setRoleName("BASIC");
            else if(i <= 90)
                role.setRoleName("MANAGER");
            else
                role.setRoleName("ADMIN");
            member.setRoles(Arrays.asList(role));

            repo.save(member);
        });

    }

    @Test
    public void testRead() {
        Optional<Member> result = repo.findById("user85");
        result.ifPresent(member -> log.info("member"+member));
    }

    /**
     * 기존의 암호화 되지 않은 정보 업데이트
     */
    @Test
    public void testUpdateOldMember() {

        List<String> ids = new ArrayList<>();

        for (int i = 0; i <= 100; i++) {

            ids.add("user"+i);
        }

        repo.findAllById(ids).forEach(member -> {

            member.setUpw(passwordEncoder.encode(member.getUpw()));

            repo.save(member);
        });

    }

}
