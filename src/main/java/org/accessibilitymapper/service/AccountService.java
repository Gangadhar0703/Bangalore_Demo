package org.accessibilitymapper.service;

import org.accessibilitymapper.model.Account;
import org.accessibilitymapper.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accounts;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accounts, PasswordEncoder passwordEncoder) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Account loadUserByUsername(String email) {
        return accounts.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "Account not found."));
    }

    @Transactional
    public Account register(String name, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        if (accounts.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("An account already exists for that email address.");
        }
        return accounts.save(new Account(name.trim(), normalizedEmail, passwordEncoder.encode(password), "USER"));
    }
}
