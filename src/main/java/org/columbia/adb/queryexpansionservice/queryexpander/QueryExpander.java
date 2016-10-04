package org.columbia.adb.queryexpansionservice.queryexpander;

import java.util.List;

import org.columbia.adb.queryexpansionservice.query.model.QueryResultInfo;

/***
 * Class responsible for incorporating user feedback to improve query
 * 
 * @author gauravmishra
 *
 */
public interface QueryExpander {

    /**
     * Expands query to more relevant query based on queryResultInfoList
     * 
     * @param query
     *            - Query to be expanded
     * @param queryResultInfoList
     *            - List of queryResultInfo
     * @return - Expanded Query
     */
    public String expandQuery(String query,
            List<QueryResultInfo> queryResultInfoList);

}
