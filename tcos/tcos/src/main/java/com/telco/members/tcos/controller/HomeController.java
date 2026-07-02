package com.telco.members.tcos.controller;

import com.telco.members.tcos.service.LoanService;
import com.telco.members.tcos.service.MembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/members")
public class HomeController {

	@Autowired
	private MembersService membersService;

	@Autowired
	private LoanService loanService;

	
	@GetMapping("/index")
	public String home(HttpServletRequest request, Model model) {
		int AM = membersService.countActive();
		model.addAttribute("ActiveMember",AM);
		int IM = membersService.countInActive();
		model.addAttribute("InActiveMember",IM);
		int LP = loanService.countLP();
		int LoanPassed = loanService.countLloanp();
		int APending = loanService.countAdvicep();
		model.addAttribute("LPending",LP);
		model.addAttribute("LPassed",LoanPassed);
		model.addAttribute("APending", APending);
		return "index";
	}

}
