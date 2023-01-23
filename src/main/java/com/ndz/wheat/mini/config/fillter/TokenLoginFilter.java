package com.ndz.wheat.mini.config.fillter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndz.wheat.mini.common.bean.ApiResult;
import com.ndz.wheat.mini.common.enums.BizCodeEnum;
import com.ndz.wheat.mini.common.enums.StateEnum;
import com.ndz.wheat.mini.common.helper.JwtHelper;
import com.ndz.wheat.mini.config.security.CustomUser;
import com.ndz.wheat.mini.utils.ApiResultUtils;
import com.ndz.wheat.mini.vo.sys.LoginVO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 登录过滤器，继承UsernamePasswordAuthenticationFilter，对用户名密码进行登录校验
 * </p>
 *
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {


    /**
     *
     */
    public TokenLoginFilter(AuthenticationManager authenticationManager) {
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        //指定登录接口及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/system/index/login","POST"));
    }

    /**
     * 尝试认证
     *
     * @param req req
     * @param res res
     * @return {@link Authentication}
     * @throws AuthenticationException 身份验证异常
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            LoginVO loginVo = new ObjectMapper().readValue(req.getInputStream(), LoginVO.class);

            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 认证成功
     *
     * @param request  请求
     * @param response 响应
     * @param chain    链
     * @param auth     身份验证
     * @throws IOException      ioexception
     * @throws ServletException servlet异常
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        String token = JwtHelper.createToken(customUser.getSysUser().getId(), customUser.getSysUser().getUsername());

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        ApiResultUtils.out(response, new ApiResult<>(StateEnum.SUCCESS, map));
    }

    /**
     * 登录失败
     *
     * @param request  请求
     * @param response 响应
     * @param e        e
     * @throws IOException      ioexception
     * @throws ServletException servlet异常
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException e) throws IOException, ServletException {

        if(e.getCause() instanceof RuntimeException) {
            ApiResultUtils.out(response, new ApiResult<>(204, e.getMessage()));
        } else {
            ApiResultUtils.out(response, new ApiResult<>(BizCodeEnum.LOGIN_MOBLE_ERROR));
        }
    }
}
