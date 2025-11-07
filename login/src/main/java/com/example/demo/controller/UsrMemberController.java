package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.Member;
import com.example.demo.dto.ResultData;
import com.example.demo.service.MemberService;
import com.example.demo.util.Util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsrMemberController {
	
	private MemberService memberService;
	
	public UsrMemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@GetMapping("/usr/member/join")
	public String join() {
		return "usr/member/join";
	}
	@GetMapping("/usr/member/login")
	public String login() {
		return "usr/member/login";
	}
	
	@PostMapping("/usr/member/doJoin")
	@ResponseBody
	public String doJoin(String loginId, String loginPw, String loginPwChk, String name) {

		this.memberService.joinMember(loginId, loginPw, name);
		
		return Util.jsReplace(String.format("%s님의 가입이 완료되었습니다", loginId), "/");
	}
	@PostMapping("/usr/member/doLogin")
	@ResponseBody
	public String doJoin(String loginId, String loginPw, HttpServletRequest requset) {
		
		HttpSession session = requset.getSession();
		if (session.getAttribute("loginId") != null) {
			return Util.jsReplace("이미 로그인 하셨습니다", "");
		}
		if (this.memberService.doLogin(loginId, loginPw) >= 1) {
			session.setAttribute("loginId", loginId);
			return Util.jsReplace(String.format("%s님의 로그인이 완료되었습니다", loginId), "/");
		}
		return Util.jsReplace("로그인 실패", "hb");
	}
	@GetMapping("/usr/member/logout")
	@ResponseBody
	public String doLogout(HttpServletRequest requset) {
		
		HttpSession session = requset.getSession();
		
		session.removeAttribute("loginId");
		
		return Util.jsReplace("로그아웃하였습니다.", "");
	}
	@GetMapping("/usr/member/loginIdDupChk")
	@ResponseBody
	public ResultData loginIdDupChk(String loginId) {
		
		Member member = this.memberService.getMemberByLoginId(loginId);
		
		if (member != null) {
			return new ResultData<>("F-1", String.format("[ %s ]은(는) 이미 사용중인 아이디입니다", loginId));
		}
		return new ResultData<>("S-1", String.format("[ %s ]은(는) 사용 가능한 아이디입니다", loginId));
	}
}