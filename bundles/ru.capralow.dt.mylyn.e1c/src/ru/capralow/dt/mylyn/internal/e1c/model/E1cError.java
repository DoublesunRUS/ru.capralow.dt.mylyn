/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Aleksandr Kapralov
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class E1cError
    extends E1cCatalog
{

    @JsonProperty("ДатаСоздания")
    public Date createdAt;

    @JsonProperty("Протокол")
    public E1cErrorProtocol[] protocol;

}
