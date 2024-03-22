package com.ns.membership.application.port.out;

import com.ns.membership.adapter.out.persistance.MembershipJpaEntity;
import com.ns.membership.domain.Membership;

public interface FindMembershipPort {

    MembershipJpaEntity findMembership(
            Membership.MembershipId membershipId
    );
}
