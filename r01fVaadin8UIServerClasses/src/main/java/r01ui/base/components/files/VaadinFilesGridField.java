package r01ui.base.components.files;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.I18NKey;
import r01f.types.blob.FileCollectionItem;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.styles.VaadinValoTheme;
import r01f.util.types.Dates.DateLangFormat;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;
import r01ui.base.components.grid.VaadinGridButton;
import r01ui.base.components.window.VaadinProceedGateDialogWindow;
import r01ui.base.components.window.VaadinProceedGateDialogWindow.VaadinProceedGateProceed;


/**
 * A component to add files and show them in a grid
 *
 */
@Accessors(prefix="_")
public class VaadinFilesGridField
	 extends CustomField<Collection<FileCollectionItem>> {

	private static final long serialVersionUID = 1336796269137327695L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	private final UII18NService _i18n;
//////////////////////////////////////////////////////////////////////////////////////////
//	STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private OID _relatedOID;
/////////////////////////////////////////////////////////////////////////////////////////
//	UI
/////////////////////////////////////////////////////////////////////////////////////////
	private final HorizontalLayout _hlNewFile = new HorizontalLayout();
	private final Upload _upload;
	private final FileUploader _receiver;
	private final TextField _fileTitle;
	private final DateField _fileDate = new DateField();
	private final Button _btnNewFile;
	private final Button _btnAddFile;
	private final Button _btnEditFile;
	private final Button _btnCancelEditFile;
	private final Button _btnCancelNewFile;
	
	private final TextField _txtHiddenBlobToDelete = new TextField();

	private final Grid<FileCollectionItem> _fileGrid = new Grid<>();

	private final VerticalLayout _contentLayout = new VerticalLayout();
	private final VerticalLayout _rootLayout = new VerticalLayout();
	
	private Registration _registrationForAddFileUploadSucceeded;
	private Registration _registrationForEditFileButtonClick;
	private Registration _registrationForTxtHiddenBlobToDeleteValueChange;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private ArrayList<FileCollectionItem> _fileList; // component value
	@Getter private FileCollectionItem _editedFile;
	@Getter private FileCollectionItem _uploadedFile = null;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinFilesGridField(final UII18NService i18n) {
		_i18n = i18n;

		_receiver = new FileUploader();
		_upload = new Upload("", _receiver);

		_fileTitle = new TextField();
		_btnAddFile = new Button(i18n.getMessage("file.add"));
		_btnNewFile = new Button(i18n.getMessage("file.new"));
		_btnEditFile = new Button(i18n.getMessage("update"));
		_btnCancelEditFile = new Button(i18n.getMessage("cancel"));
		_btnCancelNewFile = new Button(i18n.getMessage("cancel"));

		_setStyles();
		_setBehaviour();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected Component initContent() {

		SimpleDateFormat formatter = new SimpleDateFormat(DateLangFormat.of(_i18n.getCurrentLanguage()).getDateFormat());

		_contentLayout.setId("r01fui-filelist");
		//_contentLayout.addStyleName(ValoTheme.LAYOUT_CARD);

		Label gridCaption = new Label(_i18n.getMessage("file.content.title"));
		HorizontalLayout hlHeader = new HorizontalLayout();
		hlHeader.addComponent(gridCaption);
		_rootLayout.setSizeFull();

		_fileTitle.setCaption(_i18n.getMessage("file.title"));
		_fileTitle.setRequiredIndicatorVisible(true);
		_fileDate.setCaption(_i18n.getMessage("file.date"));
		_fileDate.setPlaceholder(_i18n.getMessage("file.date.format"));
		_fileDate.setRequiredIndicatorVisible(true);
		_fileDate.setDateFormat(DateLangFormat.of(_i18n.getCurrentLanguage()).getDateFormat());
		_fileDate.setValue(LocalDate.now());
		_btnCancelNewFile.setVisible(false);

		_upload.setImmediateMode(false);
		_upload.addStartedListener(_receiver);
		_upload.addSucceededListener(_receiver);
		_upload.addFailedListener(_receiver);
		_upload.setButtonStyleName(VaadinValoTheme.HIDE);
		_upload.setCaption(_i18n.getMessage("file.select"));
		_upload.addStyleName("file-select-required");

		_btnAddFile.setEnabled(false);
		_btnEditFile.setVisible(false);
		_btnCancelEditFile.setVisible(false);

		_hlNewFile.addComponents(_fileTitle,
								 _fileDate,
								 _upload,
								 _btnCancelNewFile,
								 _btnAddFile,
								 _btnCancelEditFile,
								 _btnEditFile);
		_hlNewFile.setWidthFull();
		_hlNewFile.setExpandRatio(_fileTitle, 2);
		_hlNewFile.setExpandRatio(_fileDate, 1);
		_hlNewFile.setExpandRatio(_upload, 2);
		_hlNewFile.setComponentAlignment(_btnAddFile, Alignment.BOTTOM_RIGHT);
		_hlNewFile.setComponentAlignment(_btnEditFile, Alignment.BOTTOM_RIGHT);
		_hlNewFile.setComponentAlignment(_btnCancelEditFile, Alignment.BOTTOM_RIGHT);
		_hlNewFile.setComponentAlignment(_btnCancelNewFile, Alignment.BOTTOM_RIGHT);
		_hlNewFile.setVisible(false);
		_fileTitle.setWidthFull();


		_fileGrid.setWidthFull();
		_fileGrid.setHeightMode(HeightMode.UNDEFINED);
		_fileGrid.addColumn(FileCollectionItem::getFileTitle)
				 .setCaption(_i18n.getMessage("file.title"))
				 .setExpandRatio(1)
				 .setId("fileTitle");
		_fileGrid.addColumn(file -> formatter.format(file.getFileDate()))
				 .setCaption(_i18n.getMessage("file.date"))
				 .setExpandRatio(0)
				 .setId("fileDate");
		_fileGrid.addColumn(file -> file.getFile().getName())
				 .setCaption(_i18n.getMessage("file.name"))
				 .setExpandRatio(1)
				 .setId("fileName");
		_fileGrid.addComponentColumn(file -> {
										  HorizontalLayout hl = new HorizontalLayout();
										  Button btnEdit = new VaadinGridButton(_i18n.getMessage("edit"),
																				  VaadinIcons.PENCIL,
																				  clickEvent -> {
																					  _editedFile = file;
																					  _hlNewFile.setVisible(true);
																					  _fileTitle.setValue(file.getFileTitle());
																					  _fileDate.setValue(file.getFileDate()
																							  				 .toInstant()
																							  				 .atZone(ZoneId.systemDefault())
																							  				 .toLocalDate());
																					  _btnAddFile.setVisible(false);
																					  _btnEditFile.setVisible(true);
																					  _btnCancelEditFile.setVisible(true);
																					  _upload.setVisible(false);
																					  _btnCancelNewFile.setVisible(false);
																					  _btnNewFile.setEnabled(false);
																				  },
																				  VaadinValoTheme.NO_PADDING);
										  Button btnDelete = new VaadinGridButton(_i18n.getMessage("delete"),
																				  VaadinIcons.TRASH,
																				  clickEvent -> {
																					  			 // show a proceed-gate dialog before deleting
																								 this.getUI()
																									 .addWindow(createDisposeProceedGatePopUp(_i18n,
																											 								  file.getFileTitle(),
																											 								  // what happens when the user allows the panel disposal
																											 								  () -> { // raise an event
																											 									  _fileList.remove(file);
																											 									  _fileGrid.setItems(_fileList);
																											 									  _txtHiddenBlobToDelete.setValue(file.getBlobOid().asString());
																											 									  
																											   }));
																				  },
																				  VaadinValoTheme.NO_PADDING);
										  Button btnDownload = new VaadinGridButton(_i18n.getMessage("file.download"),
												  									VaadinIcons.DOWNLOAD,
												  									clickEvent -> {
												  										// nothing
																				    },
																				  VaadinValoTheme.NO_PADDING);
										  Resource resource = new FileResource(file.getFile());
										  FileDownloader fileDownloader = new FileDownloader(resource);
										  fileDownloader.extend(btnDownload);
										  hl.addComponents(btnDownload,
												  		   btnEdit,
												  		   btnDelete);
										  return hl;
											})
									.setExpandRatio(0)
						  			.setId("fileActions");

		hlHeader.addComponent(_btnNewFile);
		_rootLayout.addComponents(hlHeader,
								  _fileGrid,
								  _hlNewFile);
		hlHeader.setWidthFull();
		hlHeader.setComponentAlignment(_btnNewFile, Alignment.MIDDLE_RIGHT);
		hlHeader.setComponentAlignment(gridCaption, Alignment.MIDDLE_LEFT);
		_contentLayout.addComponents(_rootLayout);
		return _contentLayout;
	}
	private void _setStyles() {
		_btnAddFile.addStyleName(ValoTheme.BUTTON_PRIMARY);
		_btnNewFile.addStyleName(ValoTheme.BUTTON_PRIMARY);
		_btnEditFile.addStyleName(ValoTheme.BUTTON_PRIMARY);
		_upload.setWidthUndefined();
	}
	private void _setBehaviour() {
		_btnNewFile.addClickListener(event -> {
			_hlNewFile.setVisible(true);
			_btnNewFile.setEnabled(false);
			_btnCancelNewFile.setVisible(true);
		});
		_btnCancelNewFile.addClickListener(event -> {
											_hlNewFile.setVisible(false);
											_btnNewFile.setEnabled(true);
											_btnCancelNewFile.setVisible(false);
		});
		_btnAddFile.addClickListener(event -> { // TODO VALIDATE TITLE AND FILE ??
											_upload.submitUpload();
		});
		_btnEditFile.addClickListener(event -> { // TODO VALIDATE TITLE AND FILE ??
											if (Strings.isNullOrEmpty(_fileTitle.getValue())) {
												Notification.show(_i18n.getMessage("file.validation.title"));
											} else if (_fileDate.getValue() == null) {
												Notification.show(_i18n.getMessage("file.validation.date"));
											} else {
												int pos = _fileList.indexOf(_editedFile);
												_btnEditFile.setVisible(false);
												_btnAddFile.setVisible(true);
												_btnCancelEditFile.setVisible(false);
												_upload.setVisible(true);
												_editedFile.setFileTitle(_fileTitle.getValue());
												_editedFile.setFileDate(Date.from(_fileDate.getValue()
																						   .atStartOfDay(ZoneId.systemDefault())
																						   .toInstant()));
												_fileList.set(pos, _editedFile);
												_fileGrid.setItems(_fileList);
												_fileGrid.recalculateColumnWidths();
												_fileTitle.setValue("");
												_fileDate.setValue(LocalDate.now());
												_hlNewFile.setVisible(false);
												_btnNewFile.setEnabled(true);
											}
		});
		_btnCancelEditFile.addClickListener(event -> {
											_btnEditFile.setVisible(false);
											_btnCancelEditFile.setVisible(false);
											_btnAddFile.setVisible(true);
											_upload.setVisible(true);
											_fileTitle.setValue("");
											_fileDate.setValue(LocalDate.now());
											_hlNewFile.setVisible(false);
											_btnNewFile.setEnabled(true);
		});

		_upload.addChangeListener(event -> {
										if (Strings.isNOTNullOrEmpty(_fileTitle.getValue())
										 && _fileDate.getValue() != null
										 && event.getFilename() != null) {
											_btnAddFile.setEnabled(true);
										} else {
											_btnAddFile.setEnabled(false);
										}
										_receiver.setSelectedFile(true);
		});
		_fileTitle.addValueChangeListener(event -> {
											if (_receiver.isSelectedFile()
											 && _fileDate.getValue() != null
											 && Strings.isNOTNullOrEmpty(event.getValue())) {
												_btnAddFile.setEnabled(true);
											} else {
												_btnAddFile.setEnabled(false);
											}
		});
		_fileDate.addValueChangeListener(event -> {
											if (_receiver.isSelectedFile()
											 && Strings.isNOTNullOrEmpty(_fileTitle.getValue())
											 && event.getValue() != null) {
												_btnAddFile.setEnabled(true);
											} else {
												_btnAddFile.setEnabled(false);
											}
		});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void editFileButtonClickListener(final ClickListener listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_registrationForEditFileButtonClick == null) { 
			_registrationForEditFileButtonClick = _btnEditFile.addClickListener(listener);
		}
	}
	public void addFileUploadSucceededListener(final SucceededListener listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_registrationForAddFileUploadSucceeded == null) { 
			_registrationForAddFileUploadSucceeded = _upload.addSucceededListener(listener);
		}
	}
	
	public void txtHiddenBlobToDeleteValueChangeListener(final ValueChangeListener<String> listener) {
		// Add listener only first time, if not we will have an exponential list of listeners raising events
		if (_registrationForTxtHiddenBlobToDeleteValueChange == null) { 
			_registrationForTxtHiddenBlobToDeleteValueChange = _txtHiddenBlobToDelete.addValueChangeListener(listener);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<FileCollectionItem> getValue() {
		return CollectionUtils.hasData(_fileList)
					? _fileList
					: Lists.newArrayList();
	}
	@Override
	protected void doSetValue(final Collection<FileCollectionItem> value) {
		if (CollectionUtils.hasData(value)) {
			_fileGrid.setItems(value);
			_fileList = (ArrayList<FileCollectionItem>) value;
			_fileGrid.setVisible(true);
		} else {
			_fileGrid.setVisible(false);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void addStyleNamesToContentLayout(final String... styles) {
		_contentLayout.addStyleNames(styles);
	}
	public void addStyleNamesToRootLayout(final String... styles) {
		_rootLayout.addStyleNames(styles);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	// Implement both receiver that saves upload in a file and
	// listener for successful upload
	@Accessors(prefix = "_")
	class FileUploader implements Receiver, StartedListener, SucceededListener, FailedListener {
		private static final long serialVersionUID = 3590645812839087146L;
		@Getter private File _file;
		@Getter @Setter boolean _selectedFile;

		@Override
		public void uploadStarted(final StartedEvent event) {
			 /*if(!event.getMIMEType().equals(MimeTypes.TEXT_PLAIN.getName())) {
				 new Notification("Tipo no valido para el fichero",
									 Notification.Type.ERROR_MESSAGE)
						.show(Page.getCurrent());
				 _upload.interruptUpload();
			 }*/
			// TODO VALIDATIONS??
		}

	    @Override
		public OutputStream receiveUpload(final String filename,
	                                      final String mimeType) {
	       FileOutputStream fos = null; // Stream to write to
	       try {
				// Open the file for writing.
				_file = new File("/tmp/" + filename);
				fos = new FileOutputStream(_file);

			} catch (final java.io.FileNotFoundException e) {
				new Notification(_i18n.getMessage("file.open-error"),
								 e.getMessage(),
								 Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
				return null;
			}
	        _uploadedFile = new FileCollectionItem();
			_uploadedFile.setFileTitle(_fileTitle.getValue());
			_uploadedFile.setFileDate(Date.from(_fileDate.getValue()
											  			 .atStartOfDay(ZoneId.systemDefault())
											  			 .toInstant()));
			_uploadedFile.setCreationDate(new Date());
			_uploadedFile.setRelatedObjectOid(_relatedOID);
			_uploadedFile.setFile(_file);
			return fos; // Return the output stream to write to
	    }

	    @Override
		public void uploadSucceeded(final SucceededEvent event) {

	    	_btnAddFile.setEnabled(false);
			if (CollectionUtils.isNullOrEmpty(_fileList)) {
				_fileList = new ArrayList<>();
				_fileGrid.setVisible(true);
			}
			_fileList.add(_uploadedFile);
			_fileGrid.setItems(_fileList);
			_fileGrid.recalculateColumnWidths();
			_fileTitle.setValue("");
			_fileDate.setValue(LocalDate.now());
			_receiver.setSelectedFile(false);
			_hlNewFile.setVisible(false);
			_btnNewFile.setEnabled(true);
	    }

		@Override
		public void uploadFailed(final FailedEvent event) {
				new Notification(_i18n.getMessage("file.open-error"),
									 Notification.Type.ERROR_MESSAGE)
						.show(Page.getCurrent());
				_btnNewFile.setEnabled(true);
		}
	};
/////////////////////////////////////////////////////////////////////////////////////////
//	Dispose proceed gate
/////////////////////////////////////////////////////////////////////////////////////////
	public static VaadinProceedGateDialogWindow createDisposeProceedGatePopUp(final UII18NService i18n,
																			  final String name,
																			  final VaadinProceedGateProceed proceed) {
		return new VaadinProceedGateDialogWindow(i18n,
												 I18NKey.forId("delete.confirm.title"),
												 I18NKey.forId("delete.confirm.proceed.gate.message.withparams"),
												 null,
												 // what happens when the user allows the panel disposal
												 proceed,null,	// null = no cancel handler
												 // puzzle check
												 text -> text.trim().equals(name.trim()),
												 // params for message
												 name);
	}
}
