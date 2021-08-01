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
public class E1cErrorProtocol

{

    @JsonProperty("LineNumber")
    public int lineNumber;

    @JsonProperty("Дата")
    public Date date;

    @JsonProperty("Автор_Key")
    public String authorKey;

    @JsonProperty("Комментарий")
    public String comment;

    @JsonProperty("Статус")
    public String status;

    @JsonProperty("КомуНаправлена_Key")
    public String assigneeKey;

}
