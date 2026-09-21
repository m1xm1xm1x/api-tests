package config;

public class ApiConfig {
    public static final String BASE_URL = System.getenv("testRailApiUrl");
    public static final String EMAIL = System.getenv("testRailApiEmail");
    public static final String API_KEY = System.getenv("testRailApiKey");
    public static final String PROJECT_ID = System.getenv("testRailProject");
    public static final String SUITE_ID = System.getenv("testRailSuite");
    public static final String SECTION_ID = System.getenv("testRailSection");
    public static final String TARGET_SECTION_ID = System.getenv("testRailTargetSection");
}
