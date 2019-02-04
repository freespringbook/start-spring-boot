package me.freelife;

import lombok.extern.java.Log;
import me.freelife.domain.Member;
import me.freelife.domain.Profile;
import me.freelife.presistence.MemberRepository;
import me.freelife.presistence.ProfileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
@Commit // 테스트 결과 commit
public class ProfileTests {

    @Autowired
    MemberRepository memberRepo;

    @Autowired
    ProfileRepository profileRepo;

    @Test
    public void 더미데이터_생성() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Member member = Member.builder()
                    .uid("user" + i)
                    .upw("pw" + i)
                    .uname("사용자" + i)
                    .build();
            memberRepo.save(member);
        });
    }

    @Test
    public void 특정회원_프로필데이터_생성() {
        Member member = Member.builder()
                .uid("user1")
                .build();
        IntStream.range(1, 5).forEach(i -> {
            Profile profile = Profile.builder()
                    .fname("face"+i+".jpg")
                    .build();

            if(i == 1) {
                profile.setCurrent(true);
            }
            profile.setMember(member);
            profileRepo.save(profile);
        });
    }

    @Test
    public void FETCH_JOIN_테스트1() {
        List<Object[]> result = memberRepo.getMemberWithProfileCount("user1");
        result.forEach(arr -> System.out.println(Arrays.toString(arr)));
    }

    @Test
    public void FETCH_JOIN_테스트2() {
        List<Object[]> result = memberRepo.getMemberWithProfile("user1");
        result.forEach(arr -> System.out.println(Arrays.toString(arr)));
    }
}
