package poly.com.asm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.com.asm.dao.AccountDAO;
import poly.com.asm.entity.Account;
import poly.com.asm.service.AccountService;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountDAO dao;

    @Override
    public Account findById(String username) {
        return dao.findById(username).orElse(null);
    }

    @Override
    public List<Account> findAll() {
        return dao.findAll();
    }

    @Override
    public Account create(Account account) {
        return dao.save(account);
    }

    @Override
    public Account update(Account account) {
        return dao.save(account);
    }

    @Override
    public void delete(String username) {
        dao.deleteById(username);
    }

    @Override
    public Account login(String username, String password) {
        Account user = dao.findById(username).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}