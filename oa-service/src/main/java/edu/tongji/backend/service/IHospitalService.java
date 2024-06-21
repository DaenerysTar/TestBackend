package edu.tongji.backend.service;

import edu.tongji.backend.entity.Hospital;

public interface IHospitalService {
    /**
     * 添加医院的抽象方法
     * @param hospital
     * @return java.lang.Integer
     */
    public Integer addHospital(Hospital hospital);
    /**
     * 删除医院的抽象方法
     * @param hospitalId
     */
    public void deleteHospital(int hospitalId);
}

