package me.freelife;

import lombok.extern.java.Log;
import me.freelife.domain.Member;
import me.freelife.domain.MemberRole;
import me.freelife.persistence.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@Commit
public class MemberTests {

    @Autowired
    private MemberRepository repo;

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
}
