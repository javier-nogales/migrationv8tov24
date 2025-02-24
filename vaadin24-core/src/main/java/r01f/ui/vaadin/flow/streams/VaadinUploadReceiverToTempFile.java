package r01f.ui.vaadin.flow.streams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Receiver;

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
	private static final Provider<String> DEFAULT_FILE_NAME_PROVIDER = OIDs::supplyShortOid;
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
			H2 lbl = new H2("Could not upload blob");
			Pre lblEx = new Pre(th.getMessage());
			Notification notif = new Notification(lbl,
												  lblEx);
			notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
			notif.open();
		}
		return null;
	}

}
