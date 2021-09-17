package com.nxp.api.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.nxp.api.config.LogConfig;
import com.nxp.api.entity.Banner;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nxp.api.repo.BannerRepository;
import com.nxp.api.utils.Constant;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BannerService extends BaseService<Banner, Long, BannerRepository> {

    private static final Integer ACCESS_OK = 1;

    Logger logger = LogConfig.getLogger(BannerService.class);
    Gson gson = new Gson();

    @Autowired
    BannerRepository bannerRepository;

    @Value("${image_folder}")
    String imageFolderPath;

    @Value("${image_header_part}")
    String imageHeaderPath;

    @Value("${access_code}")
    Integer access;

    @Override
    public List<Banner> getAll() {
        logger.info("call to getAll banner");
        return super.repo.findAllByOrderByPriorityAscCreatedDateDesc();
    }

    public String getAllForLanding(Long operatorId) {
        logger.info("call to getAllForLanding banner");
        JsonObject json = new JsonObject();
        json.addProperty("code", Constant.FAIL);
        try {
            List<Banner> list = bannerRepository
                    .findAllByStatusAndOperatorIdOrderByPriorityAscCreatedDateDesc(Constant.ENABLE, operatorId);
            JsonArray jsonA = JsonParser.parseString(gson.toJson(list)).getAsJsonArray();
            json.add("result", jsonA);
            json.addProperty("code", Constant.SUCCESS);
        } catch (Exception e) {
            logger.error("error get all banner for landing: ", e);
            return null;
        }
        return gson.toJson(json);
    }

    @Override
    public Banner getById(Long id) {
        logger.info("call to getById banner");
        return super.getById(id);
    }

    public String search(String keysearch, int page, int size) {
        logger.info("call to search banner");
        JsonObject json = new JsonObject();
        json.addProperty("code", Constant.FAIL);
        try {
            List<Banner> list = bannerRepository.searchBanner(keysearch, PageRequest.of(page - 1, size));
            Long total = bannerRepository.countBanner(keysearch);
            JsonArray jsonA = JsonParser.parseString(gson.toJson(list)).getAsJsonArray();
            json.add("result", jsonA);
            json.addProperty("code", Constant.SUCCESS);
            json.addProperty("total", total);
        } catch (Exception e) {
            logger.error("error search banner: ", e);
            return null;
        }
        return gson.toJson(json);
    }

    public String create(String usernameCreate, Banner banner) {
        logger.info("call to create banner: " + banner.toString());
        JsonObject json = new JsonObject();
        json.addProperty("code", Constant.FAIL);
        try {
            if (banner.getLink() == null || banner.getLink().isEmpty()) {
                logger.info("null link");
                json.addProperty("detail", "đường dẫn không hợp lệ");
            } else if (banner.getOperatorId() == null || banner.getOperatorId() < Constant.MIN_ID) {
                logger.info("invalid operator id");
                json.addProperty("detail", "nhà mạng không hợp lệ");
            } else if (banner.getStatus() != Constant.ENABLE && banner.getStatus() != Constant.DISABLE) {
                logger.info("invalid status");
                json.addProperty("detail", "trạng thái không hợp lệ");
            } else if (banner.getImagePath() == null && banner.getImagePath().isEmpty()) { // mục có ảnh hay không
                logger.info("null image path");
                json.addProperty("detail", "đường dẫn ảnh không hợp lệ");
            } else {
                if (banner.getHtmlType() == Constant.DYNAMIC_WEB)
                    if (!banner.getLink().contains("id="))
                        banner.setLink("id=" + banner.getLink());
                banner.setImagePath(imageHeaderPath.concat("/").concat(banner.getImagePath()));
                banner.setCreatedBy(usernameCreate);
                banner.setCreatedDate(new Date());
                Banner result = bannerRepository.save(banner);
                if (result == null) {
                    logger.info("create banner fail");
                    json.addProperty("detail", "thêm mới không thành công");
                } else {
                    logger.info("create banner success");
                    json.addProperty("code", Constant.SUCCESS);
                    json.addProperty("detail", "thêm mới thành công");
                }
            }
        } catch (Exception e) {
            logger.error("error create banner: ", e);
            return null;
        }
        return gson.toJson(json);
    }

    public String update(String usernameUpdate, Banner banner) {
        logger.info("call to update banner: " + banner.toString());
        JsonObject json = new JsonObject();
        json.addProperty("code", Constant.FAIL);
        try {
            if (banner.getLink() == null || banner.getLink().isEmpty()) {
                logger.info("null link");
                json.addProperty("detail", "đường dẫn không hợp lệ");
            } else if (banner.getOperatorId() == null || banner.getOperatorId() < Constant.MIN_ID) {
                logger.info("invalid operator id");
                json.addProperty("detail", "nhà mạng không hợp lệ");
            } else if (banner.getStatus() != Constant.ENABLE && banner.getStatus() != Constant.DISABLE) {
                logger.info("invalid status");
                json.addProperty("detail", "trạng thái không hợp lệ");
            } else if (banner.getImagePath() == null && banner.getImagePath().isEmpty()) {
                logger.info("null image path");
                json.addProperty("detail", "đường dẫn ảnh không hợp lệ");
            } else {
                if (!banner.getImagePath().contains(imageHeaderPath))
                    banner.setImagePath(imageHeaderPath.concat("/").concat(banner.getImagePath()));
                if (banner.getHtmlType() == Constant.DYNAMIC_WEB)
                    if (!banner.getLink().contains("id="))
                        banner.setLink("id=" + banner.getLink());
                Banner result = bannerRepository.save(banner);
                if (result == null) {
                    logger.info("update banner fail");
                    json.addProperty("detail", "cập nhật không thành công");
                } else {
                    logger.info("update banner success");
                    json.addProperty("code", Constant.SUCCESS);
                    json.addProperty("detail", "cập nhật thành công");
                }
            }
        } catch (Exception e) {
            logger.error("error update banner: ", e);
            return null;
        }
        return gson.toJson(json);
    }

    public String delete(Long id) {
        logger.info("call to delete banner");
        JsonObject json = new JsonObject();
        json.addProperty("code", Constant.FAIL);
        try {
            bannerRepository.deleteById(id);
            logger.info("delete banner success");
            json.addProperty("code", Constant.SUCCESS);
            json.addProperty("detail", "xóa thành công");
        } catch (Exception e) {
            logger.error("error delete banner: ", e);
            return null;
        }
        return gson.toJson(json);
    }

    public String saveImageToLocalFolder(MultipartFile imageNews) {
        logger.info("acccess image api: " + access);
        JsonObject json = new JsonObject();
        if (access == ACCESS_OK) {
            json.addProperty("code", Constant.SUCCESS);
            logger.info("call to upload image: " + imageNews.toString());
            try {
                String fileName = imageNews.getOriginalFilename();
                byte[] bytes = imageNews.getBytes();
                String imagePath = imageFolderPath.concat("/").concat(fileName);
                if (checkExistedImage(imagePath)) {
                    logger.info("image existed: " + imagePath);
                    String splFile[] = fileName.split("\\.");
                    String tail = splFile[splFile.length - 1];
                    fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).concat(".").concat(tail);
                    imagePath = imageFolderPath.concat("/") + fileName;
                    Thread.sleep(1000);
                }
                BufferedOutputStream buffStream = new BufferedOutputStream(new FileOutputStream(new File(imagePath)));
                buffStream.write(bytes);
                buffStream.close();
                logger.info("save image to folder success: " + imagePath);
                json.addProperty("detail", "upload ảnh thành công");
                json.addProperty("result", imagePath);
                json.addProperty("fileName", fileName);
            } catch (Exception e) {
                logger.error("error save image to folder: ", e);
                return null;
            }
        }else {
			json.addProperty("code", Constant.FAIL);
			json.addProperty("detail", "Access denied!!!");
		}
        return gson.toJson(json);
    }

    public boolean checkExistedImage(String imagePath) {
        File f = new File(imagePath);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
