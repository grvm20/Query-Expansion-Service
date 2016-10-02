package org.columbia.adb.queryexpansionservice.query;

import java.util.List;

import org.columbia.adb.queryexpansionservice.query.model.QueryResponseModel;

public interface QueryWeb {

    /**
     * Returns List<QueryResponseModel> corresponding to a query
     * 
     * @param query
     *            - Query string to be queried upon
     * @return
     */
    public List<QueryResponseModel> query(String query) throws Exception;

}
