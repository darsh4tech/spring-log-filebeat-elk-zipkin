package eg.com.demo.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Employee {

	private int empId;
	private String name;
	private int age;
	
	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", name=" + name + ", age=" + age + "]";
	}
	
}
