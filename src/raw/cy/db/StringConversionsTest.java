package raw.cy.db;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringConversionsTest {

  @Test
  public void testFormatStringString() {
    String s = StringConversions.format("T9hRfggF", "T9h");
    System.out.println(s);
  }

  @Test
  public void testFormatString() {
    String s = StringConversions.format("sSjSHKjde");
    String s2 = StringConversions.format("SjSHKjde");
    String s3 = StringConversions.format("sSjSHKjdD");
    System.out.println("sSjSHKjde ==>"+s);
    System.out.println("SjSHKjde ==>"+s2);
    System.out.println("sSjSHKjdD ==>"+s3);
  }

}
