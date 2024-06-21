package edu.tongji.backend.service.impl;

import edu.tongji.backend.entity.Hospital;
import edu.tongji.backend.mapper.HospitalMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static reactor.core.Disposables.never;

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

    @Test
    void addHospital_IdMinPlus() {
        // UT_002_001_003 id取min+（示例值：1）
        when(hospitalMapper.getMaxId()).thenReturn(1);
        Hospital hospital = new Hospital();
        hospitalService.addHospital(hospital);
        assertEquals(2, hospital.getHospitalId());
        verify(hospitalMapper).insert(hospital);
    }

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

    @Test
    void addHospital_IdMaxPlus() {
        // UT_002_001_007 id取max+（示例值：Integer.MAX_VALUE+1）
        // 这种情况不会导致整数溢出，但会超出int的范围，所以测试应该预期异常
        when(hospitalMapper.getMaxId()).thenReturn(Integer.MAX_VALUE);
        assertThrows(RuntimeException.class, () -> hospitalService.addHospital(new Hospital()));
    }

    @Test
    void addHospital_DatabaseConnectionFailure() {
        // UT_002_001_008 数据库连接失败
        doThrow(new RuntimeException("Database connection failed")).when(hospitalMapper).getMaxId();
        assertThrows(RuntimeException.class, () -> hospitalService.addHospital(new Hospital()));
    }
    @Test
    void deleteHospital() {
    }
}