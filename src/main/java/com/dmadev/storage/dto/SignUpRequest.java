package com.dmadev.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {

    @Schema(description = "Никнейм пользователя", example = "JohnMacclein")
    @Size(min = 3, max = 50, message = "Никнейм пользователя должно содержать от 3 до 50 символов")
    @NotBlank(message = "Никнейм пользователя не должно быть пустым")
    private String username;
    @Schema(description = "Имя пользователя", example = "John")
    @Size(min = 2, max = 50, message = "Имя пользователя должно содержать от 2 до 50 символов")
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String firstname;

    @Schema(description = "Фамилия пользователя", example = "Macclein")
    @Size(min = 2, max = 50, message = "Фамилия пользователя должно содержать от 2 до 50 символов")
    @NotBlank(message = "Фамилия пользователя не должна быть пустой")
    private String lastname;

    @Schema(description = "Адрес электронной почты", example = "johnmacclein@gmail.com")
    @Size(min = 11, max = 255, message = "Адрес электронной почты должен содержать от 11 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;
}
