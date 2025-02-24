package r01f.ui.vaadin.streams;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.server.StreamResource;

@FunctionalInterface
public interface VaadinThrowingStreamSource 
		 extends StreamResource.StreamSource {
	@Override
	public default InputStream getStream() {
		try {
			return this.getStreamThrowing();
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}
	public InputStream getStreamThrowing() throws IOException;
}
