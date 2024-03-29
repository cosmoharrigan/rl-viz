package rlVizLib.utilities;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mlee
 */
public class BitManipulationTest {



	@Test
	public void testinputEqualsOutput() {
		int A = 0;
		int B = 0;
		int amount = 0;
		int offset = 0;
		int packed = 0;
		int unpacked = 0;
		A = 5;
		B = 7;
		amount = 3;
		offset = 10; 
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
			unpacked = UtilityShop.extractSomeBitsAsIntFromInt(packed,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		assertTrue(A == unpacked);
	}
	
	@Test
	public void testPackMultiple() {
		int A1_8bit = 37;
		int A2_4bit = 12;
		int A3_3bit = 5;
		int A1_out = 0;
		int A2_out = 0;
		int A3_out = 0;
		int A = 0;
		int B = 0;
		int amount = 0;
		int offset = 0;
		int packed = 0;
		int unpacked = 0;
		A = A1_8bit;
		B = 0;
		amount = 8;
		offset = 0;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		A = A2_4bit;
		amount = 4;
		offset = 8;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,packed,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		A = A3_3bit;
		amount = 3;
		offset = 12;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,packed,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		
		A = 0;
		amount = 8;
		offset = 0;
		try {
			A1_out = UtilityShop.extractSomeBitsAsIntFromInt(packed,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		A = 0;
		amount = 4;
		offset = 8;
		try {
			A2_out = UtilityShop.extractSomeBitsAsIntFromInt(packed,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		A = 0;
		amount = 3;
		offset = 12;
		try {
			A3_out = UtilityShop.extractSomeBitsAsIntFromInt(packed,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		
		assertTrue(A1_8bit == A1_out);
		assertTrue(A2_4bit == A2_out);
		assertTrue(A3_3bit == A3_out);
	}
	
	@Test
	public void testOffset() {
		int A = 0;
		int B = 0;
		int amount = 0;
		int offset = 0;
		int packed = 0;
		int unpacked = 0;
		A = 5;
		B = 0;
		amount = 3;
		offset = 10;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		assertTrue(packed == (5 << 10));
	}
	
	@Test
	public void testAmount() {
		int A = 0;
		int B = 0;
		int amount = 0;
		int offset = 0;
		int packed = 0;
		int unpacked = 0;
		A = 13;
		B = 0;
		amount = 3;
		offset = 10;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
		} catch(Exception e) {
			System.err.println("Error: " + e);
			fail();
		}
		assertTrue(packed == (5 << 10));
	}
	
        
	@Test
	public void testAssertionFailureOnInvalidInput1() {
		int A = 0;
		int B = 0;
		int amount = 0;
		int offset = 0;
		int packed = 0;
		int unpacked = 0;
		boolean exception;
		A = -5;
		B = 7;
		amount = 3;
		offset = 10;
		
		exception = false;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
	}

		
		@Test
		public void testAssertionFailureOnInvalidInput3() {
			int A = 0;
			int B = 0;
			int amount = 0;
			int offset = 0;
			int packed = 0;
			int unpacked = 0;
			boolean exception;	
				A = 5;
		B = -7;
		amount = 3;
		offset = 10;
		
		exception = false;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
	}
	
	@Test
	public void testAssertionFailureOnInvalidInput4() {
		int A = 0;
		int B = 0;
		int amount = 0;
		int offset = 0;
		int packed = 0;
		int unpacked = 0;
		boolean exception;
			packed = -7;
		exception = false;
		try {
			UtilityShop.extractSomeBitsAsIntFromInt(packed,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
		
		A = 5;
		B = 7;
		amount = -3;
		offset = 10;
		
		exception = false;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
		exception = false;
		try {
			UtilityShop.extractSomeBitsAsIntFromInt(packed,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
		
		A = 5;
		B = 7;
		amount = 3;
		offset = -10;
		
		exception = false;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
		exception = false;
		try {
			UtilityShop.extractSomeBitsAsIntFromInt(packed,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
		
		A = 5;
		B = 7;
		amount = 3;
		offset = 29;
		
		exception = false;
		try {
			packed = UtilityShop.putSomeBitsFromIntIntoInt(A,B,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
		exception = false;
		try {
			UtilityShop.extractSomeBitsAsIntFromInt(packed,amount,offset);
		} catch (Exception e) {
			exception = true;
		} finally {
			assertTrue(exception);
		}
	}

	

}