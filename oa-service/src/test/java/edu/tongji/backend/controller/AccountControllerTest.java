package edu.tongji.backend.controller;

import com.netflix.client.ClientException;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.service.IAccountService;
import edu.tongji.backend.service.IHospitalService;
import edu.tongji.backend.util.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static edu.tongji.backend.util.RedisConstants.LOGIN_TOKEN_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Epic("糖小智")
@Feature("AccountController")
@SpringBootTest
class AccountControllerTest {
    @Autowired
    AccountController accountController;
    private MockHttpSession session;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
    }

    @Feature("generateInvitationCode")
    @Test
    @DisplayName("测试生成邀请码，共包含3个测试用例")
    void GenerateInvitationCodeTestSuites() throws Exception {
        GenerateInvitationCodeTest(null);   // 测试非法输入
        GenerateInvitationCodeTest(2);      // 测试合法输入但该医院已有管理员
        GenerateInvitationCodeTest(4);      // 测试合法输入且该医院没有管理员
    }
    void GenerateInvitationCodeTest(Integer hospitalId) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/oa/GenInviteCode")
                .param("hospitalId", (hospitalId != null) ? hospitalId.toString() : "")
                .contentType("application/json")
                .header("Authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ).andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
    }

    @Feature("deleteAccount")
    @Test
    @DisplayName("测试删除医生账号，共包含三个测试用例")
    void testDeleteAccount() {
        // 测试非法输入
        Integer doctorId = -1;
        ResponseEntity<Response<String>> response = accountController.deleteAccount(doctorId);
        Assertions.assertEquals("The doctor account " + doctorId + " has been removed",
                Objects.requireNonNull(response.getBody()).getMessage());
        // 合法输入但不在数据库中
        doctorId = 888;
        response = accountController.deleteAccount(doctorId);
        Assertions.assertEquals("The doctor account " + doctorId + " has been removed",
                Objects.requireNonNull(response.getBody()).getMessage());
        // 合法输入且在数据库中
        doctorId = 202;
        response = accountController.deleteAccount(doctorId);
        Assertions.assertEquals("The doctor account " + doctorId + " has been removed",
                Objects.requireNonNull(response.getBody()).getMessage());
    }
}