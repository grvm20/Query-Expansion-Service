package org.columbia.adb.queryexpansionservice.query.model;

import org.columbia.adb.queryexpansionservice.utilities.QueryExpansionServiceUtilities;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QueryResponseModel {

    private String url;
    private String title;
    private String description;

    public String toString() {
        return QueryExpansionServiceUtilities.GSON.toJson(this);
    }

}
