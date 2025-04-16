package org.ihorzima.telegram_notification.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "catalog")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(exclude = "id")
public class Account {

    @Id
    @JsonIgnore
    private Long id;
    @Column(name = "landId")
    private String landId;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "notes")
    private String notes;
    @Column(name = "telegram_id")
    private String telegramId;

}
