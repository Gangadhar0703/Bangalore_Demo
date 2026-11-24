package org.accessibilitymapper.web;

import org.accessibilitymapper.model.Account;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {
    @ModelAttribute("currentAccount")
    public Account currentAccount(@AuthenticationPrincipal Account account) {
        return account;
    }
}
