package org.columbia.adb.queryexpansionservice.query;

import java.util.List;

import org.columbia.adb.queryexpansion.query.model.QueryResponseModel;

public interface QueryWeb {

    /**
     * Returns JSONObject corresponding to a query
     * 
     * @param query
     *            - Query string to be queried upon
     * @return
     */
    public List<QueryResponseModel> query(String query) throws Exception;

}
