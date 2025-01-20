package com.simsimbookstore.apiserver.users.user.scheduled;

import com.simsimbookstore.apiserver.users.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserSchedulerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserScheduler userScheduler;

    @Test
    void checkDormantUser() {
        userScheduler.checkDormantUser();
        verify(userService, times(1)).updateDormantUserState(30);
    }
}