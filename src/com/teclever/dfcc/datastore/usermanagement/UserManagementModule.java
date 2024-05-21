package com.teclever.dfcc.datastore.usermanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.UserLoginDetailsResponse;
import com.teclever.datastore.entities.UserLoginDetails;
import com.teclever.datastore.service.UserLoginDetailsService;
import com.teclever.datastore.service.UserRoleMasterDetailsService;
import com.teclever.dfcc.datastore.dto.LoginResponse;
import com.teclever.dfcc.datastore.dto.SystemConfig;
import com.teclever.dfcc.datastore.dto.UserGetAllResponse;
import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
import com.teclever.dfcc.datastore.dto.UserRoleMasterDto;
import com.teclever.dfcc.datastore.dto.UserRoleResponse;
import com.teclever.dfcc.datastore.filemanagement.SystemConfigManagement;

public class UserManagementModule {

	public UserRoleResponse getAllUserType() {
		UserRoleResponse userRoleResponse = new UserRoleResponse();
		Response response = new Response();
		try {
			UserRoleMasterDetailsService userRole = new UserRoleMasterDetailsService();
			Map<String, String> userType = userRole.getAllUserType();
			List<UserRoleMasterDto> listUserRoleDto = new ArrayList<>();
			for (Map.Entry<String, String> entry : userType.entrySet()) {
				UserRoleMasterDto userDto = new UserRoleMasterDto();
				userDto.setRoleId(entry.getKey());
				userDto.setUserType(entry.getValue());
				listUserRoleDto.add(userDto);

			}
			response.setResponseCode(1);
			response.setResponseMessage("Fetching User Role Successfull");
			userRoleResponse.setResponse(response);
			userRoleResponse.setListOfUserRoleMaster(listUserRoleDto);
			return userRoleResponse;
		} catch (Exception e) {
			response.setResponseCode(0);
			response.setResponseMessage("Error  " + e.getLocalizedMessage());
			userRoleResponse.setResponse(response);
			return userRoleResponse;
		}
	}

