package com.simsimbookstore.apiserver.users.user.scheduled;


import com.simsimbookstore.apiserver.users.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserScheduler {
    private final UserService userService;

    // 매일 자정마다
    @Scheduled(cron = "0 0 0 * * ?")

    // 매분마다
//    @Scheduled(cron = "0 * * * * ?")
    public void checkDormantUser() {
        userService.updateDormantUserState(30);
    }
}
