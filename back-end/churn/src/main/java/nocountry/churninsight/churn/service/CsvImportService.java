package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.CsvImportResponseDTO;
import nocountry.churninsight.churn.model.Cliente;
import nocountry.churninsight.churn.repository.ClientRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class CsvImportService {

    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Imports clients from a CSV file.
     * 
     * @param file CSV file containing client data
     * @return CsvImportResponseDTO with import statistics
     */
    public CsvImportResponseDTO importClientsFromCsv(MultipartFile file) {
        int successCount = 0;
        int failureCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CSVParser csvParser = new CSVFormat()
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines()
                    .parse(reader);

            for (CSVRecord record : csvParser) {
                try {
                    Cliente cliente = parseClienteFromCsv(record);
                    clientRepository.save(cliente);
                    successCount++;
                } catch (Exception e) {
                    logger.warn("Failed to import record: {}", record.getRecordNumber(), e);
                    failureCount++;
                }
            }

            return CsvImportResponseDTO.builder()
                    .successCount(successCount)
                    .failureCount(failureCount)
                    .message(String.format("Import completed. Successfully imported %d clients, %d failed.", 
                            successCount, failureCount))
                    .build();

        } catch (Exception e) {
            logger.error("Error reading CSV file", e);
            return CsvImportResponseDTO.builder()
                    .successCount(0)
                    .failureCount(-1)
                    .message("Error reading CSV file: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Parses a CSV record into a Cliente object.
     * 
     * @param record CSV record
     * @return Cliente object
     */
    private Cliente parseClienteFromCsv(CSVRecord record) {
        Cliente cliente = new Cliente();
        
        // Parse fields from CSV - adjust these based on your actual CSV structure
        // This is a basic example - modify according to your CSV columns
        
        try {
            if (record.isMapped("genero")) {
                cliente.setGenero(record.get("genero"));
            }
            if (record.isMapped("idoso")) {
                cliente.setIdoso(Integer.parseInt(record.get("idoso")));
            }
            if (record.isMapped("conjuge")) {
                cliente.setConjuge(record.get("conjuge"));
            }
            if (record.isMapped("dependentes")) {
                cliente.setDependentes(record.get("dependentes"));
            }
            if (record.isMapped("tempo_contrato")) {
                cliente.setTempoContrato(Integer.parseInt(record.get("tempo_contrato")));
            }
            // Add more fields as needed
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in CSV record", e);
        }

        return cliente;
    }
}
