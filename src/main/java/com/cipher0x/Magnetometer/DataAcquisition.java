package com.cipher0x.Magnetometer;

import lombok.Data;

import java.io.IOException;

@Data
public class DataAcquisition {
    private SerialPortAccess serialPort;

    public DataAcquisition() throws IOException {
        serialPort.loadSerialPortSettings();
    }
}
