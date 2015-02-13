package springbook.learningtest.template;

public interface LineCallback<T> {

	public T doSomethingWithLine(String line, T value);
}
