package org.batuhankertmen.passwordmanager.logininformation;

import org.batuhankertmen.passwordmanager.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginInformationRepository extends JpaRepository<User, String> {
}
