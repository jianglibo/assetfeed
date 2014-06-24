package com.m3958.vertxio.assetfeed.unit;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.vertxio.assetfeed.utils.Utils;

public class UtilsTest {

  @Test
  public void t1(){
    String fn = "abc";
    String fn1= "abc.txt";
    String fn2 = "abc.xx.exe";
    
    Assert.assertEquals("", Utils.getFileExtWithDot(fn));
    Assert.assertEquals(".txt", Utils.getFileExtWithDot(fn1));
    Assert.assertEquals(".exe", Utils.getFileExtWithDot(fn2));
    
  }
}
