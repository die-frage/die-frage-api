package com.diefrage.businessserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(name = "name")
    private String name;

    @Column(name = "group_number")
    private String groupNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "chat_id", unique = true)
    private String chatId;
}
