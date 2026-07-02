package com.telco.members.tcos.controller;

import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.AccessCtrlRepo;
import com.telco.members.tcos.service.CompanyService;
import com.telco.members.tcos.service.SuretyService;
import com.telco.members.tcos.service.UserService;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.telco.members.tcos.dto.UserregisterDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/members/user_registration")
public class userController {
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SuretyService suretyService;
	@Autowired
	private AccessCtrlRepo accessCtrlRepo;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private User user;

	@Value("${my.api.key}")
	private String apiKey;

	@GetMapping
	public String listUsers(Model model) {

		List<UserregisterDTO> userss = userService.getUnactiveuser();

//		for(UserregisterDTO u : userss){
//			System.out.println("UserId = " + u.getUserId());
//			System.out.println("EmpId = " + u.getEmpId());
//			System.out.println("Name = " + u.getName());
//			System.out.println("----------------");
//		}

		model.addAttribute("users", userss);

		return "dashboard/user_registration";
	}

	@GetMapping("/deleteUser/{empId}")
	public String deleteUser(@PathVariable String empId) {
		this.userService.deleteUser(empId);
		return "redirect:/members/user_registration";
	}

	@GetMapping("/new_registration")
	public String createNew(Model model) {
		List<Company> companies = companyService.getAllCompany();
		model.addAttribute("companyList", companies);
		model.addAttribute("UserregisterDTO", new UserregisterDTO());
		return "dashboard/new_user";
	}

	@GetMapping("/new_registration/security/{key}")
	public ResponseEntity<Boolean> checkSecurity(@PathVariable int key){
		Integer accessId = accessCtrlRepo.getAccessId(accessDec.Create_User);
		boolean isValid = (accessId != null && key == accessId);
		return ResponseEntity.ok(isValid);
	}

	@GetMapping("/details/{userId}/fetchUser/{ticketNo}")
	private ResponseEntity<UserregisterDTO> seaarchUserId(@PathVariable String userId, @PathVariable String ticketNo) {
		User users = userService.getUsers(ticketNo);
		if (users != null) {
			UserregisterDTO userDTO = userService.convertToDTO1(users);
			return ResponseEntity.ok(userDTO);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@GetMapping("/details/{userId}/addUser/{userIds}")
	public ResponseEntity<String> addGuarantor(@PathVariable String userId, @PathVariable String userIds) {
		suretyService.addSurety(userId, userIds);
		return ResponseEntity.ok("Surety added successfully!");
	}

	@GetMapping("/details/{userId}/deleteUser/{userIds}")
	public ResponseEntity<String> removeGuarantor(@PathVariable String userId, @PathVariable String userIds) {
		suretyService.removeSurety(userId, userIds);
		return ResponseEntity.ok("Surety removed successfully");
	}

	@GetMapping("/smsgarantor")
	public ResponseEntity<Map<String, Boolean>> sendSmsToGarantor(@RequestParam("userId") String userId) {

		List<Surety> lst = suretyService.findByUserId(userId);
		User usr = userService.getUserByUserId(userId);

		List<String> nums = new ArrayList<>();
		for (Surety s : lst) {
			nums.add(s.getPhone_num() + "");
		}
		String allNumbers = String.join(",", nums);

		Map<String, Boolean> responseMap = new HashMap<>();
		OkHttpClient client = new OkHttpClient();

		String senderId = "TCOSMS";

		String message = "Dear Sir/Mam,\r\n" + "you have been added as guaranteer for the registration of "
				+ usr.getName() + " with " + userId + ".\r\n" + "\r\n" + "-Sigma e Solution Pvt. Ltd.";

		RequestBody formBody = new FormBody.Builder().add("module", "TRANS_SMS")
				.add("apikey", apiKey).add("to", allNumbers).add("from", senderId)
				.add("msg", message).build();

		Request request = new Request.Builder().url("https://2factor.in/API/R1/").post(formBody).build();
		
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
					responseMap.put("msg", true);
				} else {
					responseMap.put("msg", false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}


		return ResponseEntity.ok(responseMap);
	}

	@PostMapping("/update")
	public String updateu(@ModelAttribute UserregisterDTO userregisterDTOS, Model model) {
		userService.updateUser(userregisterDTOS);
		
		if(userregisterDTOS.getAccountStatus()==AccountStatus.ACTIVE) {
			
			OkHttpClient client = new OkHttpClient();
			
			String senderId = "TCOSMS";
			String message = "Account creation for TCOS by ticket number "+userregisterDTOS.getUserId()+" was successful.\r\n"
					+ "\r\n"
					+ "-Sigma e Solution Pvt. Ltd.";

			RequestBody formBody = new FormBody.Builder().add("module", "TRANS_SMS")
					.add("apikey", apiKey).add("to", userregisterDTOS.getPhoneNum()+"").add("from", senderId)
					.add("msg", message).build();

			Request request = new Request.Builder().url("https://2factor.in/API/R1/").post(formBody).build();

			try (Response response = client.newCall(request).execute()) {
				if (response.isSuccessful()) {
						System.out.println("sms sent");
					} else {
						System.out.println("sms not sent");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}

		model.addAttribute("successMessage", "User saved successfully!");
		return "redirect:/members/user_registration";
	}

	@PostMapping("/newUser")
	public String newUser(@ModelAttribute UserregisterDTO userregisterDTOS, Model model) {
		String encryptedPassword = passwordEncoder.encode(userregisterDTOS.getPassword()); // Hash password
		userregisterDTOS.setPassword(encryptedPassword);
		userregisterDTOS.setUserType("Existing User");
		userregisterDTOS.setPaymentMode(0);
		userregisterDTOS.setUtr("Not Applicable");
		userService.newUser(userregisterDTOS);
		model.addAttribute("successMessage", "User saved successfully!");
		return "redirect:/members/user_registration";
	}

	@GetMapping("/updateUser/{empId}")
	public String updateUser(@PathVariable String empId, Model model) {
		User users = userService.getUser(empId);
		if (users != null) {
			UserregisterDTO userDTO = userService.convertToDTOs(users);
			model.addAttribute("user", userDTO);
		} else {
			model.addAttribute("errorMessage", "No user found with the given identifier.");
			return "error_page"; // Redirect to an error page or handle accordingly
		}
		return "dashboard/update_user";
	}

	@GetMapping("/updateUser/security/{key}")
	public ResponseEntity<Boolean> checkUpdate(@PathVariable int key){
		Integer accessId = accessCtrlRepo.getAccessId(accessDec.Update_User);
		boolean isValid = (accessId != null && key == accessId);
		return ResponseEntity.ok(isValid);
	}

	@GetMapping("/userDetails/{empId}")
	public String userDetails(@PathVariable String empId, Model model) {
		User users = userService.getUser(empId);
		if (users != null) {
			List<Surety> sureties = suretyService.findByUserId(users.getUserId());
			UserregisterDTO userDTO = userService.convertToDTOs(users);
			model.addAttribute("surety", sureties);
			model.addAttribute("user", userDTO);
		} else {
			model.addAttribute("errorMessage", "No user found with the given identifier.");
			return "error_page"; // Redirect to an error page or handle accordingly
		}

		return "dashboard/user_details";
	}

	@GetMapping("/{empId}")
	@ResponseBody
	public String getUser(@PathVariable String empId, Model model) {
		User user = userService.getUser(empId);
		model.addAttribute("users", user);
		return "dashboard/" + "user_registation";
	}
}
