package r01f.ui.vaadin.flow.streams;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.flow.server.InputStreamFactory;

import r01f.patterns.Factory;

/**
 * When a {@link Factory} that throws a CHECKED exception has to be used in a lambda expression the code is awful since the 
 * {@link Factory}'s create method has to be wrapped inside a try-catch statement
 * 
 * An usual {@link Factory} cannot be used if a CHECKED exception is thrown
 * <pre class='brush:java'>
 * 		Factory<MyObj> factory = () -> new MyObj();	<-- the compiler complains!!
 * </pre>
 * ... the "ugly" solution is to wrap the exception-throwing code inside a try-catch and rethrow a RuntimeException
 * <pre class='brush:java'>
 * 		InputStreamFactory isFactory = () -> {
 * 											try {
 * 												// code that creates the InputStream and throws an IOException
 * 											} catch (IOException th) {
 * 												throw new RuntimeException(th);
 * 											}
 * 					 			 	   });
 * </pre>
 * This verbosity can be avoided with this {@link VaadinThrowingInputStreamFactory} like:
 * <pre class='brush:java'>
 * 		VaadinThrowingInputStreamFactory factory = () -> {
 * 														// code that creates the InputStream and throws an IOException
 * 												   };
 * </pre>
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface VaadinThrowingInputStreamFactory {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	InputStream createInputStream() throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
	
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static InputStreamFactory unchecked(final VaadinThrowingInputStreamFactory f) {
		return () -> {
					try {
						return f.createInputStream();
					} catch (Throwable th) {
						throw new RuntimeException(th);
					}
			  };
	}
}
