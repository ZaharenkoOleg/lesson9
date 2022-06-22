import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideTest {
    @Test
    void Download() throws Exception {
        Configuration.browserSize = "1920x1080";
        open("https://github.com/junit-team/junit5/blob/main/README.md");


        File testFile = $("#raw-url").download();
        try (InputStream is = new FileInputStream(testFile)) {
            byte[] fileContent = is.readAllBytes();
            String asString = new String(fileContent, StandardCharsets.UTF_8);
            assertThat(asString).contains("Contributions to JUnit 5");
        }
    }

    @Test
    void UploadFile() {
        Configuration.browserSize = "1920x1080";
        open("https://fineuploader.com/demos.html");
        $("input[type='file']").uploadFromClasspath("1.txt");
        $(byText("1.txt has an invalid extension. Valid extension(s): jpeg, jpg, gif, png.")).shouldBe(visible);
    }
}
