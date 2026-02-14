package poly.com.asm.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "Orderdetails")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "Orderid")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "Productid")
    private Product product;
    
}