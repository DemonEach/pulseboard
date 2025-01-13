package org.goblintelligence.pulseboard.services.auth.data.repository;

import org.goblintelligence.pulseboard.services.auth.data.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, Integer id);

    boolean existsByEmailAndIdNot(String email, Integer id);
}
