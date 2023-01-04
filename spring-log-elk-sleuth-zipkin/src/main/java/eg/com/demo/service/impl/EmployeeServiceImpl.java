package eg.com.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import eg.com.demo.model.Employee;
import eg.com.demo.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

	static List<Employee> list = null;
	
	static {
		list = List.of(Employee.builder().empId(1).name("Ahmed").age(20).build(),
				Employee.builder().empId(1).name("Ali").age(23).build(),
				Employee.builder().empId(1).name("Karim").age(32).build(),
				Employee.builder().empId(1).name("Medo").age(34).build(),
				Employee.builder().empId(1).name("darsh").age(56).build(),
				Employee.builder().empId(1).name("Nona").age(67).build());
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		log.info("start calling getAllEmployees() .....");
		log.info("employees size: {}",list.size()); 
		return list;
	}

	@Override
	public Employee getEmployee(int index) {
		log.info("employees index: {}",index);
		log.info("employees : {}",list.get(index));
		return list.get(index);

	}

}
