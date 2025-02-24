package r01f.ui.vaadin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

import com.google.common.collect.Range;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.locale.Language;
import r01f.util.types.Dates;
import r01f.util.types.Ranges;
import r01f.util.types.Strings;
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
//	FORMAT
/////////////////////////////////////////////////////////////////////////////////////////
	public static LocalDateTimeRenderer createLocalDateTimeRenderer(final Locale locale,
																	final FormatStyle style) {
		return new LocalDateTimeRenderer(() -> DateTimeFormatter.ofLocalizedDateTime(style)
		        												.withLocale(locale));
	}
	public static LocalDateRenderer createLocalDateRenderer(final Locale locale,
															final FormatStyle style) {
		return new LocalDateRenderer(() -> DateTimeFormatter.ofLocalizedDate(style)
		        											.withLocale(locale));
	}
	public static LocalDateTimeRenderer createLocalDateTimeRenderer(final Language lang,
																	final FormatStyle style) {
		return VaadinDates.createLocalDateTimeRenderer(Languages.getLocale(lang),
												   	   style);
	}
	public static LocalDateRenderer createLocalDateRenderer(final Language lang,
															final FormatStyle style) {
		return VaadinDates.createLocalDateRenderer(Languages.getLocale(lang),
												   style);
	}
	public static String formatDateByLang(final LocalDate lDate,final Language lang) {
		return lDate != null ? Dates.format(Date.from(lDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), 
								   			Dates.DATE_FORMATS_BY_LANG.get(lang))
							 : null;
	}
	public static String formatDateByLang(final Date date,final Language lang) {
		return formatDateByLang(date != null ? date.toInstant()
													.atZone(ZoneId.systemDefault())
													.toLocalDate() 
											  : null,
								lang);
	}
	public static String formatDateTimeByLang(final LocalDateTime localDate,final Language lang) {
		StringBuilder sb = new StringBuilder(Dates.formatterFor(lang)
												  .formatDateWithTimeToSeconds(VaadinDates.dateFrom(localDate)));
		return sb.toString();
	}
	public static String formatDateTimeByLang(final Date date,final Language lang) {
		return formatDateTimeByLang(date != null ? date.toInstant()
													   .atZone(ZoneId.systemDefault())
													   .toLocalDateTime() 
												 : null,
									lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGES
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Range<LocalDate> localDateRangeFrom(final Range<Date> dateRange) {
		LocalDate low = dateRange != null
				 	 && dateRange.hasLowerBound()
					 && dateRange.lowerEndpoint() != null ? dateRange.lowerEndpoint().toInstant()
																				     .atZone(ZoneId.systemDefault())
																				     .toLocalDate()
													    : null;
		LocalDate up = dateRange != null
					&& dateRange.hasUpperBound()
					&& dateRange.upperEndpoint() != null ? dateRange.upperEndpoint().toInstant()
																					.atZone(ZoneId.systemDefault())
																					.toLocalDate()
														 : null;
		Range<LocalDate> outRange = Ranges.guavaRangeFrom(low,up);
		return outRange;
	}
	public static final Range<Date> dateRangeFrom(final Range<LocalDate> localDateRange) {
		Date low = localDateRange != null
				&& localDateRange.hasLowerBound()
				&& localDateRange.lowerEndpoint() != null ? Date.from(localDateRange.lowerEndpoint()
																					.atStartOfDay()
																					.atZone(ZoneId.systemDefault())
																					.toInstant())
											 		  	  : null;
		Date up = localDateRange != null
			   && localDateRange.hasUpperBound() 
			   && localDateRange.upperEndpoint() != null ? Date.from(localDateRange.upperEndpoint()
																				   .atStartOfDay()
																				   .atZone(ZoneId.systemDefault())
																				   .toInstant())
														 : null;
		Range<Date> outRange = Ranges.guavaRangeFrom(low,up);
		return outRange;
	}
	
	public static final r01f.types.Range<Date> dateR01FRangeFrom(final Range<LocalDate> localDateRange) {
		Date low = localDateRange != null
				&& localDateRange.hasLowerBound()
				&& localDateRange.lowerEndpoint() != null ? Date.from(localDateRange.lowerEndpoint()
																					.atStartOfDay()
																					.atZone(ZoneId.systemDefault())
																					.toInstant())
											 		  	  : null;
		Date up = localDateRange != null
			   && localDateRange.hasUpperBound() 
			   && localDateRange.upperEndpoint() != null ? Date.from(localDateRange.upperEndpoint()
																				   .atStartOfDay()
																				   .atZone(ZoneId.systemDefault())
																				   .toInstant())
														 : null;
		if (low == null && up == null) return null;
		r01f.types.Range<Date> outRange = Ranges.rangeFrom(low,up);
		return outRange;
	}	
	public static String dateRangeWithMonthPrecissionAsString(final Range<LocalDate> range) {
		if (range == null) return null;
		String fmt = "yyyy/MM";
		String low = range.hasLowerBound() ? Dates.format(Date.from(range.lowerEndpoint()
																		 .atStartOfDay()
																		  .atZone(ZoneId.systemDefault())
																		  .toInstant()),
														  fmt)
										   : "";
		String up = range.hasUpperBound() ? Dates.format(Date.from(range.upperEndpoint()
																		.atStartOfDay()
																		.atZone(ZoneId.systemDefault())
																		.toInstant()),
														  fmt)
										   : "";
		String renderedRangeTemplate = "{} - {}";
		return Strings.customized(renderedRangeTemplate,
								  low,up);
	}
}
