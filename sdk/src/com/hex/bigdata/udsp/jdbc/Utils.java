package com.hex.bigdata.udsp.jdbc;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JunjieM on 2018-7-25.
 */
public class Utils {
    public static final Log LOG = LogFactory.getLog(Utils.class.getName());
    /**
     * 连接URL所需的前缀
     */
    public static final String URL_PREFIX = "jdbc:udsp://";

    /**
     * 默认端口号
     */
    public static final String DEFAULT_PORT = "9089";

    /**
     * Hive's default service name
     */
    public static final String DEFAULT_SERVICE_NAME = "";

    private static final String URI_JDBC_PREFIX = "jdbc:";

    private static final String URI_UDSP_PREFIX = "udsp:";

    public static JdbcConnectionParams parseURL(String uri) throws JdbcUriParseException, SQLException {
        JdbcConnectionParams connParams = new JdbcConnectionParams();

        if (!uri.startsWith(URL_PREFIX)) {
            throw new JdbcUriParseException("Bad URL format: Missing prefix " + URL_PREFIX);
        }

        // 解析连接uri
        // [scheme:][//authority][path][?query][#fragment]
        URI jdbcURI = URI.create(uri.substring(URI_JDBC_PREFIX.length()));

        // 提取 host 和 port
        connParams.setHost(jdbcURI.getHost());
        LOG.debug("Host: " + jdbcURI.getHost());
        connParams.setPort(jdbcURI.getPort());
        LOG.debug("Port: " + jdbcURI.getPort());

        // key=value pattern
        Pattern pattern = Pattern.compile("([^;]*)=([^;]*)[;]?");

        // 解析serviceName 和 session var 设置
        String sessVars = jdbcURI.getPath();
        LOG.debug("Path: " + sessVars);
        if ((sessVars != null) && !sessVars.isEmpty()) {
            String serviceName = "";
            // removing leading '/' returned by getPath()
            sessVars = sessVars.substring(1);
            if (!sessVars.contains(";")) {
                // only serviceName is provided
                serviceName = sessVars;
            } else {
                // we have serviceName followed by session parameters
                serviceName = sessVars.substring(0, sessVars.indexOf(';'));
                sessVars = sessVars.substring(sessVars.indexOf(';') + 1);
                if (sessVars != null) {
                    Matcher sessMatcher = pattern.matcher(sessVars);
                    while (sessMatcher.find()) {
                        if (connParams.getSessionVars().put(sessMatcher.group(1), sessMatcher.group(2)) != null) {
                            throw new JdbcUriParseException("Bad URL format: Multiple values for property "
                                    + sessMatcher.group(1));
                        }
                    }
                }
            }
            if (!serviceName.isEmpty()) {
                connParams.setServiceName(serviceName);
            }
        }

        // 解析 query
        String query = jdbcURI.getQuery();
        LOG.debug("Query: " + query);
        if (query != null) {
            Matcher confMatcher = pattern.matcher(query);
            while (confMatcher.find()) {
                connParams.getUdspVars().put(confMatcher.group(1), confMatcher.group(2));
            }
        }

        // 解析 fragment
        String fragment = jdbcURI.getFragment();
        LOG.debug("Fragment: " + fragment);
        if (fragment != null) {
            Matcher varMatcher = pattern.matcher(fragment);
            while (varMatcher.find()) {
                connParams.getDataVars().put(varMatcher.group(1), varMatcher.group(2));
            }
        }

        return connParams;
    }

    public static void main(String[] args) throws Exception {
        String url = "jdbc:udsp://192.168.1.61:9088/aaaaa;a=b;c=d?bb=qq;1=2#aDASD=3;ASD=ASD";
        JdbcConnectionParams params = parseURL(url);
        LOG.info(params);
    }
}
