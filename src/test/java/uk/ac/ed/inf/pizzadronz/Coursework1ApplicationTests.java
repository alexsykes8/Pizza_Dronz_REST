package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc


@SpringBootTest
class Coursework1ApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	/**
	 * Test the service with an endpoint that doesn't exist
	 * @throws Exception
	 */
	@Test
	void testBadURL() throws Exception {

		String testContent = "Test Content";
		MvcResult result = (MvcResult) mockMvc.perform(post("/validateorder")
						.contentType("application/json")
						.content(testContent))
				.andExpect(status().isBadRequest())
				.andReturn();
	}

}
