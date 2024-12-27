package com.simsimbookstore.apiserver.users.localuser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simsimbookstore.apiserver.users.localuser.dto.LocalUserRequestDto;
import com.simsimbookstore.apiserver.users.localuser.service.LocalUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(LocalUserController.class)
@ExtendWith(MockitoExtension.class)
class LocalUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocalUserService localUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/localUsers"))

    }
}