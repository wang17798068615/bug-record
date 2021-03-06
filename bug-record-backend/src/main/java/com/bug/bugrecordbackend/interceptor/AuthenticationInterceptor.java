package com.bug.bugrecordbackend.interceptor;

import com.bug.bugrecordbackend.dto.common.ResponseData;
import com.bug.bugrecordbackend.util.SessionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bug.bugrecordbackend.annotation.UserRight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    SessionUtil sessionUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        UserRight userRight = handlerMethod.getMethodAnnotation(UserRight.class);
        if(userRight == null){
            Class clazz = handlerMethod.getBean().getClass();
            userRight = (UserRight) clazz.getAnnotation(UserRight.class);
        }
        if(userRight == null){
            return true;
        }

        SessionUtil.CurrentUser currentUser = sessionUtil.getCurrentUser();
        if(currentUser!=null){
            List<String> needRoles = Arrays.asList(userRight.roles());
            List<String> roles = currentUser.getUserEntity().getRoles();
            if(roles ==null || roles.stream().filter(item->needRoles.contains(item)).count()<=0){
                ResponseData responseData = new ResponseData();
                responseData.setCode(403);
                responseData.setMessage("拒绝访问，未被授权！");
                String json = new ObjectMapper().writeValueAsString(responseData);
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(json);
                return false;
            }
        }else{
            ResponseData responseData = new ResponseData();
            responseData.setCode(401);
            responseData.setMessage("未授权，请登录后再调用！");
            String json = new ObjectMapper().writeValueAsString(responseData);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(json);
            return false;
        }

        return true;
    }
}
