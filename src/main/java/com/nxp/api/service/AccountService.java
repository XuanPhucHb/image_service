package com.nxp.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.nxp.api.config.LogConfig;
import com.nxp.api.entity.Role;
import com.nxp.api.entity.UserToken;
import com.nxp.api.repo.RoleRepository;
import com.nxp.api.utils.ServiceUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nxp.api.entity.Account;
import com.nxp.api.repo.AccountRepository;
import com.nxp.api.utils.Constant;

@Service
public class AccountService extends BaseService<Account, Long, AccountRepository> {

	Logger logger = LogConfig.getLogger(AccountService.class);

	public final static String TOKEN_HEADER = "authorization";

	@Autowired
	AccountRepository accountRepository;

	@Autowired
    RoleRepository roleRepository;

	@Autowired
	private JwtService jwtService;

	@Value("${service.expired_time}")
	public int EXPIRE_TIME;

	Gson gson = new Gson();

	public static List<Account> listAccounts = new ArrayList<>();

	public Account loadUserByUsername(String username) {
		for (Account account : listAccounts) {
			if (account.getUsername().equals(username)) {
				return account;
			}
		}
		return null;
	}

	public String checkLogin(Account account) {
		logger.info("call to login service: " + account.getUsername());
		JsonObject json = new JsonObject();
		String token = null;
		boolean isLogon = false;
		try {
			if (account != null) {
				Account result = super.repo.findAccountByUsernameAndPassword(account.getUsername(),
						ServiceUtils.getMd5(account.getPassword()));
				if (result != null) {
					logger.info("login OK");
					for (Map.Entry<String, UserToken> item : JwtService.MAP_USER_TOKEN.entrySet()) {
						if (item.getValue().getUsername().equals(account.getUsername())) {
							isLogon = true;
							token = item.getKey();
							break;
						}
					}
					if (!isLogon) {
						logger.info("token login: " + token);
						token = jwtService.generateTokenLogin(account.getUsername());
						boolean u = listAccounts.add(result);
						if (u) {
							JwtService.MAP_USER_TOKEN.put(token, new UserToken(token, result.getUsername(),
									System.currentTimeMillis() + EXPIRE_TIME));
						}
					}
					json.addProperty("token", token);
					json.addProperty("detail", "login success");
					json.addProperty("code", Constant.SUCCESS);
					String jsonStr = gson.toJson(result);
					json.add("account", JsonParser.parseString(jsonStr).getAsJsonObject());
				} else {
					json.addProperty("code", Constant.FAIL);
					json.addProperty("detail", "wrong username or password");
					logger.info("login fail: wrong username or password");
				}
			}

		} catch (Exception ex) {
			logger.error("error login: ", ex);
			return null;
		}
		return gson.toJson(json);
	}

	public String search(String keysearch, Long id, int page, int size) {
		logger.info("call to search account");
		JsonObject json = new JsonObject();
		json.addProperty("code", Constant.FAIL);
		try {
			List<Account> list = accountRepository.searchAccount(keysearch, id, PageRequest.of(page - 1, size));
			Long total = accountRepository.countAccount(keysearch, id);
			list.forEach(t -> {
				t.setPassword(null);
				t.setRoles(null);
			});
			JsonArray jsonA = JsonParser.parseString(gson.toJson(list)).getAsJsonArray();
			json.add("result", jsonA);
			json.addProperty("code", Constant.SUCCESS);
			json.addProperty("total", total);
		} catch (Exception e) {
			logger.error("error search account: ", e);
			return null;
		}
		return gson.toJson(json);
	}

