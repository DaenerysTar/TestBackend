package edu.tongji.backend.service;

import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface IAccountService {
    /**
     * 获取医生列表
     * @param
     * @return java.util.List<edu.tongji.backend.dto.DoctorInfoDTO>
     */
    public List<DoctorInfoDTO> getAccountList();
    /**
     * description:
     * @param doctor
     * @param contact
     * @param address
     * @return java.lang.Integer
     */
    public Integer addAccount(Doctor doctor, String contact, String address) throws NoSuchAlgorithmException;
    /**
     * 删除医生账号
     * @param doctorId
     */
    public void deleteAccount(int doctorId);
    /**
     * description:
     * @param idCard
     * @return java.lang.Boolean
     */
    Boolean repeatedIdCard(String idCard);
    /**
     * description:
     * @param doctor
     * @return java.lang.Boolean
     */
    Boolean updateAccount(Doctor doctor);
}

