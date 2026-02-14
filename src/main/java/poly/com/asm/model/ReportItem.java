package poly.com.asm.model;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReportItem implements Serializable {
	@Id
	private Serializable group;
	private Double sum;
	private Long count;
}