	public String create(String usernameCreate, Account account) {
		logger.info("call to create data code view: " + account.toString());
		JsonObject json = new JsonObject();
		json.addProperty("code", Constant.FAIL);
		try {
			if (account.getUsername() == null || account.getUsername().isEmpty()) {
				logger.info("null username");
				json.addProperty("detail", "tên đăng nhập không hợp lệ");
			} else if (account.getPassword() == null || account.getPassword().isEmpty()) {
				logger.info("null password");
				json.addProperty("detail", "mật khẩu không hợp lệ");
			} else if (account.getFullname() == null || account.getFullname().isEmpty()) {
				logger.info("null fullname");
				json.addProperty("detail", "tên không hợp lệ");
			} else if (account.getRoleId() != Constant.IS_ROLE_ADMIN && account.getRoleId() != Constant.IS_ROLE_CENSOR
					&& account.getRoleId() != Constant.IS_ROLE_DATA
					&& account.getRoleId() != Constant.IS_ROLE_CONTENT) {
				logger.info("invalid role");
				json.addProperty("detail", "Phân quyền không hợp lệ");
			} else if (account.getRoleId() != Constant.IS_ROLE_DATA
					&& (account.getPenname() == null || account.getPenname().isEmpty())) {
				logger.info("null penname");
				json.addProperty("detail", "bút danh không hợp lệ");
			} else {
				Role role = checkRole(usernameCreate);
				if (!role.getRole().equals(Constant.ROLE_ADMIN)) {
					logger.info("not admin role");
					json.addProperty("detail", "Không có quyền tạo tài khoản");
				} else {
					account.setPassword(ServiceUtils.getMd5(account.getPassword()));
					account.setCreatedBy(usernameCreate);
					account.setUpdatedBy(usernameCreate);
					account.setRoleId(account.getRoleId());
					account.setCreatedDate(new Date());
					account.setUpdatedDate(new Date());
					Account result = accountRepository.save(account);
					if (result == null) {
						logger.info("create account fail");
						json.addProperty("detail", "thêm mới không thành công");
					} else {
						logger.info("create account success");
						json.addProperty("code", Constant.SUCCESS);
						json.addProperty("detail", "thêm mới thành công");
					}
				}
			}
		} catch (Exception e) {
			logger.error("error create account: ", e);
			return null;
		}
		return gson.toJson(json);
	}

