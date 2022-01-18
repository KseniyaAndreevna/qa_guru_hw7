package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesParsingTest {

    private ClassLoader cl = SelenideFilesTest.class.getClassLoader();

    @Test
    void parsePdfTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File pdfDownload = $(byText("PDF download")).download();
        PDF parsed = new PDF(pdfDownload);
        assertThat(parsed.author).contains("Marc Philipp");
    }

    @Test
    void parseXlsTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File pdfDownload = $(byText("PDF download")).download();
        PDF parsed = new PDF(pdfDownload);
        assertThat(parsed.author).contains("Marc Philipp");
    }

    @Test
    void parseCsvFile() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("files/example.csv")) {
            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = reader.readAll();

            assertThat(list)
                    .hasSize(3)
                    .contains(
                            new String[] {"Author", "Book"},
                            new String[] {"Block", "Apteka"},
                            new String[] {"Esenin", "Cherniy Chelovek"}
                    );
        }
    }

    @Test
    void zipTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("sample-zip-file.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                assertThat(zipEntry.getName()).isEqualTo("sample.txt");
            }

        }
        ZipFile zf = new ZipFile(new File(cl.getResource("sample-zip-file.zip").toURI()));
    }

    @Test
    void zipTestHw() throws Exception {
        ZipFile zipFile = new ZipFile(new File(cl.getResource("TestZip.zip").toURI()));

        ZipEntry pdfEntry = zipFile.getEntry("TestZip/pdf-test.pdf");
        try(InputStream stream = zipFile.getInputStream(pdfEntry)) {
            PDF pdfFile = new PDF(stream);
            assertThat(pdfFile.author).isEqualTo("Yukon Department of Education");
        }

        ZipEntry csvEntry = zipFile.getEntry("TestZip/csv-test.csv");
        try(InputStream stream = zipFile.getInputStream(csvEntry)) {
            CSVReader csvReader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = csvReader.readAll();
            System.out.println("ListSize: " + list.size());
            assertThat(list)
                    .hasSize(7)
                    .contains(
                    new String[] {"Username; Identifier;First name;Last name"}
            );
        }

        ZipEntry xlsxEntry = zipFile.getEntry("TestZip/xlsx-test.xlsx");
        try(InputStream stream = zipFile.getInputStream(xlsxEntry)) {
            XLS xlsxFile = new XLS(stream);
            assertThat(xlsxFile.excel.getSheetAt(0).getRow(0).getCell(1).toString())
                    .isEqualTo("First Name");

        }
    }
}
