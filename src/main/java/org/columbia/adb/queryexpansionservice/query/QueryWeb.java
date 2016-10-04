package org.columbia.adb.queryexpansionservice.query;

import java.util.List;

import org.columbia.adb.queryexpansionservice.query.model.QueryResultInfo;

/***
 * Responsible for querying web
 * @author gauravmishra
 *
 */
public interface QueryWeb {

    /**
     * Responsible for fetching data from web and converting into a data model
     * which application understands
     * 
     * @param query
     *            - Query string to be queried upon
     * @param totalNumberOfResults
     *            - Total number of docs needed in query results
     * @return List of
     *         {@link org.columbia.adb.queryexpansionservice.query.model.QueryResultInfo
     *         QueryResultInfo} corresponding to a query
     */
    public List<QueryResultInfo> query(final String query,
            final int totalNumberOfResults) throws Exception;

}
