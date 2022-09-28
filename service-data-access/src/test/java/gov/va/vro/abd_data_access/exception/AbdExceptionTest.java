package gov.va.vro.abd_data_access.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** @author warren @Date 9/6/22 */
class AbdExceptionTest {

  @Test
  public void test() {
    AbdException ex1 = new AbdException();
    AbdException ex2 = new AbdException("test");
    AbdException ex3 = new AbdException(ex2);
    AbdException ex4 = new AbdException("test", ex1);

    assertTrue(!ex1.getMessage().isEmpty());
    assertEquals("test", ex2.getMessage());
    assertNotEquals(ex1.getMessage(), ex2.getMessage());

    assertEquals(ex1.getMessage(), ex3.getMessage());
    assertEquals(ex2.getMessage(), ex3.getCause().getMessage());

    assertEquals(ex2.getMessage(), ex4.getMessage());
  }
}
