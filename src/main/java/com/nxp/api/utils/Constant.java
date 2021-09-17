package com.nxp.api.utils;

public class Constant {

	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	public static final int ERROR_MULTI_DATACODE_TIME = -2;
	public static final int ERROR_MULTI_DATACODE_UNIQUE = -3;

	// status ẩn hiển
	public static final int DISABLE = 0;
	public static final int ENABLE = 1;

	// type loại trang tĩnh - động
	public static final int STATIC_WEB = 1;
	public static final int DYNAMIC_WEB = 2;
	

	// status chưa sử dụng - đã sử dụng data
	public static final int NOT_USED = 1;
	public static final int USED = 2;

	// code nhà mạng
	public static final String MBF_CODE = "MBF";
	public static final String VT_CODE = "VT";
	public static final String VNF_CODE = "VNF";

	// code nhà mạng - id
	public static final int MBF = 1;
	public static final int VT = 2;
	public static final int VNF = 3;
	
	//status data code
	public static final int NOT_SOLD_YET = 1;
	public static final int SOLD = 2;

	// status kiểm duyệt
	public static final int REJECT = -1;
	public static final int WAIT_CENSOR = 0;
	public static final int CENSORED = 1;

	public static final int ON_LANDING_PAGE = 1;
	public static final int ON_SALE_PAGE = 2;
	public static final int BOTH_LANDING_AND_SALE_PAGE = 3;

	public static final int MIN_ID = 1;

	public static final String ROLE_ADMIN = "ADMIN";
	public static final int IS_ROLE_ADMIN = 1;
	public static final int IS_ROLE_CENSOR = 2;
	public static final int IS_ROLE_DATA = 3;
	public static final int IS_ROLE_CONTENT = 4;

	public static final int TYPE_NEWS_SALE = 1;
	public static final int TYPE_NEWS_DISCOVER = 2;
	public static final int TYPE_NEWS_SUPPORT = 3;
	
}
