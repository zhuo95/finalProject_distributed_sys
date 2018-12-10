package com.loveinDC.personalInfoMicroService.Controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loveinDC.personalInfoMicroService.Entity.PersonalInfo;
import com.loveinDC.personalInfoMicroService.Service.PersonalInfoService;

import RedisService.RedisSerialization;
import RedisService.UserSession;

@RestController
@RequestMapping("/api/personalInfo")
@CrossOrigin(origins = "*",allowCredentials = "true")
public class PersonalInfoController {
	@Autowired
	private PersonalInfoService personalInfoService;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@RequestMapping("/")
	public String index() {
		return "Hello from personalInfo micro service of Love in DC";
	}
	
	@RequestMapping("/create")
	public String createPersonalInfo(HttpServletRequest httpServletRequest) throws Exception{
		Integer id = getId(httpServletRequest);
		PersonalInfo personInfo= new PersonalInfo();
		personInfo.setId(id);
		personalInfoService.create(personInfo);
		try {
			return personalInfoService.findById(id).getId().toString();
		}catch(NullPointerException e) {
			throw new NullPointerException();
		}
	}
	
	@RequestMapping("/delete")
	public String deletePersonalInfo(HttpServletRequest httpServletRequest) throws Exception {
		Integer id = getId(httpServletRequest);
		int deleteResult = personalInfoService.delete(id);
		if(deleteResult == 1 ) return "Delete successed";
		else return "Delete failed";
	}
	
	@PostMapping("/update")
	public PersonalInfo updatePersonalInfo(HttpServletRequest httpServletRequest) throws Exception {	
		
		Integer id = getId(httpServletRequest);
		PersonalInfo personInfo = personalInfoService.findById(id);
		String firstName = httpServletRequest.getParameter("firstName");
		if (firstName != null) {
			personInfo.setFirstName(firstName);
		}
		String lastName = httpServletRequest.getParameter("lastName");
		if (lastName != null) {
			personInfo.setLastName(lastName);
		}
		String nickName = httpServletRequest.getParameter("nickName");
		if (nickName != null) {
			personInfo.setNickName(nickName);
		}
		String birthDate = httpServletRequest.getParameter("birthDate");
		if (birthDate != null && birthDate.length() == 10) {
			personInfo.setBirthDate(birthDate);
		}
		String gender = httpServletRequest.getParameter("gender");
		if (gender != null) {
			personInfo.setGender(gender.charAt(0));
		}
		String college = httpServletRequest.getParameter("college");
		if (college != null) {
			personInfo.setCollege(college);
		}
		String major = httpServletRequest.getParameter("major");
		if (major != null) {
			personInfo.setMajor(major);
		}
		String phoneNumber = httpServletRequest.getParameter("phoneNumber");
		if (phoneNumber != null) {
			personInfo.setPhoneNumber(phoneNumber);
		}
		return personalInfoService.update(personInfo);
	}
	
	@RequestMapping("/findSelf")
	public PersonalInfo findSelfPersonalInfo(HttpServletRequest httpServletRequest) throws Exception {
		Integer id = getId(httpServletRequest);
		return personalInfoService.findById(id);
	}
	
	@RequestMapping("/findAll")
	public List<PersonalInfo> findAllPersonalInfo() {
		return personalInfoService.findAll();
	}
	
	private Integer getId(HttpServletRequest httpServletRequest) throws Exception {
		Integer id = 0;
		//get user's cookies
			Cookie[] cookies = httpServletRequest.getCookies();
			String userRedisKey = "";
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("loveinDC_token")) {
					userRedisKey = "UserKey:" + cookie.getValue();
					break;
				}
			}
			//get user session from redis
			String serializedUser = "";
			if(stringRedisTemplate.hasKey(userRedisKey)) {
				serializedUser = stringRedisTemplate.opsForValue().get(userRedisKey);
			} else {
				throw new Exception();
			}
			
			UserSession userSession = RedisSerialization.antiSerialization(serializedUser, UserSession.class);
			
			//get user id
			if (userSession == null) {
				throw new Exception();
			} else {
				id = userSession.getId();
			}
		return id;
	}
}
