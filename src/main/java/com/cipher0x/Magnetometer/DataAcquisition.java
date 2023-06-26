package com.cipher0x.Magnetometer;

import com.fazecast.jSerialComm.SerialPort;
import lombok.Data;

import javax.vecmath.Vector3d;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DataAcquisition {
    private SerialPortAccess serialPort;
    private SerialPort comPort;

    public DataAcquisition() throws IOException {
        serialPort = new SerialPortAccess();
        serialPort.loadSerialPortSettings();
        serialPort.openPort();
        comPort = serialPort.getComPort();
    }

    public List<Vector3d> getAcquisitionVectorBuffer() throws InterruptedException {
        Thread.sleep(100);
        byte[] readBuffer = new byte[comPort.bytesAvailable()];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);
        String hexStr = bytesToHex(readBuffer, numRead);
        List<String> hexLines = getMagnoLines(hexStr);
        List<String> validMagneticLines = getMagneticLines(hexLines);
        return validMagneticLines.stream().map(this::getMagVectors).toList();
    }

    private String bytesToHex(byte[] bytes, int count) {
        final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[count * 2];
        for (int j = 0; j < count; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public final List<String> getMagnoLines(String hexBuffer) {
        String[] blocks =  hexBuffer.split("55");
        return Arrays.stream(blocks).toList().stream().map(line -> "55" + line).collect(Collectors.toList());
    }

    public List<String> getMagneticLines(List<String> hexLine) {
        return hexLine.stream().filter(this::isValidLine).filter(line -> line.substring(0, 4).equals("5554")).collect(Collectors.toList());
    }

    public Vector3d getMagVectors(String magLine){
        List<Integer> nums = new ArrayList<>();
        for(int j = 0; j != 22 ; j+=2) {
            nums.add(Integer.parseInt(magLine.substring(j, j + 2), 16));
        }
        int magX = (nums.get(3) << 8) | nums.get(2);
        int magY = (nums.get(5) << 8) | nums.get(4);
        int magZ = (nums.get(7) << 8) | nums.get(6);

        return new Vector3d(magX, magY, magZ);
    }

    public boolean isValidLine(String line) {
        List<Integer> nums = new ArrayList<>();
        if(line.length() == 22) {
            for(int j = 0; j != 22 ; j+=2) {
                nums.add(Integer.parseInt(line.substring(j, j+2), 16));
            }
            Integer chkSum = nums.get(nums.size()-1);
            nums.remove(nums.size()-1);
            return (nums.stream().mapToInt(Integer::intValue).sum() & 0xff) == chkSum;
        }
        return false;
    }
}
