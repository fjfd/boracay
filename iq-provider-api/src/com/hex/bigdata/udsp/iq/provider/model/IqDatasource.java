package com.hex.bigdata.udsp.iq.provider.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import org.apache.commons.lang.StringUtils;

/**
 * Created by JunjieM on 2019-1-25.
 */
public class IqDatasource extends Datasource {

    public IqDatasource(Datasource datasource) {
        super (datasource);
    }

    public int getMaxSize() {
        String value = getProperty ("max.data.size").getValue ();
        if (StringUtils.isBlank (value)) {
            value = "65535";
        }
        return Integer.valueOf (value);
    }

    public boolean getMaxSizeAlarm() {
        String value = getProperty ("max.data.size.alarm").getValue ();
        if (org.apache.commons.lang.StringUtils.isBlank (value)) {
            return true;
        }
        return Boolean.valueOf (value);
    }
}
