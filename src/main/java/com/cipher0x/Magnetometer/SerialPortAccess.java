package com.cipher0x.Magnetometer;

import com.cipher0x.Magnetometer.dto.SerialPortDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class SerialPortAccess {
    private SerialPortDto portInfo;
    @Getter
    private SerialPort comPort;
    public void loadSerialPortSettings() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        portInfo = objectMapper.readValue(new File("src/main/resources/serialPort.json"), SerialPortDto.class);
        comPort = SerialPort.getCommPort(portInfo.getSerial_port());
    }

    public void openPort() {
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
    }
}
