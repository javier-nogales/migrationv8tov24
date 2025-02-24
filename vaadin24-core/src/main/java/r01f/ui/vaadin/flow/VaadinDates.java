package r01f.ui.vaadin.flow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.function.ValueProvider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.locale.Language;
import r01f.util.types.locale.Languages;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinDates {
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCAL DATE TIME CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	public static LocalDateTime localDateTimeFrom(final Date date) {
		return date != null ? date.toInstant()
								  .atZone(ZoneId.systemDefault())
								  .toLocalDateTime()
							: null;
	}
	public static Date dateFrom(final LocalDateTime localDate) {
		return localDate != null ? Date.from(localDate.atZone(ZoneId.systemDefault())
									   .toInstant())
								 : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCAL DATE CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	public static LocalDate localDateFrom(final Date date) {
		return date != null ? date.toInstant()
								  .atZone(ZoneId.systemDefault())
								  .toLocalDate()
							: null;
	}
	public static Date dateFrom(final LocalDate localDate) {
		return localDate != null ? Date.from(localDate.atStartOfDay()
									   				  .atZone(ZoneId.systemDefault())
									   .toInstant())
								 : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RENDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <SOURCE> LocalDateTimeRenderer<SOURCE> createLocalDateTimeRenderer(final ValueProvider<SOURCE,LocalDateTime> valProvider,
																	   				 final Locale locale,final FormatStyle style) {
		return new LocalDateTimeRenderer<>(valProvider,
										   () -> DateTimeFormatter.ofLocalizedDateTime(style)
																  .withLocale(locale),
										   "");	// null representation
	}
	public static <SOURCE> LocalDateRenderer<SOURCE> createLocalDateRenderer(final ValueProvider<SOURCE,LocalDate> valProvider,
																			 final Locale locale,final FormatStyle style) {
		return new LocalDateRenderer<SOURCE>(valProvider,
											 () -> DateTimeFormatter.ofLocalizedDate(style)
		        													.withLocale(locale),
		        						     "");	// null representation
	}
	public static <SOURCE> LocalDateTimeRenderer<SOURCE> createLocalDateTimeRenderer(final ValueProvider<SOURCE,LocalDateTime> valProvider,
																					 final Language lang,final FormatStyle style) {
		return VaadinDates.createLocalDateTimeRenderer(valProvider,
													   Languages.getLocale(lang),style);
	}
	public static <SOURCE> LocalDateRenderer<SOURCE> createLocalDateRenderer(final ValueProvider<SOURCE,LocalDate> valProvider,
																			 final Language lang,final FormatStyle style) {
		return VaadinDates.createLocalDateRenderer(valProvider,
												   Languages.getLocale(lang),style);
	}
}
