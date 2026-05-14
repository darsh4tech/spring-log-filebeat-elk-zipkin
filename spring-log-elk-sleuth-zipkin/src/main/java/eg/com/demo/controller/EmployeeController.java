package eg.com.demo.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import eg.com.demo.model.Employee;
import eg.com.demo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;


@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {

	private final EmployeeService employeeService;
    private final RestClient restClient;

	EmployeeController(EmployeeService employeeService, RestClient.Builder builder) {
        this.employeeService = employeeService;
        this.restClient = builder.baseUrl("http://second-service:8002").build();
    }

	@GetMapping
	public List<Employee> getAllEmployees(){
		return employeeService.getAllEmployees();
	}
	
	@GetMapping("/{id}")
	public Employee getEmployee(@PathVariable int id){
		return employeeService.getEmployee(id);
	}

    @PostMapping("/send")
    public void sendToSecondService(@RequestBody Employee employee) {
        log.info("sendToSecondService started: {}", employee);
        String response = restClient.post()
                .uri("/second")
                .contentType(MediaType.APPLICATION_JSON)
                .body(employee)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, res) -> {
                    // Handle errors here
                    throw new RuntimeException("Failed to send logs: " + res.getStatusCode());
                })
                .body(String.class);

        log.info("Response from second service: {}", response);

    }

}
