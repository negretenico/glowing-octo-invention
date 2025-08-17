package com.negretenico.glowing.octo.invention.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.common.functionico.risky.Try;
import com.negretenico.glowing.octo.invention.models.Report;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileWriterService {

    private final Path reportsDir = Path.of("target", "fuzz-reports");

    public FileWriterService() {
        Try.of(()->Files.createDirectories(reportsDir))
                .onSuccess(s-> System.out.printf("FileWriterService: Created file %s",s))
                .onFailure(System.out::println);
    }

    public void writeReport(Report report) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "report_" + timestamp + ".txt";
        Path file = reportsDir.resolve(filename);
        Try.of(()-> Files.writeString(
                file,
                report.toString(), // assumes Report has a good toString()
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )).onSuccess(s-> System.out.printf("Report written to %s%n", file.toAbsolutePath()))
                .onFailure(s-> System.out.println("Failed to write report "));
    }
}
