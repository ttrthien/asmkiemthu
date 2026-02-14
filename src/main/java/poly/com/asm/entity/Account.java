package poly.com.asm.entity;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullname;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private String photo;
    private Boolean activated;
    private Boolean admin;

    @OneToMany(mappedBy = "account")
    private List<Order> orders;
    
}