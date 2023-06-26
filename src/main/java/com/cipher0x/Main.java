package com.cipher0x;

import com.cipher0x.Magnetometer.DataAcquisition;
import org.jfree.data.time.TimeSeries;

import java.io.IOException;

public class Main {
  static TimeSeries ts = new TimeSeries("delta");

  public static void main(String[] args) throws IOException, InterruptedException {
   DataAcquisition dataAcquisition = new DataAcquisition();
   while (true) {
          dataAcquisition.getAcquisitionVectorBuffer().forEach(System.out::println);
      }
  }
}
