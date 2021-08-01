/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Aleksandr Kapralov
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class E1cCatalog
{

    @JsonProperty("Ref_Key")
    public String refKey;

    @JsonProperty("DataVersion")
    public String dataVersion;

    @JsonProperty("DeletionMark")
    public boolean deletionMark;

    @JsonProperty("Owner_Key")
    public String ownerKey;

    @JsonProperty("Code")
    public String code;

    @JsonProperty("Description")
    public String description;

    @JsonProperty("Predefined")
    public boolean predefined;

    @JsonProperty("PredefinedDataName")
    public String predefinedDataName;

}
