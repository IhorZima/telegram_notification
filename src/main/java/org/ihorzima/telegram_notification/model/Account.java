package org.ihorzima.telegram_notification.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(exclude = "id")
public class Account {

    @JsonIgnore
    private Long id;
    private String landId;
    private String fullName;
    private String address;
    private String phoneNumber;
    private String notes;
    private String telegramId;

}
