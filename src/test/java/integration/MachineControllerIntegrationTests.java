package integration;

import com.factory.pal.PalApplication;
import com.factory.pal.model.request.MachineRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PalApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MachineControllerIntegrationTests {

    private static final String HOST = "http://localhost:";
    private static final String GET_MACHINE = "/internal-api/machine?page=0&count=10&order=DESC&sort=createdAt";
    private static final String GET_MACHINE_BY = "/internal-api/machine/";
    private static final String CREATE_MACHINE = "/internal-api/machine/";
    private static final String GET_MACHINE_STATS = "/internal-api/machine/stats";
    private static final String GET_MACHINE_STATS_BY_KEY = "/internal-api/machine/%s/stats";
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetMachinesWithStatus_200() {
        StringBuilder getMachineApi = new StringBuilder().append(HOST).append(port).append(GET_MACHINE);
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(getMachineApi.toString(), HttpMethod.GET, null, String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachineByKeyWithStatus_200() {
        StringBuilder getMachineApiByKey = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_BY).append("aufwickler");
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(getMachineApiByKey.toString(), HttpMethod.GET, null, String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachineByKeyWithStatus_422() {
        StringBuilder getMachineApiByKey = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_BY).append("blahblah");
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(getMachineApiByKey.toString(), HttpMethod.GET, null, String.class);
        assertEquals(422, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testSaveMachineParametersWithStatus_201() {
        StringBuilder createMachineParameters = new StringBuilder().append(HOST).append(port).append(CREATE_MACHINE);
        MachineRequest request = new MachineRequest();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("wheel", "25");
        request.setMachineKey("aufwickler");
        request.setParameters(parameters);
        ResponseEntity<String> responseEntity = this.restTemplate
                .postForEntity(createMachineParameters.toString(), request, String.class);
        assertEquals(201, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testSaveMachineParametersWithStatus_422() {
        StringBuilder createMachineParameters = new StringBuilder().append(HOST).append(port).append(CREATE_MACHINE);
        MachineRequest request = new MachineRequest();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("wheel", "25");
        request.setMachineKey("blah blah");
        request.setParameters(parameters);
        ResponseEntity<String> responseEntity = this.restTemplate
                .postForEntity(createMachineParameters.toString(), request, String.class);
        assertEquals(422, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testSaveMachineParametersWithEmptyMachineKey_400() {
        StringBuilder createMachineParameters = new StringBuilder().append(HOST).append(port).append(CREATE_MACHINE);
        MachineRequest request = new MachineRequest();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("wheel", "25");
        request.setParameters(parameters);
        ResponseEntity<String> responseEntity = this.restTemplate
                .postForEntity(createMachineParameters.toString(), request, String.class);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testSaveMachineParametersWithNoParameters_400() {
        StringBuilder createMachineParameters = new StringBuilder().append(HOST).append(port).append(CREATE_MACHINE);
        MachineRequest request = new MachineRequest();
        request.setMachineKey("blah blah");
        ResponseEntity<String> responseEntity = this.restTemplate
                .postForEntity(createMachineParameters.toString(), request, String.class);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachinesStatsWithStatus_200(){
        StringBuilder getMachineStatsApi = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_STATS);
        getMachineStatsApi.append("?");
        getMachineStatsApi.append("startDateTime=").append(LocalDateTime.now().minusDays(7));
        getMachineStatsApi.append("&");
        getMachineStatsApi.append("endDateTime=").append(LocalDateTime.now().plusDays(7));
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(getMachineStatsApi.toString(), HttpMethod.GET, null, String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachinesStatsWithoutStartDateTime_400(){
        StringBuilder getMachineStatsApi = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_STATS);
        getMachineStatsApi.append("?");
        getMachineStatsApi.append("endDateTime=").append(LocalDateTime.now().plusDays(7));
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(getMachineStatsApi.toString(), HttpMethod.GET, null, String.class);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachinesStatsWithoutEndDateTime_400(){
        StringBuilder getMachineStatsApi = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_STATS);
        getMachineStatsApi.append("?");
        getMachineStatsApi.append("startDateTime=").append(LocalDateTime.now().minusDays(7));
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(getMachineStatsApi.toString(), HttpMethod.GET, null, String.class);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachinesStatsByMachineKeyWithStatus_200(){
        StringBuilder getMachineStatsApi = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_STATS_BY_KEY);

        getMachineStatsApi.append("?");
        getMachineStatsApi.append("startDateTime=").append(LocalDateTime.now().minusDays(7));
        getMachineStatsApi.append("&");
        getMachineStatsApi.append("endDateTime=").append(LocalDateTime.now().plusDays(7));
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(String.format(getMachineStatsApi.toString(),"aufwickler"), HttpMethod.GET, null, String.class);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachinesStatsByInvalidMachineKeyWithStatus_422(){
        StringBuilder getMachineStatsApi = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_STATS_BY_KEY);

        getMachineStatsApi.append("?");
        getMachineStatsApi.append("startDateTime=").append(LocalDateTime.now().minusDays(7));
        getMachineStatsApi.append("&");
        getMachineStatsApi.append("endDateTime=").append(LocalDateTime.now().plusDays(7));
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(String.format(getMachineStatsApi.toString(),"blah"), HttpMethod.GET, null, String.class);
        assertEquals(422, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachinesStatsByMachineKeyWithStatus_400(){
        StringBuilder getMachineStatsApi = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_STATS_BY_KEY);

        getMachineStatsApi.append("?");
        getMachineStatsApi.append("endDateTime=").append(LocalDateTime.now().plusDays(7));
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(String.format(getMachineStatsApi.toString(),"aufwickler"), HttpMethod.GET, null, String.class);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetMachinesStatsByMachineKeyWithNoEndDateStatus_400(){
        StringBuilder getMachineStatsApi = new StringBuilder().append(HOST).append(port).append(GET_MACHINE_STATS_BY_KEY);

        getMachineStatsApi.append("?");
        getMachineStatsApi.append("startDateTime=").append(LocalDateTime.now().minusDays(7));
        ResponseEntity<String> responseEntity = this.restTemplate
                .exchange(String.format(getMachineStatsApi.toString(),"aufwickler"), HttpMethod.GET, null, String.class);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

}
