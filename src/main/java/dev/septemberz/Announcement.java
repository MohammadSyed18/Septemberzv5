package dev.septemberz;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record Announcement(@NotBlank String title, @NotBlank String details, @NotBlank String audience, @NotNull LocalDate deadline, @NotBlank String contact) {}
