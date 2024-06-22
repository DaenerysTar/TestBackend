package edu.tongji.backend.service.impl;

import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.DoctorMapper;
import edu.tongji.backend.mapper.HospitalMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import java.util.stream.Stream;

@Epic("糖小智")
@Feature("AccountService")
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

    @Story("getAccountList")
    @Test
    @DisplayName("对GetAccountList方法进行简单的测试")
    void testGetAccountList() {
        List<DoctorInfoDTO> expectedList = Arrays.asList(new DoctorInfoDTO(), new DoctorInfoDTO());
        when(doctorMapper.getAccountList()).thenReturn(expectedList);

        List<DoctorInfoDTO> result = accountService.getAccountList();

        assertEquals(expectedList, result);
        verify(doctorMapper, times(1)).getAccountList();
    }

    @Story("addAccountSuccess")
    @Test
    @DisplayName("测试addAccount方法的正常情况")
    void testAddAccountSuccess() throws NoSuchAlgorithmException {
        Doctor doctor = new Doctor();
        doctor.setIdCard("123456789");
        String contact = "18139000985";
        String address = "Test Address";
        when(userClient2.getMaxUserId()).thenReturn(1);
        accountService.addAccount(doctor, contact, address);
        verify(userClient2, times(1)).addUser(any(User.class));
        verify(doctorMapper, times(1)).insert(doctor);
        assertNotNull(doctor.getDoctorId());
    }

    // 为addAccount方法的异常测试提供数据
    private static Stream<Arguments> provideInvalidData() {
        return Stream.of(
                Arguments.of(null, "18139000985", "Test Address", false), // doctor为null，其他有效
                Arguments.of(null, "123", "Test Address", true),    // doctor和contact不合法
                Arguments.of(null, "123",null, true)    // doctor,contact,address都不合法
        );
    }

    // 使用参数化方法，测试addAccount方法对不合法输入的响应
    @Story("addAccountWithInvalidInputs")
    @ParameterizedTest
    @MethodSource("provideInvalidData")
    @DisplayName("测试addAccount方法对不合法输入（异常输入）的响应")
    void testAddAccountWithInvalidInputs(Doctor doctor, String contact, String address, boolean expectException) {
        when(userClient2.getMaxUserId()).thenThrow(new RuntimeException("UserClient2 error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.addAccount(doctor, contact, address));
        assertEquals("UserClient2 error", exception.getMessage());
        verify(doctorMapper, times(0)).insert(doctor);
    }

    @Story("deleteAccountSuccess")
    @Test
    @DisplayName("测试deleteAccount方法的正常情况")
    void deleteAccountSuccess() {
        // 测试输入的doctorId正确的情况
        int doctorId  = 1;
        when(doctorMapper.deleteById(doctorId)).thenReturn(1);
        doNothing().when(userClient2).rmUser(doctorId);
        accountService.deleteAccount(doctorId);
        verify(doctorMapper, times(1)).deleteById(doctorId);
        verify(userClient2, times(1)).rmUser(doctorId);
    }

    @Story("testDeleteAccountWithIllegalId")
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

    @Story("testRepeatedIdCard")
    @Test
    @DisplayName("对RepeatedIdCard进行简单的测试")
    public void testRepeatedIdCard() {
        String idCard = "123456";
        when(doctorMapper.repeatedIdCard(idCard)).thenReturn(true);
        Boolean result = accountService.repeatedIdCard(idCard);
        assertTrue(result);
        verify(doctorMapper, times(1)).repeatedIdCard(idCard);
    }
}