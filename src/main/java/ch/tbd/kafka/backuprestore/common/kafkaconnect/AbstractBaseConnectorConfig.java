package ch.tbd.kafka.backuprestore.common.kafkaconnect;

import ch.tbd.kafka.backuprestore.config.ComposableConfig;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.SSEAlgorithm;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Width;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.types.Password;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.common.config.ConfigDef.Range.atLeast;

/**
 * Class AbstractBaseConnectorConfig.
 * This represents TODO.
 *
 * @author iorfinoa
 * @version $$Revision$$
 */
public abstract class AbstractBaseConnectorConfig extends AbstractConfig implements ComposableConfig {

    private static Logger logger = LoggerFactory.getLogger(AbstractBaseConnectorConfig.class);
    public static final String S3_BUCKET_CONFIG = "s3.bucket.name";
    private static final String S3_BUCKET_DOC = "The S3 Bucket.";
    private static final String S3_BUCKET_DISPLAY = "S3 Bucket";

    public static final String S3_PROFILE_NAME_CONFIG = "s3.profile.name";
    private static final String S3_PROFILE_NAME_DOC = "The profile name to use in Amazon configuration.";
    private static final String S3_PROFILE_NAME_DEFAULT = null;
    private static final String S3_PROFILE_NAME_DISPLAY = "S3 Profile name";

    private static final String SSEA_CONFIG = "s3.ssea.name";
    private static final String SSEA_CONFIG_DOC = "The S3 Server Side Encryption Algorithm.";
    private static final String SSEA_CONFIG_DISPLAY = "S3 Server Side Encryption Algorithm";
    private static final String SSEA_DEFAULT = "";

    private static final String SSE_CUSTOMER_KEY = "s3.sse.customer.key";
    private static final String SSE_CUSTOMER_KEY_DOC = "The S3 Server Side Encryption Customer-Provided Key (SSE-C).";
    private static final Password SSE_CUSTOMER_KEY_DEFAULT = new Password(null);
    private static final String SSE_CUSTOMER_KEY_DISPLAY = "S3 Server Side Encryption Customer-Provided Key (SSE-C)";

    private static final String SSE_KMS_KEY_ID_CONFIG = "s3.sse.kms.key.id";
    private static final String SSE_KMS_KEY_ID_DOC = "The name of the AWS Key Management Service (AWS-KMS) key to be used for server side "
            + "encryption of the S3 objects. No encryption is used when no key is provided, but"
            + " it is enabled when '" + SSEAlgorithm.KMS + "' is specified as encryption "
            + "algorithm with a valid key name.";
    private static final String SSE_KMS_KEY_ID_DEFAULT = "";
    private static final String SSE_KMS_KEY_DISPLAY = "S3 Server Side Encryption Key";

    private static final String ACL_CANNED_CONFIG = "s3.acl.canned";
    private static final String ACL_CANNED_DOC = "An S3 canned ACL header value to apply when writing objects.";
    private static final String ACL_CANNED_DEFAULT = null;
    private static final String ACL_CANNED_DISPLAY = "S3 Canned ACL";

    public static final String S3_PROXY_URL_CONFIG = "s3.proxy.url";
    private static final String S3_PROXY_URL_DOC = "S3 Proxy settings encoded in URL syntax. This property is meant to be used only if you"
            + " need to access S3 through a proxy.";
    private static final String S3_PROXY_URL_DEFAULT = "";
    private static final String S3_PROXY_URL_DISPLAY = "S3 Proxy URL Settings";

    public static final String S3_PROXY_USER_CONFIG = "s3.proxy.user";
    private static final String S3_PROXY_USER_DOC = "S3 Proxy User. This property is meant to be used only if you"
            + " need to access S3 through a proxy. Using ``"
            + S3_PROXY_USER_CONFIG
            + "`` instead of embedding the username and password in ``"
            + S3_PROXY_URL_CONFIG
            + "`` allows the password to be hidden in the logs.";
    private static final String S3_PROXY_USER_DEFAULT = null;
    private static final String S3_PROXY_USER_DISPLAY = "S3 Proxy User";

