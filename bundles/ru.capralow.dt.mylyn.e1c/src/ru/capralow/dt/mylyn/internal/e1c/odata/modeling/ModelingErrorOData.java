/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.mylyn.internal.e1c.odata.modeling;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ru.capralow.dt.mylyn.internal.e1c.odata.CatalogOData;

/**
 * @author Aleksandr Kapralov
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelingErrorOData
    extends CatalogOData
{

    @JsonProperty("ДатаСоздания")
    public Date createdAt;

    @JsonProperty("Закрыта")
    public boolean closed;

    @JsonProperty("ДатаЗакрытия")
    public Date closedAt;

    @JsonProperty("Исправлена")
    public boolean fixed;

    @JsonProperty("ДатаИсправления")
    public Date fixedAt;

    @JsonProperty("Отозвана")
    public boolean withdrawn;

    @JsonProperty("ДатаОтзыва")
    public Date withdrawnAt;

    @JsonProperty("Статус")
    public String status;

    @JsonProperty("Протокол")
    public ModelingErrorProtocolOData[] protocol;

}
