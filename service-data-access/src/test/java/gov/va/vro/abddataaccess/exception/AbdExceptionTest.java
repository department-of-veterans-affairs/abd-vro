package gov.va.vro.abddataaccess.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class AbdExceptionTest {

  @Test
  public void test() {
    final AbdException ex1 = new AbdException();
    final AbdException ex2 = new AbdException("test");
    final AbdException ex3 = new AbdException(ex2);
    final AbdException ex4 = new AbdException("test", ex1);

    assertFalse(ex1.getMessage().isEmpty());
    assertEquals("test", ex2.getMessage());
    assertNotEquals(ex1.getMessage(), ex2.getMessage());

    assertEquals(ex1.getMessage(), ex3.getMessage());
    assertEquals(ex2.getMessage(), ex3.getCause().getMessage());

    assertEquals(ex2.getMessage(), ex4.getMessage());
  }
}
