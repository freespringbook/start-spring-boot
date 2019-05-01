package me.freelife;

import me.freelife.persistence.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Commit
public class ApplicationTests {

    @Autowired
    private MemberRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void contextLoads() {
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
