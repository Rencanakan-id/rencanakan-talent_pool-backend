package rencanakan.id.talentpool;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "rencanakan.id.talentPool.stepdefinitions"
)
public class CucumberRunnerTest {
}