package eg.com.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eg.com.demo.model.Employee;
import eg.com.demo.service.EmployeeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;
	
	@GetMapping
	public List<Employee> getAllEmployees(){
		
		return employeeService.getAllEmployees();
		
	}
	
	@GetMapping("/{id}")
	public Employee getEmployee(@PathVariable int id){
		
		return employeeService.getEmployee(id);
		
	}
	
}
