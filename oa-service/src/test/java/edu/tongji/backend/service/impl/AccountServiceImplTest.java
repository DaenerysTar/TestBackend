package edu.tongji.backend.service.impl;

import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.DoctorMapper;
import edu.tongji.backend.mapper.HospitalMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private UserClient2 userClient2;

    @Mock
    private HospitalMapper hospitalMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp(){
        System.out.println("测试开始");
    }

    @AfterEach
    void tearDown(){
        System.out.println("测试结束！");
    }

    @Test
    void getAccountList() {
    }

    @Test
    void addAccount() {
    }

    @Test
    @DisplayName("测试deleteAccount方法的成功情况")
    void deleteAccountSuccess() {
        // 测试输入的doctorId正确的情况
        int doctorId  = 1;
        when(doctorMapper.deleteById(doctorId)).thenReturn(1);
        doNothing().when(userClient2).rmUser(doctorId);
        accountService.deleteAccount(doctorId);
        verify(doctorMapper, times(1)).deleteById(doctorId);
        verify(userClient2, times(1)).rmUser(doctorId);
    }

    @Test
    @DisplayName("测试deleteAccount方法的异常情况：非法ID")
    void testDeleteAccountWithIllegalId() {
        testDeleteAccountWithException(-1, "Delete error");
    }
    @Test
    @DisplayName("测试deleteAccount方法的异常情况：数据库中不存在的ID")
    void testDeleteAccountWithNonExistentId() {
        testDeleteAccountWithException(999, "Delete error");
    }
    private void testDeleteAccountWithException(int doctorId, String expectedExceptionMessage) {
        doThrow(new RuntimeException(expectedExceptionMessage)).when(doctorMapper).deleteById(doctorId);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.deleteAccount(doctorId),
                "Expected RuntimeException when attempting to delete with an invalid ID"
        );
        assertEquals(expectedExceptionMessage, exception.getMessage());

        verify(doctorMapper, times(1)).deleteById(doctorId);
        verify(userClient2, times(0)).rmUser(doctorId);
    }

    @Test
    void repeatedIdCard() {
    }

    @Test
    void updateAccount() {
    }
}