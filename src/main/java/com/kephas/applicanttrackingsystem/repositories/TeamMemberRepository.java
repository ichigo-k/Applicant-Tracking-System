package com.kephas.applicanttrackingsystem.repositories;

import com.kephas.applicanttrackingsystem.entities.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {

}
