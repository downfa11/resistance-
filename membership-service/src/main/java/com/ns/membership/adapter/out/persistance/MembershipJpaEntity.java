package com.ns.membership.adapter.out.persistance;

import com.ns.membership.domain.userData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


@Entity
@Table(name ="membership")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipJpaEntity {

    @Id
    @GeneratedValue
    private Long membershipId;

    private String name;
    private String address;
    private String email;

    private boolean isValid;

    @ElementCollection
    private Set<Long> friends;

    @ElementCollection
    private Set<Long> wantedFriends;

    private String refreshToken;

    public MembershipJpaEntity(String name, String address, String email, boolean isValid, Set<Long> friends, Set<Long> wantedFriends, String refreshToken) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.isValid = isValid;
        this.friends = friends;
        this.wantedFriends = wantedFriends;
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "MembershipJpaEntity{" +
                "membershipId=" + membershipId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", isValid=" + isValid +
                ", friends=" + friends +
                ", wantedFriends=" + wantedFriends +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
