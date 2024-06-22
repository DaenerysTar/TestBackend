package edu.tongji.backend.service.impl;

import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.mapper.HospitalMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static reactor.core.Disposables.never;

@Epic("糖小智")
@Feature("HospitalService")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class HospitalServiceImplTest {
    @Mock
    private HospitalMapper hospitalMapper;

    @InjectMocks
    private HospitalServiceImpl hospitalService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        System.out.println("测试开始");
    }

    @AfterEach
    void tearDown() {
        System.out.println("测试结束！");
    }

    @Feature("addHospital_IdMinMinus")
    @Test         // 测试出了第一个需要修改的地方！bug！
    void addHospital_IdMinMinus() {
        // UT_002_001_001 id取min-（示例值：-2）
        when(hospitalMapper.getMaxId()).thenReturn(-2);
        assertThrows(RuntimeException.class, () -> hospitalService.addHospital(new Hospital()));
        // 验证 getMaxId() 被调用
        verify(hospitalMapper).getMaxId();
        // 由于抛出异常，insert() 不应该被调用
        verify(hospitalMapper, (VerificationMode) never()).insert(any(Hospital.class));
    }

    @Feature("addHospital_IdMin")
    @Test
    void addHospital_IdMin() {
        // UT_002_001_002 id取min（示例值：0）
        when(hospitalMapper.getMaxId()).thenReturn(0);
        Hospital hospital = new Hospital();
        hospitalService.addHospital(hospital);
        assertEquals(1, hospital.getHospitalId());
        // 验证 insert() 被调用
        verify(hospitalMapper).insert(hospital);
    }

    @Feature("addHospital_IdMinPlus")
    @Test
    void addHospital_IdMinPlus() {
        // UT_002_001_003 id取min+（示例值：1）
        when(hospitalMapper.getMaxId()).thenReturn(1);
        Hospital hospital = new Hospital();
        hospitalService.addHospital(hospital);
        assertEquals(2, hospital.getHospitalId());
        verify(hospitalMapper).insert(hospital);
    }

    @Feature("addHospital_IdMaxMinus")
    @Test
    void addHospital_IdMaxMinus() {
        // UT_002_001_005 id取max-（示例值：Integer.MAX_VALUE-2）
        when(hospitalMapper.getMaxId()).thenReturn(Integer.MAX_VALUE - 2);
        Hospital hospital = new Hospital();
        Integer result = hospitalService.addHospital(hospital);
        assertEquals(Integer.MAX_VALUE-2, (int) result);
        //verify(hospital).setHospitalId(Integer.MAX_VALUE);
        verify(hospitalMapper).insert(hospital);
    }

    @Feature("addHospital_IdMax")
    @Test
    void addHospital_IdMax() {
        // UT_002_001_006 id取max（示例值：Integer.MAX_VALUE-1）
        // 这种情况会导致整数溢出，所以测试应该预期异常
        when(hospitalMapper.getMaxId()).thenReturn(Integer.MAX_VALUE - 1);
        Hospital hospital = new Hospital();
        Integer result = hospitalService.addHospital(hospital);
        assertEquals(Integer.MAX_VALUE-1, (int) result);
        //verify(hospital).setHospitalId(Integer.MAX_VALUE);
        verify(hospitalMapper).insert(hospital);
    }

    @Feature("addHospital_IdMaxPlus")
    @Test
    void addHospital_IdMaxPlus() {
        // UT_002_001_007 id取max+（示例值：Integer.MAX_VALUE+1）
        // 这种情况不会导致整数溢出，但会超出int的范围，所以测试应该预期异常
        when(hospitalMapper.getMaxId()).thenReturn(Integer.MAX_VALUE);
        assertThrows(RuntimeException.class, () -> hospitalService.addHospital(new Hospital()));
    }

    @Feature("addHospital_DatabaseConnectionFailure")
    @Test
    void addHospital_DatabaseConnectionFailure() {
        // UT_002_001_008 数据库连接失败
        doThrow(new RuntimeException("Database connection failed")).when(hospitalMapper).getMaxId();
        assertThrows(RuntimeException.class, () -> hospitalService.addHospital(new Hospital()));
    }
    @Feature("deleteHospitalSuccess")
    @Test
    void deleteHospitalSuccess() {
        // 测试hospitalId合法且在数据库中，应该能够正常删除
        when(hospitalMapper.deleteById(13)).thenReturn(1);
        assertDoesNotThrow(() -> hospitalService.deleteHospital(13));
        verify(hospitalMapper, times(1)).deleteById(13);
    }

    @Feature("testDeleteHospitalNotFound")
    @Test
    void testDeleteHospitalNotFound() {
        // 测试hospitalId虽然合法，但却在数据库中不存在
        when(hospitalMapper.deleteById(589)).thenReturn(0);
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> hospitalService.deleteHospital(589));
        assertEquals("The Hospital 589 doesn't exist or has been removed earlier!", exception.getMessage());
        verify(hospitalMapper, times(1)).deleteById(589);
    }

    @Feature("testDeleteHospitalIllegal")
    @Test
    void testDeleteHospitalIllegal() {
        // 测试hospitalId不合法，hospitalId为-1的情况
        when(hospitalMapper.deleteById(-1)).thenReturn(0);
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> hospitalService.deleteHospital(-1));
        assertEquals("The Hospital -1 doesn't exist or has been removed earlier!", exception.getMessage());
        verify(hospitalMapper, times(1)).deleteById(-1);
    }
}