    public static final String S3_PROXY_PASS_CONFIG = "s3.proxy.password";
    private static final String S3_PROXY_PASS_DOC = "S3 Proxy Password. This property is meant to be used only if you"
            + " need to access S3 through a proxy. Using ``"
            + S3_PROXY_PASS_CONFIG
            + "`` instead of embedding the username and password in ``"
            + S3_PROXY_URL_CONFIG
            + "`` allows the password to be hidden in the logs.";
    private static final Password S3_PROXY_PASS_DEFAULT = new Password(null);
    private static final String S3_PROXY_PASS_DISPLAY = "S3 Proxy Password";

    public static final String S3_REGION_CONFIG = "s3.region";
    private static final String S3_REGION_DOC = "The AWS region to be used the connector.";
    private static final String S3_REGION_DEFAULT = Regions.DEFAULT_REGION.getName();
    private static final String S3_REGION_DISPLAY = "AWS region";

    public static final String S3_SERVICE_ENDPOINT_CONFIG = "s3.service.endpoint";
    private static final String S3_SERVICE_ENDPOINT_DOC = "The Service endpoint to be used for requests.";
    private static final String S3_SERVICE_ENDPOINT_DEFAULT = null;
    private static final String S3_SERVICE_ENDPOINT_DISPLAY = "Service Endpoint";
    
    public static final String S3_WAN_MODE_CONFIG = "s3.wan.mode";
    private static final String S3_WAN_MODE_DOC = "Use S3 accelerated endpoint.";
    private static final boolean S3_WAN_MODE_DEFAULT = false;
    private static final String S3_WAN_MODE_DISPLAY = "Use S3 Accelerated Endpoint";
    
    public static final String S3_PATH_STYLE_ACCESS_CONFIG = "s3.path.style.access";
    private static final String S3_PATH_STYLE_ACCESS_DOC = "Enable path style access.";
    private static final boolean S3_PATH_STYLE_ACCESS_DEFAULT = false;
    private static final String S3_PATH_STYLE_ACCESS_DISPLAY = "S3 Path Style Access";
    
    public static final String AWS_SIGNER_OVERRIDE_CONFIG = "aws.signer.override";
    private static final String AWS_SIGNER_OVERRIDE_DOC = "AWS Client Signer Override.";
    private static final String AWS_SIGNER_OVERRIDE_DEFAULT = null;
    private static final String AWS_SIGNER_OVERRIDE_DISPLAY = "AWS Client Signer Override";
    
    public static final int S3_RETRY_MAX_BACKOFF_TIME_MS = (int) TimeUnit.HOURS.toMillis(24);

    public static final String S3_RETRY_BACKOFF_CONFIG = "s3.retry.backoff.ms";
    private static final String S3_RETRY_BACKOFF_DOC = "How long to wait in milliseconds before attempting the first retry of a failed S3 request."
    												+ "Upon a failure, this connector may wait up to twice as long as the previous wait, up to the maximum number of retries. "
    										        + "This avoids retrying in a tight loop under failure scenarios.";
    public static final int S3_RETRY_BACKOFF_DEFAULT = 200;
    private static final String S3_RETRY_BACKOFF_DISPLAY = "Retry backoff";

    public static final String S3_PART_RETRIES_CONFIG = "s3.part.retries";
    public static final int S3_PART_RETRIES_DEFAULT = 3;

    public static final String HEADERS_USE_EXPECT_CONTINUE_CONFIG =
            "s3.http.send.expect.continue";
    public static final boolean HEADERS_USE_EXPECT_CONTINUE_DEFAULT =
            ClientConfiguration.DEFAULT_USE_EXPECT_CONTINUE;

    public static final String ROTATE_SCHEDULE_INTERVAL_MS_CONFIG = "rotate.schedule.interval.ms";
    public static final String ROTATE_SCHEDULE_INTERVAL_MS_DOC =
            "The time interval in milliseconds to periodically invoke file commits. This configuration "
                    + "ensures that file commits are invoked every configured interval. Time of commit will be "
                    + "adjusted to 00:00 of selected timezone. Commit will be performed at scheduled time "
                    + "regardless previous commit time or number of messages. This configuration is useful when"
                    + " you have to commit your data based on current server time, like at the beginning of "
                    + "every hour. The default value -1 means that this feature is disabled.";
    public static final long ROTATE_SCHEDULE_INTERVAL_MS_DEFAULT = -1L;
    public static final String ROTATE_SCHEDULE_INTERVAL_MS_DISPLAY = "Rotate Schedule Interval (ms)";

