package cn.hegongda.weixin.controller;

import cn.hegongda.weixin.common.CheckUtils;
import cn.hegongda.weixin.common.CommonUtils;
import cn.hegongda.weixin.common.Result;
import cn.hegongda.weixin.pojo.Job;
import cn.hegongda.weixin.pojo.User;
import cn.hegongda.weixin.service.UserService;
import cn.hegongda.weixin.service.WService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService ;

    @Autowired
    private WService wService;

    /**
     *   进行配置
     * @param request : 请求
     * @param response ：响应
     * @return ： 解密后echostr
     */
    @ResponseBody
    @RequestMapping("/meet")
    public String show(HttpServletRequest request, HttpServletResponse response){
        System.out.println("ajaj");
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        if (CheckUtils.check(signature,timestamp,nonce)){
            System.out.println("接入成功");
            return echostr ;
        } else {
            System.out.println("fail");
            return "fail" ;
        }

    }


    /**
     *  接收消息，进行解析
     * @param request ： 请求
     * @param response ： 响应
     * @return ： 回复的消息
     */
    @PostMapping("/meet")
    public void reviceMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        request.setCharacterEncoding("utf-8");
        Map<String, String> message = CommonUtils.parseXml(request);
        // 解析响应并返回给客户端
        System.out.println(message);
        String xml = wService.parseMessage(message);
        ServletOutputStream os = response.getOutputStream();
        os.write(xml.getBytes());
    }


    @RequestMapping("/login")
    public ModelAndView login(String email, String wid){
        ModelAndView mv = new ModelAndView();
        Result result = userService.login(email, wid);
        if (!result.getCode().equals("200")) {
            mv.addObject("message",result.getMessage());
            mv.setViewName("forward:/WEB-INF/login.jsp?wid="+wid);
        } else {
            mv.setViewName("redirect:show?email="+email);
        }
        return mv;
    }

    @RequestMapping("/show")
    public ModelAndView show(String email){
        ModelAndView mv = new ModelAndView();
        User user = userService.findByEmail(email);
        mv.addObject("userinfo", user);
        mv.setViewName("userinfo");
        return mv ;
    }
}
