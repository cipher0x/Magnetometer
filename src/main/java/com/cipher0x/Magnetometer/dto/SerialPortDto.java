package com.cipher0x.Magnetometer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerialPortDto {
    private String serial_port;
    private String speed;
}
