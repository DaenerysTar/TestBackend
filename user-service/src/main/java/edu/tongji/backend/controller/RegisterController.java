package edu.tongji.backend.controller;

import com.netflix.ribbon.proxy.annotation.Http;
import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.mapper.ProfileMapper;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.service.IUserService;
import edu.tongji.backend.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.tongji.backend.dto.RegisterDTO;
import edu.tongji.backend.util.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

import static edu.tongji.backend.util.RedisConstants.ADMIN_PERM_CODE;

@Slf4j
@RestController  //用于处理 HTTP 请求并返回 JSON 格式的数据
@RequestMapping("/api/register")  //对应的api路径
public class RegisterController {
    @Autowired  //自动装填接口的实现类
    IUserService userService;
    @PostMapping("/refresh")
    public ResponseEntity<Response<Boolean>> BrandNewUserProfile(@RequestBody User user){
//        UserDTO user1 = UserHolder.getUser();
//        user1.getUserId()
        if(user.getUserId()==null||user.getName()==null||user.getContact()==null){
            return new ResponseEntity<>(Response.fail("user id,name and contact should be all non-empty!"),HttpStatus.BAD_REQUEST);
        }
        User oldUser = userService.getById(user.getUserId());
        if(oldUser==null)
            return new ResponseEntity<>(Response.fail("The user id for administrator is invalid"),HttpStatus.NOT_FOUND);
        oldUser.setContact(user.getContact());
        oldUser.setName(user.getName());
        Boolean result = userService.updateById(oldUser);
        return new ResponseEntity<>(Response.success(result,"The user "+user.getUserId()+"'s profile has been updated successfully"),HttpStatus.OK);
    }
    /**
     * @apiNote
     * <li>the user can only unregister the account him/herself</li>
     * @since <a href="https://github.com/SEMedical/Backend/releases/tag/v2.0.0">2.0.0</a>
     * @return unregistration succeeded or not
     * @author <a href="https://github.com/VictorHuu">Victor Hu</a>
     */
    @PostMapping("/unregister")
    public ResponseEntity<Response<Boolean>> unregisterPatient(){
        UserDTO user= UserHolder.getUser();
        Boolean unregistered = userService.unregister(Integer.valueOf(user.getUserId()));
            return new ResponseEntity<>(Response.success(unregistered,"The unregisterPatient Function haven't been implemented yet"),
                    HttpStatus.OK);
    }


    /**
     * @apiNote
     * <ol>
     * <li>After 2024/1/7,register rules are updated!The password must contain at least one digit, one lowercase, one uppercase and one special character,the length should be between 8 and 16</li>
     * <li>After 2024/1/7,register rules are updated!The phone number must be 11 digits and it's the valid phone number in China mainland.</li>
     * <li>After 2024/1/7,register rules are updated!The name must be 2-10 characters and it can only contain either all Chinese characters or all English characters.</li>
     * </ol>
     * @param info consists of name,password,contact,gender,age,among which the password and contact is required.
     * @return <p>register as a patient succeeds or not</p>
     * <p>the failure cause:
     * <ol>
     *     <li>don't obey the rules above</li>
     *     <li>the contact has been registered</li>
     * </ol>
     * </p>
     * @throws NoSuchAlgorithmException
     */
    @PostMapping("/patient")  //对应的api路径
    public ResponseEntity<Response<Boolean>> registerPatient(@RequestBody RegisterDTO info) throws NoSuchAlgorithmException {
//        log.info(info);
        if (info.getContact() == null || info.getPassword() == null)  //如果请求中的内容不完整
        {
            return new ResponseEntity<>(Response.fail("手机号或密码为空"),HttpStatus.BAD_REQUEST);  //返回错误信息
        }
        //The password must contain at least one digit, one lowercase, one uppercase and one special character,the length should be between 8 and 16.
        if (!info.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$")) {
            return new ResponseEntity<>( Response.fail("After 2024/1/7,register rules are updated!" +
                    "The password must contain at least one digit, one lowercase, one uppercase and one special character,the length should be between 8 and 16"),
                    HttpStatus.BAD_REQUEST);
        }
        //The phone number must be 11 digits and it's the valid phone number in China mainland.
        if (!info.getContact().matches("^[1][3,4,5,7,8][0-9]{9}$")) {
            return new ResponseEntity<>( Response.fail("After 2024/1/7,register rules are updated!"+
                    "The phone number must be 11 digits and it's the valid phone number in China mainland."),
                    HttpStatus.BAD_REQUEST);
        }
        //The name must be 2-10 characters and it can only contain either all Chinese characters or all English characters.
        if (info.getName()==null||(!info.getName().matches("^[\\u4e00-\\u9fa5]{2,15}$") && !info.getName().matches("^[a-zA-Z]{2,50}$"))) {
            return new ResponseEntity<>(Response.fail("After 2024/1/7,register rules are updated!"+
                    "The name must be 2-10 characters and it can only contain either all Chinese characters or all English characters."),
                    HttpStatus.BAD_REQUEST);
        }
        log.info(info.toString());

        Integer result = userService.register(info.getName(), info.getPassword(), info.getContact(), info.getGender(), info.getAge());  //调用接口的register函数
        if (result == -1)  //如果返回的result为false
        {
            return new ResponseEntity<>(Response.fail("手机号已被注册"),HttpStatus.BAD_REQUEST);  //返回错误信息
        }
        return new ResponseEntity<>(Response.success(true, "注册成功"),HttpStatus.OK);
    }

    /**
     *
     * @since <a href="https://github.com/SEMedical/Backend/releases/tag/v1.0.0">1.0.0</a>
     * @deprecated please refer to OA Service:<em>/api/oa/addDoctor</em>
     * @see edu.tongji.backend.controller.RegisterController#registerPatient(RegisterDTO)
     * @author <a href="https://github.com/UltraTempest10">UltraTempest10</a>
     */
    @PostMapping("/doctor")  //对应的api路径
    public ResponseEntity<Response<Boolean>> registerDoctor(@RequestBody RegisterDTO info){
        return new ResponseEntity<>(Response.fail("The api has been out of date,please refer to the OA Service"),HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * @apiNote Only can be called by OA Service
     * @param user consists of user id,name,address,contact,password,role
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     */
    @PostMapping("/addUser")
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }
    /**
     *
     * <p>Description:remove a user ,<b>only can be called by oa service</b></p>
     * @since 2.2.0
     * @author <a href="https://github.com/rmEleven">rmEleven</a>
     */
    @PostMapping ("/rmUser")
    public void rmUser(@RequestParam("userId") Integer userId){
        userService.rmUser(userId);
    }
    @PostMapping ("/registerHelper")
    public Integer registerHelper(@RequestBody RegisterDTO registerDTO) throws NoSuchAlgorithmException {
        return userService.registerAdmin(registerDTO.getName(),registerDTO.getPassword(),registerDTO.getContact(),registerDTO.getGender(),registerDTO.getAge());
    }
}
