package com.nxp.api.controller;

import javax.servlet.http.HttpServletRequest;

import com.nxp.api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.nxp.api.entity.Account;

@RestController
@RequestMapping("/dvdd/v1/account")
@CrossOrigin
public class AccountController {

	@Autowired
	private AccountService accService;

	Gson gson = new Gson();

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> login(@RequestBody Account account) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = accService.checkLogin(account);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> logout(HttpServletRequest request, @RequestBody Account account) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = accService.checkLogout(request, account);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/search/{page}/{size}", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<String> searchByTitle(@RequestParam String keysearch, @RequestParam Long id, @PathVariable int page,
			@PathVariable int size) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = accService.search(keysearch, id, page, size);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> add(@RequestParam String usernameCreate, @RequestBody Account account) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = accService.create(usernameCreate, account);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/update/{id}", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<String> update(@RequestParam String usernameUpdate, @RequestBody Account account,
			@PathVariable Long id) {
		account.setId(id);
		HttpStatus httpStatus = HttpStatus.OK;
		String result = accService.update(usernameUpdate, account);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/delete/{id}", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<String> create(@PathVariable Long id) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = accService.delete(id);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> changePassword(HttpServletRequest request) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = accService.changePassword(request);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}
}
