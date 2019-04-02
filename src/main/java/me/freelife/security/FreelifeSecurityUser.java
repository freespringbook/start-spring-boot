package me.freelife.security;

import lombok.Getter;
import lombok.Setter;
import me.freelife.domain.Member;
import me.freelife.domain.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class FreelifeSecurityUser extends User {

    private static final String ROLE_PREFIX = "ROLE_";

    private Member member;

    public FreelifeSecurityUser(Member member) {

        super(member.getUid(), member.getUpw(), makeGrantedAuthority(member.getRoles()));

    }

    private static Collection<? extends GrantedAuthority> makeGrantedAuthority(List<MemberRole> roles) {

        List<GrantedAuthority> list = new ArrayList<>();

        roles.forEach(role -> list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getRoleName())));
        return list;
    }
}
