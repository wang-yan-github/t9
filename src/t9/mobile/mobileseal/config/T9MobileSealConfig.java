package t9.mobile.mobileseal.config;

public class T9MobileSealConfig {

	public static final String SEAL_HEADER_IDENTIFIER = "TDSEAL";
	public static final int SEAL_HEADER_IDENTIFIER_LEN = SEAL_HEADER_IDENTIFIER.length();
	public static final int SEAL_HEADER_VERSION_LEN = 10;
	public static final int SEAL_HEADER_LEN = SEAL_HEADER_IDENTIFIER_LEN +SEAL_HEADER_VERSION_LEN +4+4;
	public static final String SEAL_HEADER_HEAD = "a"+SEAL_HEADER_IDENTIFIER_LEN+"header_identifier/a"+SEAL_HEADER_VERSION_LEN+"header_version/LpasswordLen/LimgType/LpicLen";
	public static final String[] seal_version_array = {"1.0.120729","a{passwordLen}password/a{picLen}pic"};
	
}
