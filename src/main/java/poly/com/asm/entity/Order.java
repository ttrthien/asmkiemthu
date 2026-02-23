package poly.com.asm.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Createdate")
    private Date createDate = new Date();

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    private String address;

    @ManyToOne
    @JoinColumn(name = "Username")
    private Account account;

    // Bổ sung CascadeType.ALL để tự động lưu/cập nhật OrderDetail
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) 
    private List<OrderDetail> orderDetails;

    @Column(name = "status")
    private Integer status = 0; 

    @Column(name = "Paymentmethod")
    private String paymentMethod;
}