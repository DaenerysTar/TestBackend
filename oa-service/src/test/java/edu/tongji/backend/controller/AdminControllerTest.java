package edu.tongji.backend.controller;

import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.AdminDTO;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.util.Response;
import edu.tongji.backend.util.UserHolder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static edu.tongji.backend.util.RedisConstants.LOGIN_TOKEN_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Mock
    private UserHolder userHolder;
    @Mock
    private UserClient2 userClient2;
    AdminController adminController;
    private MockHttpSession session;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @BeforeEach
    void setUp() {
        System.out.println("测试开始");
    }
    @AfterEach
    void tearDown() {
        System.out.println("测试结束");
    }
    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.session = new MockHttpSession();
    }
    @Test
    @DisplayName("测试更新管理员信息")
    void updateAdminInfoTestSuits () throws Exception {
        updateAdminInfoTest("管理员","15893240414");   // name与contact都正确
        updateAdminInfoTest("ab管","15893240414");    // name不正确，contact正确
        updateAdminInfoTest("管理员","123456");        // name正确，contact不正确
        updateAdminInfoTest("a","123456");         // name和contact都不正确
    }
    void updateAdminInfoTest(String name,String contact) throws Exception {
        String token="srtbm,glb15vx15vz0cs15v15v1";
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
        Map<String,String> maps=new HashMap<>();
        maps.put("name","momo");
        maps.put("id","0");
        maps.put("role","admin");
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,maps);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/oa/editAdminInfo")
                .param("name", name) // 添加name参数
                .param("contact", contact) // 添加contact参数
                .contentType("application/json")
                .header("Authorization",token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
        ) .andExpect(status().isOk());
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY+token);
    }
}