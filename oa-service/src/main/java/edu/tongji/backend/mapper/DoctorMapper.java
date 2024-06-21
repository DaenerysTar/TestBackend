package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    /**
     * 获取医生列表
     * @return java.util.List<edu.tongji.backend.dto.DoctorInfoDTO>
     */
    @Select("SELECT d.doctor_id, d.id_card, d.department, d.title, d.photo_path, u.name, u.contact, d.state "
            + "FROM doctor d "
            + "JOIN user u ON d.doctor_id = u.user_id;")
    List<DoctorInfoDTO> getAccountList();
    /**
     * 更新医生信息
     * @param doctorId
     * @param idCard
     * @param department
     * @param title
     * @param photoPath
     * @return java.lang.Boolean
     */
    @Update("UPDATE doctor SET id_card= #{idCard},department=#{department},title=#{title},photo_path=#{photoPath}"
            + " where doctor_id=#{doctorId};")
    Boolean updateDoctor(Integer doctorId, String idCard, String department, String title, String photoPath);
    /**
     * description:
     * @param idCard
     * @return java.lang.Boolean
     */
    @Select("SELECT count(*)>0 from doctor where id_card= #{ idCard };")
    Boolean repeatedIdCard(String idCard);

//    @Insert("INSERT INTO doctor(doctor_id, hospital_id, id_card, department, title, photo_path)" +
//            "VALUES(#{doctorId}, #{hospitalId}, #{idCard}, #{department}, #{title}, #{photoPath});")
//    void addAccount(Doctor doctor);
//
//    @Delete("DELETE FROM doctor WHERE doctor_id = #{doctorId};")
//    void deleteAccount(int doctorId);
}
