package com.cipher0x;

import com.fazecast.jSerialComm.SerialPort;

import javax.vecmath.Vector3d;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
  public static void main(String[] args) {
    SerialPort[] ports = SerialPort.getCommPorts();

    for (SerialPort port : ports) {
      System.out.println(port.getSystemPortName());
    }

    SerialPort comPort = SerialPort.getCommPorts()[2];
    comPort.openPort();
    comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
    Vector3d prevVec = null;
    Vector3d currentVec = null;
    Vector3d nextVec = null;
    try {
      while (true) {
        while (comPort.bytesAvailable() == 0)
          Thread.sleep(20);

        Thread.sleep(200);
        byte[] readBuffer = new byte[comPort.bytesAvailable()];
        int numRead = comPort.readBytes(readBuffer, readBuffer.length);
        //System.out.println("Read " + numRead + " bytes.");
        //System.out.println(hexToFormattedHex(bytesToHex(readBuffer)));

        //look for 55 54 then stop when you hit 55
        String hexStr = bytesToHex(readBuffer, numRead);
        List<String> hexLines = getMagnoLines(hexStr);
        List<String> validMagneticLines = getMagneticLines(hexLines);
        List<Vector3d> vecs = validMagneticLines.stream().map(Main::getMagVectors).toList();
        //vecs.stream().forEach(System.out::println);
        for(int i = 0; i < vecs.size() - 1; i ++) {
          if(currentVec != null) {
            currentVec = nextVec;
            nextVec = vecs.get(i);
          }
          else {
            currentVec = vecs.get(i);
            nextVec = vecs.get(i+1);
          }
          BigDecimal delta = BigDecimal.valueOf(currentVec.angle(nextVec));
          delta = delta.setScale(16, RoundingMode.CEILING);
          System.out.println(delta.toPlainString());
        }


      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    comPort.closePort();
  }

  public static Vector3d getMagVectors(String magLine){
    List<Integer> nums = new ArrayList<>();
    for(int j = 0; j != 22 ; j+=2) {
      nums.add(Integer.parseInt(magLine.substring(j, j + 2), 16));
    }
    int magX = (nums.get(3) << 8) | nums.get(2);
    int magY = (nums.get(5) << 8) | nums.get(4);
    int magZ = (nums.get(7) << 8) | nums.get(6);

    return new Vector3d(magX, magY, magZ);
  }
  public static List<String> getMagneticLines(List<String> hexLine) {
    return hexLine.stream().filter(Main::isValidLine).filter(line -> line.substring(0, 4).equals("5554")).collect(Collectors.toList());
  }

  public static boolean isValidLine(String line) {
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
  public static final List<String> getMagnoLines(String hexBuffer) {
    String[] blocks =  hexBuffer.split("55");
    return Arrays.stream(blocks).toList().stream().map(line -> "55" + line).collect(Collectors.toList());
  }
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  public static String bytesToHex(byte[] bytes, int count) {
    char[] hexChars = new char[count * 2];
    for (int j = 0; j < count; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  private static final String hexToFormattedHex(String strIn) {
    char[] formattedHexChars = new char[strIn.length() + (int) (strIn.length() / 2)];
    int newArrIndex = 0;
    for (int x = 0; x != strIn.length(); x = x+2) {
      formattedHexChars[newArrIndex] = strIn.charAt(x);
      formattedHexChars[newArrIndex + 1] = strIn.charAt(x + 1);
      formattedHexChars[newArrIndex + 2] = ' ';
      newArrIndex += 3;
    }
    return new String(formattedHexChars);
  }
}