package poly.com.asm.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Categories")
public class Category {
    @Id
    private String id;
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}