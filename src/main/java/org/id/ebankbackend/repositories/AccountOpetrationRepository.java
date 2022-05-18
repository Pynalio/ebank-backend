package org.id.ebankbackend.repositories;

import org.id.ebankbackend.entities.AccountOperation;
import org.id.ebankbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOpetrationRepository extends JpaRepository<AccountOperation,Long> {


}