	public String update(String usernameUpdate, Account account) {
		logger.info("call to update data code view: " + account.toString());
		JsonObject json = new JsonObject();
		json.addProperty("code", Constant.FAIL);
		try {
			if (account.getUsername() == null || account.getUsername().isEmpty()) {
				logger.info("null username");
				json.addProperty("detail", "tên đăng nhập không hợp lệ");
			} else if (account.getFullname() == null || account.getFullname().isEmpty()) {
				logger.info("null fullname");
				json.addProperty("detail", "tên không hợp lệ");
			} else if (account.getRoleId() != Constant.IS_ROLE_ADMIN && account.getRoleId() != Constant.IS_ROLE_CENSOR
					&& account.getRoleId() != Constant.IS_ROLE_DATA
					&& account.getRoleId() != Constant.IS_ROLE_CONTENT) {
				logger.info("invalid role");
				json.addProperty("detail", "Phân quyền không hợp lệ");
			} else if (account.getRoleId() != Constant.IS_ROLE_DATA
					&& (account.getPenname() == null || account.getPenname().isEmpty())) {
				logger.info("null penname");
				json.addProperty("detail", "bút danh không hợp lệ");
			} else {
				Role role = checkRole(usernameUpdate);
				if (!role.getRole().equals(Constant.ROLE_ADMIN)) {
					logger.info("not admin role");
					json.addProperty("detail", "Không có quyền sửa tài khoản");
				} else {
					Account accCheck = super.repo.checkUsernameForUpdate(account.getUsername(), account.getId());
					if (accCheck != null) {
						logger.info("null username");
						json.addProperty("detail", "tên đăng nhập này đã tồn tại");
					} else {
						account.setRoleId(account.getRoleId());
						account.setUpdatedBy(usernameUpdate);
						account.setUpdatedDate(new Date());
						Account result = accountRepository.save(account);
						if (result == null) {
							logger.info("create account fail");
							json.addProperty("detail", "cập nhật không thành công");
						} else {
							logger.info("create account success");
							json.addProperty("code", Constant.SUCCESS);
							json.addProperty("detail", "cập nhật thành công");
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("error update account: ", e);
			return null;
		}
		return gson.toJson(json);
	}

	public String delete(Long id) {
		logger.info("call to delete account");
		JsonObject json = new JsonObject();
		json.addProperty("code", Constant.FAIL);
		try {
			super.remove(id);
			Account result = super.getById(id);
			if (result != null) {
				logger.info("delete account fail");
				json.addProperty("detail", "xóa không thành công");
			} else {
				logger.info("delete account success");
				json.addProperty("code", Constant.SUCCESS);
				json.addProperty("detail", "xóa thành công");
			}
		} catch (Exception e) {
			logger.error("error delete account: ", e);
			return null;
		}
		return gson.toJson(json);
	}

	public Role checkRole(String username) {
		Role result = roleRepository.checkRole(username);
		return result;
	}

	public String checkLogout(HttpServletRequest request, Account account) {
		logger.info("call to logout service: " + account.getUsername());
		JsonObject json = new JsonObject();
		try {
			String authToken = request.getHeader(TOKEN_HEADER);
			String usernameToken = jwtService.getUsernameFromToken(authToken);
			if (usernameToken != null) {
				logger.info("logout success");
				for (int i = 0; i < AccountService.listAccounts.size(); i++) {
					if (listAccounts.get(i).getUsername().equals(usernameToken)) {
						listAccounts.remove(listAccounts.get(i));
					}
				}
				if (JwtService.MAP_USER_TOKEN.size() > 0) {
					JwtService.MAP_USER_TOKEN.remove(authToken);
				}
				json.addProperty("code", Constant.SUCCESS);
				json.addProperty("detail", "logout success");
			} else {
				json.addProperty("code", Constant.FAIL);
				json.addProperty("detail", "this account not logon yet");
				logger.info("logout fail: this account not logon yet");
			}

		} catch (Exception ex) {
			logger.error("error: ", ex);
			return null;
		}
		return gson.toJson(json);
	}

	@Transactional
	public String changePassword(HttpServletRequest request) {
		JsonObject jsonString;
		JsonObject json = new JsonObject();
		json.addProperty("code", Constant.FAIL);
		try {
			jsonString = JsonParser.parseReader(request.getReader()).getAsJsonObject();
			if (jsonString.get("oldPassword") == null) {
				logger.info("old password null");
				json.addProperty("detail", "mật khẩu cũ không hợp lệ");
			} else if (jsonString.get("newPassword") == null) {
				logger.info("new password null");
				json.addProperty("detail", "mật khẩu mới không hợp lệ");
			} else if (jsonString.get("username") == null) {
				logger.info("username null");
				json.addProperty("detail", "tên đăng nhập không hợp lệ");
			} else {
				String username = jsonString.get("username").getAsString();
				String oldPassword = jsonString.get("oldPassword").getAsString();
				Account tempAcc = checkOldPassword(username, oldPassword);
				if (tempAcc != null) {
					String newPassword = jsonString.get("newPassword").getAsString();
					newPassword = ServiceUtils.getMd5(newPassword);
					accountRepository.changePasswordByUsername(newPassword, username);
					logger.info("change pass success");
					json.addProperty("code", Constant.SUCCESS);
					json.addProperty("detail", "change password success");
				} else {
					json.addProperty("detail", "mật khẩu cũ không đúng");
					logger.info("wrong old password");
				}
			}

		} catch (Exception e) {
			logger.error("error changePassword: ", e);
			return null;
		}
		return gson.toJson(json);
	}

	private Account checkOldPassword(String username, String oldPassword) {
		return super.repo.findAccountByUsernameAndPassword(username, ServiceUtils.getMd5(oldPassword));
	}
}
