package org.ihorzima.telegram_notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "catalog")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @Id
    private Long id;
    @Column(name = "land_id")
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
