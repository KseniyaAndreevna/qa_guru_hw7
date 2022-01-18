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

import static org.assertj.core.api.Assertions.assertThat;

public class FilesParsingTest {

    private ClassLoader cl = FilesParsingTest.class.getClassLoader();

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
