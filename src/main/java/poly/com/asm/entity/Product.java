package poly.com.asm.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String image;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private Double price;

    @Temporal(TemporalType.DATE)
    @Column(name = "Createdate")
    private Date createDate = new Date();

    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "Categoryid")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;
}