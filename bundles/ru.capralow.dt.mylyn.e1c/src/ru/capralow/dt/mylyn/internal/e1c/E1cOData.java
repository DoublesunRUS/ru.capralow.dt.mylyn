/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Aleksandr Kapralov
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class E1cOData
{
    @JsonProperty("odata.metadata")
    public String metadata;

    @JsonProperty("value")
    public E1cError[] value;
}
