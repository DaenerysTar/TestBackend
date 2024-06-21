package edu.tongji.backend.clients;

import edu.tongji.backend.config.FeignConfig;
import edu.tongji.backend.dto.AdminDTO;
import edu.tongji.backend.dto.RegisterDTO;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.util.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserClient2 {
    /**
     * description:
     * @param user
     */
    @PostMapping(value = "/api/register/addUser")
    void addUser(User user);
    /**
     * description:
     * @param userId
     */
    @PostMapping (value = "/api/register/rmUser")
    void rmUser(@RequestParam("userId") Integer userId);
    /**
     * description:
     * @param contact
     * @return edu.tongji.backend.util.Response<java.lang.Boolean>
     */
    @GetMapping(value = "/api/login/repeatedContact")
    Response<Boolean> repeatedContact(@RequestParam("contact") String contact);
    /**
     * description:
     * @param
     * @return java.lang.Integer
     */
    @GetMapping(value = "/api/login/getMaxUserId")
    Integer getMaxUserId();
    /**
     * description:
     * @param registerDTO
     * @return java.lang.Integer
     */
    @PostMapping (value = "/api/register/registerHelper")
    Integer registerHelper(@RequestBody RegisterDTO registerDTO) throws NoSuchAlgorithmException;
    /**
     * description:
     * @param user
     * @return org.springframework.http.ResponseEntity<edu.tongji.backend.util.Response < java.lang.Boolean>>
     */
    @PostMapping(value = "/api/register/refresh")
    ResponseEntity<Response<Boolean>> BrandNewUserProfile(@RequestBody User user);
    /**
     * description:
     * @param userId
     * @return java.lang.String
     */
    @GetMapping(value = "/api/login/getContactForAdmin")
    String getContactForAdmin(@RequestParam("userId") String userId);
    /**
     * description:
     * @param admin
     * @return java.lang.Boolean
     */
    @PostMapping("/api/login/updateAdminInfo")
    Boolean updateAdminInfo(@RequestBody AdminDTO admin);
}