    public static final String ROTATE_INTERVAL_MS_CONFIG = "rotate.interval.ms";
    public static final String
            ROTATE_INTERVAL_MS_DOC =
            "The time interval in milliseconds to invoke file commits. This configuration ensures that "
                    + "file commits are invoked every configured interval. This configuration is useful when "
                    + "data ingestion rate is low and the connector didn't write enough messages to commit "
                    + "files. The default value -1 means that this feature is disabled.";
    public static final long ROTATE_INTERVAL_MS_DEFAULT = -1L;
    public static final String ROTATE_INTERVAL_MS_DISPLAY = "Rotate Interval (ms)";


    protected AbstractBaseConnectorConfig(ConfigDef conf, Map<String, String> props) {
        super(conf, props);

        logger.info("AbstractBaseConnectorConfig(ConfigDef conf, Map<String, String> props)");
    }

    @Override
    public Object get(String key) {
        return super.get(key);
    }

    public static ConfigDef conf() {
        final String group = "abs-config-s3";
        int orderInGroup = 0;

        ConfigDef configDef = new ConfigDef();
        configDef.define(
                S3_BUCKET_CONFIG,
                Type.STRING,
                Importance.HIGH,
                S3_BUCKET_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                S3_BUCKET_DISPLAY
        );

        configDef.define(
                S3_PROFILE_NAME_CONFIG,
                Type.STRING,
                S3_PROFILE_NAME_DEFAULT,
                Importance.HIGH,
                S3_PROFILE_NAME_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                S3_PROFILE_NAME_DISPLAY
        );

        List<String> validSsea = new ArrayList<>(SSEAlgorithm.values().length + 1);
        validSsea.add("");
        for (SSEAlgorithm algo : SSEAlgorithm.values()) {
            validSsea.add(algo.toString());
        }
        configDef.define(
                SSEA_CONFIG,
                Type.STRING,
                SSEA_DEFAULT,
                ConfigDef.ValidString.in(validSsea.toArray(new String[validSsea.size()])),
                Importance.LOW,
                SSEA_CONFIG_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                SSEA_CONFIG_DISPLAY,
                new SseAlgorithmRecommender()
        );

        configDef.define(
                SSE_CUSTOMER_KEY,
                Type.PASSWORD,
                SSE_CUSTOMER_KEY_DEFAULT,
                Importance.LOW,
                SSE_CUSTOMER_KEY_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                SSE_CUSTOMER_KEY_DISPLAY
        );

        configDef.define(
                SSE_KMS_KEY_ID_CONFIG,
                Type.STRING,
                SSE_KMS_KEY_ID_DEFAULT,
                Importance.LOW,
                SSE_KMS_KEY_ID_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                SSE_KMS_KEY_DISPLAY,
                new SseKmsKeyIdRecommender()
        );

        configDef.define(
                ACL_CANNED_CONFIG,
                Type.STRING,
                ACL_CANNED_DEFAULT,
                new CannedAclValidator(),
                Importance.LOW,
                ACL_CANNED_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                ACL_CANNED_DISPLAY
        );


        configDef.define(
                S3_PROXY_URL_CONFIG,
                ConfigDef.Type.STRING,
                S3_PROXY_URL_DEFAULT,
                Importance.LOW,
                S3_PROXY_URL_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                S3_PROXY_URL_DISPLAY
        );

        configDef.define(
                S3_PROXY_USER_CONFIG,
                ConfigDef.Type.STRING,
                S3_PROXY_USER_DEFAULT,
                Importance.LOW,
                S3_PROXY_USER_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                S3_PROXY_USER_DISPLAY
        );

        configDef.define(
                S3_PROXY_PASS_CONFIG,
                Type.PASSWORD,
                S3_PROXY_PASS_DEFAULT,
                Importance.LOW,
                S3_PROXY_PASS_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                S3_PROXY_PASS_DISPLAY
        );

        configDef.define(
                S3_REGION_CONFIG,
                Type.STRING,
                S3_REGION_DEFAULT,
                new RegionValidator(),
                Importance.MEDIUM,
                S3_REGION_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                S3_REGION_DISPLAY,
                new RegionRecommender()
        );
        
        configDef.define(
                S3_SERVICE_ENDPOINT_CONFIG,
                Type.STRING,
                S3_SERVICE_ENDPOINT_DEFAULT,
                Importance.MEDIUM,
                S3_SERVICE_ENDPOINT_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                S3_SERVICE_ENDPOINT_DISPLAY
        );
        
        configDef.define(
                AWS_SIGNER_OVERRIDE_CONFIG,
                Type.STRING,
                AWS_SIGNER_OVERRIDE_DEFAULT,
                Importance.MEDIUM,
                AWS_SIGNER_OVERRIDE_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                AWS_SIGNER_OVERRIDE_DISPLAY
        );
        
        configDef.define(
                S3_PATH_STYLE_ACCESS_CONFIG,
                Type.BOOLEAN,
                S3_PATH_STYLE_ACCESS_DEFAULT,
                Importance.MEDIUM,
                S3_PATH_STYLE_ACCESS_DOC,
                group,
                ++orderInGroup,
                Width.LONG,
                S3_PATH_STYLE_ACCESS_DISPLAY
        );

        configDef.define(
                S3_WAN_MODE_CONFIG,
                Type.BOOLEAN,
                S3_WAN_MODE_DEFAULT,
                Importance.MEDIUM,
                "Use S3 accelerated endpoint.",
                group,
                ++orderInGroup,
                Width.LONG,
                "S3 accelerated endpoint enabled"
        );

        configDef.define(
                S3_RETRY_BACKOFF_CONFIG,
                Type.LONG,
                S3_RETRY_BACKOFF_DEFAULT,
                atLeast(0L),
                Importance.LOW,
                "How long to wait in milliseconds before attempting the first retry "
                        + "of a failed S3 request. Upon a failure, this connector may wait up to twice as "
                        + "long as the previous wait, up to the maximum number of retries. "
                        + "This avoids retrying in a tight loop under failure scenarios.",
                group,
                ++orderInGroup,
                Width.SHORT,
                "Retry Backoff (ms)"
        );

        configDef.define(
                S3_PART_RETRIES_CONFIG,
                Type.INT,
                S3_PART_RETRIES_DEFAULT,
                atLeast(0),
                Importance.MEDIUM,
                "Maximum number of retry attempts for failed requests. Zero means no retries. "
                        + "The actual number of attempts is determined by the S3 client based on multiple "
                        + "factors, including, but not limited to - "
                        + "the value of this parameter, type of exception occurred, "
                        + "throttling settings of the underlying S3 client, etc.",
                group,
                ++orderInGroup,
                Width.LONG,
                "S3 Part Upload Retries"
        );

        configDef.define(
                HEADERS_USE_EXPECT_CONTINUE_CONFIG,
                Type.BOOLEAN,
                HEADERS_USE_EXPECT_CONTINUE_DEFAULT,
                Importance.LOW,
                "Enable/disable use of the HTTP/1.1 handshake using EXPECT: 100-CONTINUE during "
                        + "multi-part upload. If true, the client will wait for a 100 (CONTINUE) response "
                        + "before sending the request body. Else, the client uploads the entire request "
                        + "body without checking if the server is willing to accept the request.",
                group,
                ++orderInGroup,
                Width.SHORT,
                "S3 HTTP Send Uses Expect Continue"
        );

        configDef.define(
                ROTATE_SCHEDULE_INTERVAL_MS_CONFIG,
                Type.LONG,
                ROTATE_SCHEDULE_INTERVAL_MS_DEFAULT,
                Importance.MEDIUM,
                ROTATE_SCHEDULE_INTERVAL_MS_DOC,
                group,
                ++orderInGroup,
                Width.MEDIUM,
                ROTATE_SCHEDULE_INTERVAL_MS_DISPLAY
        );

        configDef.define(
                ROTATE_INTERVAL_MS_CONFIG,
                Type.LONG,
                ROTATE_INTERVAL_MS_DEFAULT,
                Importance.HIGH,
                ROTATE_INTERVAL_MS_DOC,
                group,
                ++orderInGroup,
                Width.MEDIUM,
                ROTATE_INTERVAL_MS_DISPLAY
        );

        return configDef;
    }

