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

@FeignClient(name="user-service",configuration = FeignConfig.class)
public interface UserClient2 {
    @PostMapping(value="/api/register/addUser")
    void addUser(User user);
    @PostMapping (value="/api/register/rmUser")
    void rmUser(@RequestParam("userId") Integer userId);
    @GetMapping(value="/api/login/repeatedContact")
    Response<Boolean> repeatedContact(@RequestParam("contact") String contact);
    @GetMapping(value="/api/login/getMaxUserId")
    Integer getMaxUserId();
    @PostMapping (value="/api/register/registerHelper")
    Integer registerHelper(@RequestBody RegisterDTO registerDTO) throws NoSuchAlgorithmException;
    @PostMapping(value="/api/register/refresh")
    ResponseEntity<Response<Boolean>> BrandNewUserProfile(@RequestBody User user);
    @GetMapping(value="/api/login/getContactForAdmin")
    String getContactForAdmin(@RequestParam("userId") String userId);
    @PostMapping("/api/login/updateAdminInfo")
    Boolean updateAdminInfo(@RequestBody AdminDTO admin);
}