	public LoginResponse validateUser(String loginName, String password) {
		LoginResponse loginResponse = new LoginResponse();
		Response response = new Response();
		try {
			SystemConfig systemConfig = SystemConfigManagement.getConfiguration();
			if (systemConfig != null) {
				if (loginName.equals(systemConfig.getAdminName())) {
//				if (BCrypt.checkpw(password, systemConfig.getAdminPassword())) {
					if (password.equals(systemConfig.getAdminPassword())) {

						if (Integer.parseInt(systemConfig.getLoginType()) == 0) {
							response.setResponseCode(101);
							response.setResponseMessage("Please Update password");
							loginResponse.setLoginName(loginName);
							loginResponse.setRoleId("RL_ID_1");
							loginResponse.setResponse(response);
							return loginResponse;
						} else if (Integer.parseInt(systemConfig.getLoginType()) == 1) {
							response.setResponseCode(1);
							response.setResponseMessage("Login Succesfull");
							loginResponse.setLoginName(loginName);
							loginResponse.setRoleId("RL_ID_1");
							loginResponse.setResponse(response);
							return loginResponse;
						} else {
							response.setResponseCode(0);
							response.setResponseMessage("Error in SystemCofiguration File");
						}
					} else {
						response.setResponseCode(0);
						response.setResponseMessage("Please Enter valid Password ");
					}

				} else {

					return authenticateUser(loginName, password, systemConfig.getLaunchType());
				}
			} else {
				response.setResponseCode(0);
				response.setResponseMessage("Login Unsuccesfull SystemConfig File Error");
			}
		} catch (Exception e) {
			response.setResponseCode(0);
			response.setResponseMessage("Login Unsuccesfull " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		loginResponse.setResponse(response);
		return loginResponse;
	}

	public Response changePasswordForBellAdmin(String password) {
		Response res = new Response();
		try {
			SystemConfig systemConfig = SystemConfigManagement.getConfiguration();

			systemConfig.setAdminPassword(password);
			systemConfig.setLoginType("1");

			SystemConfigManagement.setConfiguration(systemConfig);
			res.setResponseCode(1);
			res.setResponseMessage("Password updated Successufull ");
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Password updated Unsuccessufull " + e.getLocalizedMessage());
		}
		return res;
	}

	public Response updateOption(String option) {
		Response res = new Response();
		try {
			SystemConfig systemConfig = SystemConfigManagement.getConfiguration();

			
			systemConfig.setLaunchType(option);

			SystemConfigManagement.setConfiguration(systemConfig);
			res.setResponseCode(1);
			res.setResponseMessage("LaunchType updated Successufull ");
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("LaunchType updated Unsuccessufull " + e.getLocalizedMessage());
		}
		return res;
	}

	public LoginResponse authenticateUser(String loginName, String password, String optionType) {
		LoginResponse loginResponse = new LoginResponse();
		try {

			UserLoginDetailsService userLogin = new UserLoginDetailsService();

			UserLoginDetailsResponse userLoginDto = userLogin.authenticateUser(loginName, password);
			if (userLoginDto.getResponse().getResponseCode() == 0) {
				loginResponse.setResponse(userLoginDto.getResponse());
				return loginResponse;
			}
			UserRoleMasterDetailsService userRoleService = new UserRoleMasterDetailsService();
			boolean userAccess = userRoleService.getUserRoleMasterByRoleIdOptionType(userLoginDto.getRoleId(),
					optionType);
			if (userAccess) {
				loginResponse.setResponse(userLoginDto.getResponse());
				loginResponse.setRoleId(userLoginDto.getRoleId());
				loginResponse.setLoginName(userLoginDto.getLoginName());
			} else {
				Response res = new Response();
				res.setResponseCode(0);
				res.setResponseMessage("User Not Allowed to Login");
				loginResponse.setResponse(res);
			}

		} catch (Exception e) {
			System.out.println("Exception in User Management ");
			e.printStackTrace();
		}
		return loginResponse;
	}

	public Response deleteUser(String userId) {
		Response response = new Response();

		try {
			UserLoginDetailsService userLogin = new UserLoginDetailsService();
			response = userLogin.removeUser(userId);
		} catch (Exception e) {
			System.out.println("Delete User Error " + e.getLocalizedMessage());
		}
		return response;
	}

	public UserGetAllResponse getAllUsers() {
		UserGetAllResponse response = new UserGetAllResponse();
		try {
			Response res = new Response();
			UserLoginDetailsService userLogin = new UserLoginDetailsService();
			List<UserLoginDetails> userLoginResponse = userLogin.getAllUser();
			List<UserLoginDetailsDto> listOfUserLogin = new ArrayList<>();
			if (userLoginResponse == null) {
				res.setResponseCode(0);
				res.setResponseMessage("User Details Empty");
				response.setResponse(res);
				return response;
			}
			UserRoleMasterDetailsService userRole = new UserRoleMasterDetailsService();
			Map<String, String> userType = userRole.getAllUserType();
			for (UserLoginDetails userLoginDetails : userLoginResponse) {
				UserLoginDetailsDto userLoginDto = new UserLoginDetailsDto();

				userLoginDto.setUserId(userLoginDetails.getUserId());
				userLoginDto.setLoginName(userLoginDetails.getLoginName());
				userLoginDto.setRoleId(userLoginDetails.getRoleId());
				userLoginDto.setRoleName(userType.get(userLoginDetails.getRoleId()));
				userLoginDto.setDate(userLoginDetails.getDate());
				userLoginDto.setDigitalSignature(userLoginDetails.getDigitalSignature());

				listOfUserLogin.add(userLoginDto);

			}

			response.setUserList(listOfUserLogin);
			res.setResponseCode(1);
			res.setResponseMessage("Fetch User Details Successfull");
			response.setResponse(res);
			return response;
		} catch (Exception e) {
			System.out.println("Delete User Error " + e.getLocalizedMessage());
		}
		return response;
	}

	public UserLoginDetailsDto addUser(UserLoginDetailsDto userLoginDto) {
		UserLoginDetailsDto response = new UserLoginDetailsDto();
		Response res = new Response();
		try {
			UserLoginDetailsService userLogin = new UserLoginDetailsService();

			List<UserLoginDetails> flag = userLogin.validateUserName(userLoginDto.getLoginName());
			if (flag.size() > 0) {
				res.setResponseCode(0);
				res.setResponseMessage("User Already Exist Pls Try Other..");
				response.setResponse(res);
				return response;
			}
			UserLoginDetails newUser = new UserLoginDetails();
			newUser.setLoginName(userLoginDto.getLoginName());
			newUser.setPassword(userLoginDto.getPassword());
			newUser.setRoleId(userLoginDto.getRoleId());
			Date utilDate = new Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

			newUser.setDate(sqlDate);
			newUser.setDigitalSignature(userLoginDto.getDigitalSignature());
			UserLoginDetailsResponse userAddResponse = userLogin.addUser(newUser);

			response.setUserId(userAddResponse.getUserId());
			response.setLoginName(userAddResponse.getLoginName());
			response.setPassword(userAddResponse.getPassword());
			response.setRoleId(userAddResponse.getRoleId());
			response.setDate(userAddResponse.getDate());
			response.setDigitalSignature(userAddResponse.getDigitalSignature());
			response.setResponse(userAddResponse.getResponse());
			return response;

		} catch (Exception e) {
			System.out.println("Adding User Error " + e.getLocalizedMessage());
		}
		return response;

	}

	public Response updateUser(UserLoginDetailsDto userLoginDto) {
		Response res = new Response();
		try {
			UserLoginDetailsService userLogin = new UserLoginDetailsService();

			UserLoginDetails newUser = new UserLoginDetails();
			newUser.setUserId(userLoginDto.getUserId());
			newUser.setLoginName(userLoginDto.getLoginName());
			newUser.setPassword(userLoginDto.getPassword());
			newUser.setRoleId(userLoginDto.getRoleId());
			newUser.setDate(userLoginDto.getDate());
			newUser.setDigitalSignature(userLoginDto.getDigitalSignature());
			res = userLogin.updateUser(newUser);

			userLoginDto.setPassword(null);
			return res;

		} catch (Exception e) {
			System.out.println("Update User Error " + e.getLocalizedMessage());
			throw e;
		}
	}

	public Response adminChangePassword(String userId, String oldPassword, String newPassword, String key) {
		Response res = new Response();
		try {
			UserLoginDetailsService user = new UserLoginDetailsService();
			if (!key.equals("") || !key.equals(null)) {

				res = user.changePasswordForBelAdmin(userId, oldPassword, newPassword, key);
			} else {
				res = user.changePasswordForBelAdmin(userId, oldPassword, newPassword, key);
			}
		} catch (Exception ex) {

		}
		return res;
	}

	public Response ChangePasswordForOthers(String userId, String newPassword) {
		Response res = new Response();
		try {
			UserLoginDetailsService user = new UserLoginDetailsService();
			res = user.changePassword(userId, newPassword);
		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return res;
	}

	public UserLoginDetailsDto getUserByUserId(String userId) {
		UserLoginDetailsDto userLoginDetailresponse = new UserLoginDetailsDto();
		try {
			UserLoginDetailsService user = new UserLoginDetailsService();
			UserLoginDetails response = user.getUserByUserId(userId);
			Response res = new Response();

			if (response != null) {
				userLoginDetailresponse.setLoginName(response.getLoginName());
				userLoginDetailresponse.setUserId(response.getUserId());
				userLoginDetailresponse.setRoleId(response.getRoleId());
				userLoginDetailresponse.setDigitalSignature(response.getDigitalSignature());
				userLoginDetailresponse.setDate(response.getDate());

				res.setResponseCode(1);
				res.setResponseMessage("Get User Successfull");
				userLoginDetailresponse.setResponse(res);

			}

		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return userLoginDetailresponse;
	}
}
