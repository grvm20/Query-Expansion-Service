package org.columbia.adb.queryexpansionservice.query.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.apache.commons.lang3.Validate;
import org.columbia.adb.queryexpansionservice.utilities.QueryExpansionServiceUtilities;

/***
 * Model class which application understands as query result frm web
 * 
 * @author gauravmishra
 *
 */
@Getter
public class QueryResultInfo {

    @NonNull
    private Integer id;
    @NonNull
    private String url;
    @NonNull
    private String title;
    @NonNull
    private String summary;

    @Setter
    private boolean isRelevant;

    public QueryResultInfo(@NonNull final Integer id, final String url,
            final String title, final String summary) {

        Validate.notEmpty(url, "URL is empty");
        Validate.notEmpty(title, "Title is empty");
        Validate.notEmpty(summary, "Summary is empty");

        this.id = id;
        this.url = url;
        this.title = title;
        this.summary = summary;

    }

    public String toString() {
        return QueryExpansionServiceUtilities.GSON.toJson(this);
    }

}
