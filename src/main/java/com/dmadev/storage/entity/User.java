package com.dmadev.storage.entity;


import com.dmadev.storage.api.constant.enums.Role;
import com.dmadev.storage.util.EmailValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@ToString
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username",unique = true,nullable = false)
    private String username;

    @Column(name = "first_name",length = 50)
    private String firstName;

    @Column(name = "last_name",length = 50)
    private String lastName;

    @Column(name = "password",nullable = false,length = 255)
    private String password;

    @Column(name="email",unique = true,nullable = false)
    @NotEmpty(message="Email cannot be empty")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    private Role role;

    public User(Long id, String firstName, String lastName, String email,String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        setEmail(email);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return "";
    }


    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // Переопределенный билдер для проверки email
    public static class UserBuilder {
        private String email;

        public UserBuilder email(String email) {
            if (!EmailValidator.isEmail(email)) {
                throw new IllegalArgumentException("Invalid email format");
            }
            this.email = email;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(this.id);
            user.setPassword(this.password);
            user.setFirstName(this.firstName);
            user.setLastName(this.lastName);
            user.setEmail(this.email);  // Проверка email
            return user;
        }
    }


    public void setEmail(String email){
      if(EmailValidator.isEmail(email)){
          this.email=email;
      }else {
          throw new IllegalArgumentException("Invalid email format");
      }
    };

}