    public String getBucketName() {
        return getString(S3_BUCKET_CONFIG);
    }

    public String getS3ProfileNameConfig() {
        return getString(S3_PROFILE_NAME_CONFIG);
    }

    public String getSsea() {
        return getString(SSEA_CONFIG);
    }

    public String getSseCustomerKey() {
        return getPassword(SSE_CUSTOMER_KEY).value();
    }

    public String getSseKmsKeyId() {
        return getString(SSE_KMS_KEY_ID_CONFIG);
    }

    public CannedAccessControlList getCannedAcl() {
        return CannedAclValidator.ACLS_BY_HEADER_VALUE.get(getString(ACL_CANNED_CONFIG));
    }

    public String getRegionConfig() {
        return getString(S3_REGION_CONFIG);
    }
    public String getServiceEndpointConfig() {
    	return getString(S3_SERVICE_ENDPOINT_CONFIG);
    }
    public boolean usePathStyleAccess() {
    	return getBoolean(S3_PATH_STYLE_ACCESS_CONFIG);
    }
    public String getAWSSignerOverrideConfig() {
    	return getString(AWS_SIGNER_OVERRIDE_CONFIG);
    }
    public long getRotateIntervalMs() {
        return getLong(ROTATE_INTERVAL_MS_CONFIG);
    }

