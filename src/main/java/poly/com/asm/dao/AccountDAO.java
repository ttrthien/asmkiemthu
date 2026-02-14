package poly.com.asm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.com.asm.entity.Account;

public interface AccountDAO extends JpaRepository<Account, String> {
}