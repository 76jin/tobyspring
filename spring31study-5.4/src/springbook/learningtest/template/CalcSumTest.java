package springbook.learningtest.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class CalcSumTest {

	public static String FILENAME = "numbers.txt";
	
	private Calculator calculator;
	private String filepath;
	
	@Before
	public void setUp() {
		this.calculator = new Calculator();
		this.filepath = getClass().getResource(FILENAME).getPath();
	}
	
	@Test
	public void sumOfNumbers() throws IOException {
		assertThat(calculator.calcSum(filepath), is(10));
	}
	
	@Test
	public void multiplyOfNumbers() throws IOException {
		assertThat(calculator.calcMultiply(filepath), is(24));
	}
	
	@Test
	public void cancatenateStrings() throws IOException {
		assertThat(calculator.concatenate(filepath), is("1234"));
	}
}