    public int getS3PartRetries() {
        return getInt(S3_PART_RETRIES_CONFIG);
    }

    public boolean useExpectContinue() {
        return getBoolean(HEADERS_USE_EXPECT_CONTINUE_CONFIG);
    }

    private static class RegionRecommender implements ConfigDef.Recommender {
        @Override
        public List<Object> validValues(String name, Map<String, Object> connectorConfigs) {
            return Arrays.<Object>asList(RegionUtils.getRegions());
        }

        @Override
        public boolean visible(String name, Map<String, Object> connectorConfigs) {
            return true;
        }
    }

    private static class SseAlgorithmRecommender implements ConfigDef.Recommender {
        @Override
        public List<Object> validValues(String name, Map<String, Object> connectorConfigs) {
            List<SSEAlgorithm> list = Arrays.asList(SSEAlgorithm.values());
            return new ArrayList<>(list);
        }

        @Override
        public boolean visible(String name, Map<String, Object> connectorConfigs) {
            return true;
        }
    }

    private static class SseKmsKeyIdRecommender implements ConfigDef.Recommender {

        @Override
        public List<Object> validValues(String name, Map<String, Object> connectorConfigs) {
            return new LinkedList<>();
        }

        @Override
        public boolean visible(String name, Map<String, Object> connectorConfigs) {
            return SSEAlgorithm.KMS.toString()
                    .equalsIgnoreCase((String) connectorConfigs.get(SSEA_CONFIG));
        }
    }

    private static class RegionValidator implements ConfigDef.Validator {
        @Override
        public void ensureValid(String name, Object region) {
            String regionStr = ((String) region).toLowerCase().trim();
            if (RegionUtils.getRegion(regionStr) == null) {
                throw new ConfigException(
                        name,
                        region,
                        "Value must be one of: " + Utils.join(RegionUtils.getRegions(), ", ")
                );
            }
        }

        @Override
        public String toString() {
            return "[" + Utils.join(RegionUtils.getRegions(), ", ") + "]";
        }
    }

    private static class CannedAclValidator implements ConfigDef.Validator {
        protected static final Map<String, CannedAccessControlList> ACLS_BY_HEADER_VALUE = new HashMap<>();
        protected static final String ALLOWED_VALUES;

        static {
            List<String> aclHeaderValues = new ArrayList<>();
            for (CannedAccessControlList acl : CannedAccessControlList.values()) {
                ACLS_BY_HEADER_VALUE.put(acl.toString(), acl);
                aclHeaderValues.add(acl.toString());
            }
            ALLOWED_VALUES = Utils.join(aclHeaderValues, ", ");
        }

        @Override
        public void ensureValid(String name, Object cannedAcl) {
            if (cannedAcl == null) {
                return;
            }
            String aclStr = ((String) cannedAcl).trim();
            if (!ACLS_BY_HEADER_VALUE.containsKey(aclStr)) {
                throw new ConfigException(name, cannedAcl, "Value must be one of: " + ALLOWED_VALUES);
            }
        }

        @Override
        public String toString() {
            return "[" + ALLOWED_VALUES + "]";
        }
    }

}
