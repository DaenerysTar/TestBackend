package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface HospitalMapper extends BaseMapper<Hospital> {
    /**
     * 选取最大的医院ID
     * @return java.lang.Integer
     */
    @Select("SELECT MAX(hospital_id) FROM hospital;")
    Integer getMaxId();
    /**
     * 检查是否已有重复医院信息
     * @param contact
     * @param name
     * @param address
     * @return java.lang.Boolean
     */
    @Select("SELECT EXISTS(SELECT * from hospital where hospital_phone=CONCAT(\"\",#{ contact } ,\"\")"
            + "or hospital_name=CONCAT(\"\", #{ name },\"\") or address=CONCAT(\"\",#{ address },\"\") )")
    Boolean InfoRepeated(String contact, String name, String address);
    /**
     * 检查医院是否有管理员，若有的话返回管理员的ID
     * @param hospitalId
     * @return java.lang.String
     */
    @Select("SELECT admin_id from hospital where hospital_id=#{hospitalId};")
    String havaAdministrator(String hospitalId);
    /**
     * 为医院设置管理员
     * @param hospitalId
     * @param adminId
     * @return java.lang.Boolean
     */
    @Update("UPDATE hospital set adminId=#{adminId} where hospital_id=#{hospitalId};")
    Boolean setAdministrator(String hospitalId, String adminId);
}
