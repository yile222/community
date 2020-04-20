package com.community.community1.controller;

import com.community.community1.DTO.AccessTokenDTO;
import com.community.community1.DTO.GithubUser;
import com.community.community1.mapper.UserMapper;
import com.community.community1.model.User;
import com.community.community1.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.setClient_id}")
    private String Client_id;
    @Value("${github.setClient_secret}")
    private String Client_secret;
    @Value("${github.setRedirect_uri}")
    private String Redirect_uri;
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(Client_id);
        accessTokenDTO.setClient_secret(Client_secret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(Redirect_uri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubProviderUser = githubProvider.getUser(accessToken);
        //System.out.println(user.getName());
        if (githubProviderUser!=null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubProviderUser.getName());
            user.setAccountID(String.valueOf(githubProviderUser.getId()));
            user.setGmtCreated(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            //说明登陆成功，写cookie和session
            //request.getSession().setAttribute("user",githubProviderUser);
            return "redirect:/";
        }else {
            //说明登陆失败
            return "redirect:/";
        }

    }
}
