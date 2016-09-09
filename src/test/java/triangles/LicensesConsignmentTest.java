package triangles;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.kozak.triangles.services.LicenseMarketService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml" })
public class LicensesConsignmentTest {

	@Autowired
	LicenseMarketService licenseMarketService;

	@Before
	public void init() {
	}

	@After
	public void after() throws Exception {
	}

	@Test
	public void testLazyLoading() {
		licenseMarketService.confirmLicenseSelling(1, (byte) 2, 1, new JSONObject());
	}
}

