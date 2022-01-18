package guru.qa;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFilesTest {

    private ClassLoader cl = SelenideFilesTest.class.getClassLoader();

    @Test
    void downloadTest() throws Exception {
        open("https://github.com/junit-team/junit5/blob/main/LICENSE.md");
        File licenseFile = $("#raw-url").download();
        try (InputStream is = new FileInputStream(licenseFile)) {
            assertThat(new String(is.readAllBytes(), StandardCharsets.UTF_8))
                    .contains("Eclipse Public License - v 2.0");
        }
    }

    @Test
    void uploadFile() {
        open("https://the-internet.herokuapp.com/upload");
        $("input[type='file']").uploadFromClasspath("upload.txt");
        $("#file-submit").click();
        $("#uploaded-files").shouldHave(Condition.text("upload.txt"));
    }
}
