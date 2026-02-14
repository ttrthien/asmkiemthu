package poly.com.asm.service;

import poly.com.asm.entity.Account;
import java.util.List;

public interface AccountService {
    Account findById(String username);
    List<Account> findAll();
    Account create(Account account);
    Account update(Account account);
    void delete(String username);
    Account login(String username, String password);
}