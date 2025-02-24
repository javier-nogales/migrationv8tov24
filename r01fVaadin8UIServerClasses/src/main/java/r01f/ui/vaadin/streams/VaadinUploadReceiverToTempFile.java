package r01f.ui.vaadin.streams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.guids.OIDs;
import r01f.patterns.Provider;

/**
 * A vaadin's {@link Receiver} that "provides" a TEMP FileOutpuutStream where 
 * uploaded data will be written
 */
@Accessors(prefix="_")
public class VaadinUploadReceiverToTempFile 
  implements Receiver {

	private static final long serialVersionUID = 166701220645292254L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Provider<String> DEFAULT_FILE_NAME_PROVIDER = () -> OIDs.supplyShortOid();
/////////////////////////////////////////////////////////////////////////////////////////
//	RETURNED DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Provider<String> _fileNameProvider;
	@Getter private File _tempFile;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinUploadReceiverToTempFile() {
		this(DEFAULT_FILE_NAME_PROVIDER);
	}
	public VaadinUploadReceiverToTempFile(final Provider<String> fileNameProvider) {
		_fileNameProvider = fileNameProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//																			  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public OutputStream receiveUpload(final String filename,final String mimeType) {
		try {
			// ignore the uploaded file name and provide a new one
			String fileName = _fileNameProvider != null ? _fileNameProvider.provideValue()
														: filename;
			_tempFile = File.createTempFile(fileName,	// just the name
										 	null);		// will create a .tmp file
			FileOutputStream fos = new FileOutputStream(_tempFile);
			return fos;
		} catch (Throwable th) {
			Notification notif = new Notification("Could not upload blob<br/>",
							 					  th.getMessage(),
							 					  Notification.Type.ERROR_MESSAGE);
			notif.show(Page.getCurrent());
		}
		return null;
	}

}
