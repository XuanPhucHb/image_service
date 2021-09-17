package com.nxp.api.controller;

import java.util.List;

import com.nxp.api.entity.Banner;
import com.nxp.api.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/dvdd/v1/banner")
@CrossOrigin
public class BannerController {

	@Autowired
    BannerService bannerService;

	@RequestMapping(value = "/getAll", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	public List<Banner> getAll() {
		return bannerService.getAll();
	}

	@RequestMapping(value = "/landing/getAllForLanding", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	public ResponseEntity<String> getAllForLanding(@RequestParam Long operatorId) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = bannerService.getAllForLanding(operatorId);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/getById/{id}", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	public Banner getById(@PathVariable Long id) {
		return bannerService.getById(id);
	}

	@RequestMapping(value = "/search/{page}/{size}", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<String> searchByTitle(@RequestParam String keysearch, @PathVariable int page,
			@PathVariable int size) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = bannerService.search(keysearch, page, size);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/saveImage", method = RequestMethod.POST)
	public ResponseEntity<String> singleSave(@RequestPart("imageNews") MultipartFile imageNews) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = bannerService.saveImageToLocalFolder(imageNews);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/create", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	public ResponseEntity<String> create(@RequestParam String usernameCreate, @RequestBody Banner banner) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = bannerService.create(usernameCreate, banner);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/update/{id}", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	public ResponseEntity<String> update(@RequestParam String usernameUpdate, @RequestBody Banner banner,
			@PathVariable Long id) {
		banner.setId(id);
		HttpStatus httpStatus = HttpStatus.OK;
		String result = bannerService.update(usernameUpdate, banner);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@RequestMapping(value = "/delete/{id}", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	public ResponseEntity<String> create(@PathVariable Long id) {
		HttpStatus httpStatus = HttpStatus.OK;
		String result = bannerService.delete(id);
		if (result == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(result, httpStatus);
	}
}
