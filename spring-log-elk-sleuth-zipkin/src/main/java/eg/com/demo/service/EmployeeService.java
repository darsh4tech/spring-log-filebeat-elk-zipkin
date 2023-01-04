package eg.com.demo.service;

import java.util.List;

import eg.com.demo.model.Employee;

public interface EmployeeService {

	List<Employee> getAllEmployees();
	Employee getEmployee(int index);
}
