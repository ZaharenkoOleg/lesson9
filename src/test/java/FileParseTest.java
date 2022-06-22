import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;


public class FileParseTest {

    ClassLoader classLoader = FileParseTest.class.getClassLoader();

    void pdfTest(ZipFile zipFile, ZipEntry zipEntry) throws Exception {
        PDF pdf = new PDF(zipFile.getInputStream(zipEntry));
        assertThat(pdf.text).contains("Boring, zzzzz. And more text. And more text");
    }

    void csvTest(ZipFile zipFile, ZipEntry zipEntry) throws Exception {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(zipFile.getInputStream(zipEntry), UTF_8))) {
            List<String[]> csv = csvReader.readAll();
            assertThat(csv).contains(
                    new String[]{"John", "Doe", "120 jefferson st.", "Riverside", " NJ", " 08075"}
            );
        }
    }

    void xlsTest(ZipFile zipFile, ZipEntry zipEntry) throws Exception {
        XLS xls = new XLS(zipFile.getInputStream(zipEntry));
        assertThat(
                xls.excel.getSheetAt(0)
                        .getRow(17)
                        .getCell(0)
                        .getStringCellValue())
                .isEqualTo("Дворянка");
    }

    @Test
    void zipTest() throws Exception {
        ZipFile zipFile = new ZipFile(classLoader.getResource("test.zip").getFile());
        ZipInputStream is = new ZipInputStream(classLoader.getResourceAsStream("test.zip"));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            if (entry.getName().contains(".pdf")) {
                pdfTest(zipFile, entry);
            } else if (entry.getName().contains("csv")) {
                csvTest(zipFile, entry);
            } else if (entry.getName().contains("xlsx")) {
                xlsTest(zipFile, entry);
            }
        }
    }

    @Test
    void jsonTest() throws Exception{

        JsonNode jsonNode;
        try (InputStream is = classLoader.getResourceAsStream("myJsonFile0.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(new InputStreamReader(is, UTF_8));
        }

        assertThat(jsonNode.get("email from expression").asText()).isEqualTo("Olwen.Drus@yopmail.com");
        assertThat(jsonNode.withArray("array").toString()).isEqualTo( "[\"Rosabelle\",\"Priscilla\",\"Viviene\",\"Ardys\",\"Georgina\"]");
        assertThat(jsonNode.get("random").asInt()).isEqualTo(1);
    }
}