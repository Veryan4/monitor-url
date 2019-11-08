package monitor;

import monitor.MonitorController;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MonitorController.class)
class MonitorControllerTest {

		@Autowired
    private MockMvc mockMvc;

    List<Status> expectedList = new ArrayList<>();

		@Test
    public void main() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("monitor"))
                .andExpect(model().attribute("message", equalTo("Monitoring hasn't started yet")))
								.andExpect(model().attribute("interval", equalTo(1000)))
                .andExpect(model().attribute("statuses", is(expectedList)))
								.andExpect(model().attribute("url", equalTo("https://google.com")));

        MvcResult mvcResult = resultActions.andReturn();
        ModelAndView mv = mvcResult.getModelAndView();
    }

		@Test
    public void submit() throws Exception {
        ResultActions resultActions = mockMvc.perform( MockMvcRequestBuilders.post("/")
                .content(asJsonString(new Monitor(new ArrayList<>(),1000, "https://google.com", "Monitor is Started")))
      					.contentType(MediaType.APPLICATION_JSON)
      					.accept(MediaType.APPLICATION_JSON))
      					.andExpect(status().isOk())
      					.andExpect(MockMvcResultMatchers.jsonPath("$.interval").value(1000))
      					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Monitor is Started"))
      					.andExpect(MockMvcResultMatchers.jsonPath("$.url").value("https://google.com"));

        MvcResult mvcResult = resultActions.andReturn();
        ModelAndView mv = mvcResult.getModelAndView();
    }

		@Test
    public void stop() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/stop"))
                .andExpect(status().isOk())
                .andExpect(view().name("monitor"))
                .andExpect(model().attribute("message", equalTo("Monitor hasn't Started yet")))
								.andExpect(model().attribute("interval", equalTo(1000)))
                .andExpect(model().attribute("statuses", is(expectedList)))
								.andExpect(model().attribute("url", equalTo("https://google.com")));

        MvcResult mvcResult = resultActions.andReturn();
        ModelAndView mv = mvcResult.getModelAndView();
    }

		public static String asJsonString(final Object obj) {
		    try {
		        final ObjectMapper mapper = new ObjectMapper();
		        final String jsonContent = mapper.writeValueAsString(obj);
		        return jsonContent;
		    } catch (Exception e) {
		        throw new RuntimeException(e);
		    }
		}

}
