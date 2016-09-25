package org.columbia.adb.queryexpansion.query.model;

import org.columbia.adb.queryexpansion.utilities.QueryExpansionServiceUtilities;

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
