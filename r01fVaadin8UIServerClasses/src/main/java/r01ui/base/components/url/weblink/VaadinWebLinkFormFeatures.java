package r01ui.base.components.url.weblink;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents the features of a WebLink.
 * <p>
 * Utilizes the builder pattern to configure the options for the {@link R01UIUrlBuilderWebLinkForm}.
 * </p>
 * 
 * <h2>Usage Examples</h2>
 * <p>
 * Below are some examples of how to construct an instance of {@code VaadinWebLinkFormFeatures} using the {@code builder()} method:
 * </p>
 * 
 * <p>
 * These examples create an instance of {@code VaadinWebLinkFormFeatures} with the default configuration.
 * </p>
 * 
 * <pre>
 * {@code
 * VaadinWebLinkFormFeatures formFeatures = VaadinWebLinkFormFeatures.builder()
 *																   .build());
 * }
 * </pre>
 * 
 * <pre>
 * {@code
 * VaadinWebLinkFormFeatures formFeatures = VaadinWebLinkFormFeatures.builder()
 *																   .byDefault());
 * }
 * </pre>
 * 
 * <p>
 * These example creates an instance of {@code VaadinWebLinkFormFeatures} with the full configuration.
 * </p>
 * 
 * <pre>
 * {@code
 * VaadinWebLinkFormFeatures formFeatures = VaadinWebLinkFormFeatures.builder()
 *																   .showLinkTextInput(true)
 *																   .requireLinkTextInput(true)
 *																   .showLinkDescriptionInput(true)
 *																   .showLinkOpeningFeatures(true)
 *																   .mandatoryOpenInNewTab(false)
 *																   .build());
 * }
 * </pre>
 * <p>
 * Custom options:
 * <ul>
 *   <li>{@code showLinkTextInput(true/false)}: Displays the input field for the link text.</li>
 *   <li>{@code requireLinkTextInput(true/false)}: Makes the input field for the link text required.</li>
 *   <li>{@code showLinkDescriptionInput(true/false)}: Displays the input field for the link description.</li>
 *   <li>{@code showLinkOpeningFeatures(true/false)}: Displays the link opening features.</li>
 *   <li>{@code mandatoryOpenInNewTab(true/false)}: Requires the link to open in a new tab.</li>
 * </ul>
 * </p>
 * 
 * @see VaadinWebLinkFormFeaturesBuilder
 */
@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class VaadinWebLinkFormFeatures 
  implements Serializable {

	private static final long serialVersionUID = 2935521576908437829L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Show the link text input
	 */
	@Getter private final boolean _showLinkTextInput;
	/**
	 * Require link text input
	 */
	@Getter private final boolean _requiredLinkTextInput;
	/**
	 * Show the link description input
	 */
	@Getter private final boolean _showLinkDescriptionInput;
	/**
	 * Show link opening features
	 */
	@Getter private final boolean _showLinkOpeningFeatures;
	/**
	 * Opening in new window
	 */
	@Getter private final boolean _mandatoryOpenInNewTab;
	
	///// Default values
	private static final boolean DEFAULT_SHOW_LINK_TEXT_INPUT = true;
	private static final boolean DEFAULT_REQUIRED_LINK_TEXT_INPUT = true;
	private static final boolean DEFAULT_SHOW_LINK_DESCRIPTION_INPUT = true;
	private static final boolean DEFAULT_SHOW_LINK_OPENING_FEATURES = true;
	private static final boolean DEFAULT_MANDATORY_OPEN_IN_NEW_TAB = false;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER 
/////////////////////////////////////////////////////////////////////////////////////////
	public static VaadinWebLinkFormFeaturesBuilder builder() {
		return new VaadinWebLinkFormFeaturesBuilder();
	}
	public static class VaadinWebLinkFormFeaturesBuilder {
		private boolean _showLinkTextInput = DEFAULT_SHOW_LINK_TEXT_INPUT;
		private boolean _requiredLinkTextInput = DEFAULT_REQUIRED_LINK_TEXT_INPUT;
		private boolean _showLinkDescriptionInput = DEFAULT_SHOW_LINK_DESCRIPTION_INPUT;
		private boolean _showLinkOpeningFeatures = DEFAULT_SHOW_LINK_OPENING_FEATURES;
		private boolean _mandatoryOpenInNewTab = DEFAULT_MANDATORY_OPEN_IN_NEW_TAB;
		
		///// Methods to configure options
		public VaadinWebLinkFormFeaturesBuilder showLinkTextInput(final boolean show) {
			_showLinkTextInput = show;
			return this;
		}
		public VaadinWebLinkFormFeaturesBuilder requireLinkTextInput(final boolean require) {
			_requiredLinkTextInput = require;
			return this;
		}
		public VaadinWebLinkFormFeaturesBuilder showLinkDescriptionInput(final boolean show) {
			_showLinkDescriptionInput = show;
			return this;
		}
		public VaadinWebLinkFormFeaturesBuilder showLinkOpeningFeatures(final boolean show) {
			_showLinkOpeningFeatures = show;
			return this;
		}
		public VaadinWebLinkFormFeaturesBuilder mandatoryOpenInNewTab(final boolean mandatory) {
			_mandatoryOpenInNewTab = mandatory;
			return this;
		}

		///// build 
		public VaadinWebLinkFormFeatures build() {
			return new VaadinWebLinkFormFeatures(
				_showLinkTextInput,
				_requiredLinkTextInput,
				_showLinkDescriptionInput,
				_showLinkOpeningFeatures,
				_mandatoryOpenInNewTab
			);
		}
	}
	public static VaadinWebLinkFormFeatures byDefault() {
		return VaadinWebLinkFormFeatures.builder()
										.build();
	}
	
	
/**
 * DEPRECATED!, please use the new builder pattern implementation:
 */
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	public static R01UIUrlBuilderWebLinkFormFeaturesLinkTextRequireStep showLinkTextInput(final boolean show) {
		return new R01UIUrlBuilderWebLinkFormFeaturesLinkTextRequireStep(show);
	}
	@Deprecated
	public static R01UIUrlBuilderWebLinkFormFeaturesLinkTextRequireStep showLinkTextInput() {
		return new R01UIUrlBuilderWebLinkFormFeaturesLinkTextRequireStep(true);
	}
	@Deprecated
	public static R01UIUrlBuilderWebLinkFormFeaturesLinkTextRequireStep doNOTShowLinkTextInput() {
		return new R01UIUrlBuilderWebLinkFormFeaturesLinkTextRequireStep(false);
	}
	
	@Deprecated
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class R01UIUrlBuilderWebLinkFormFeaturesLinkTextRequireStep {
		private final boolean _showLinkTextInput;
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesDescriptionShowStep requireLinkTextInput(final boolean require) {
			return new R01UIUrlBuilderWebLinkFormFeaturesDescriptionShowStep(_showLinkTextInput,
																			 require);
		}
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesDescriptionShowStep requireLinkTextInput() {
			return new R01UIUrlBuilderWebLinkFormFeaturesDescriptionShowStep(_showLinkTextInput,
																			 true);
		}
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesDescriptionShowStep doNOTRequireLinkTextInput() {
			return new R01UIUrlBuilderWebLinkFormFeaturesDescriptionShowStep(_showLinkTextInput,
																			 false);
		}
	}
	
	@Deprecated
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class R01UIUrlBuilderWebLinkFormFeaturesDescriptionShowStep {
		private final boolean _showLinkTextInput;
		private final boolean _requiredLinkTextInput;
		
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesOpeningFeaturesShowStep showLinkDescriptionInput(final boolean show) {
			return new R01UIUrlBuilderWebLinkFormFeaturesOpeningFeaturesShowStep(_showLinkTextInput,
																				 _requiredLinkTextInput,
																				 show);
		}
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesOpeningFeaturesShowStep showLinkDescriptionInput() {
			return new R01UIUrlBuilderWebLinkFormFeaturesOpeningFeaturesShowStep(_showLinkTextInput,
																				 _requiredLinkTextInput,
																				 true);
		}
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesOpeningFeaturesShowStep doNOTShowLinkDescriptionInput() {
			return new R01UIUrlBuilderWebLinkFormFeaturesOpeningFeaturesShowStep(_showLinkTextInput,
																				 _requiredLinkTextInput,
																				 false);
		}
	}
	@Deprecated
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class R01UIUrlBuilderWebLinkFormFeaturesOpeningFeaturesShowStep {
		private final boolean _showLinkTextInput;
		private final boolean _requiredLinkTextInput;
		private final boolean _showLinkDescriptionInput;
		
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep showLinkOpeningFeatures(final boolean show) {
			return new R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep(_showLinkTextInput,
																	   _requiredLinkTextInput,
																	   _showLinkDescriptionInput,
																	   show);
		}
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep showLinkOpeningFeatures() {
			return new R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep(_showLinkTextInput,
																	   _requiredLinkTextInput,
																	   _showLinkDescriptionInput,
																	   true);
		}
		@Deprecated
		public R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep doNOTShowLinkOpeningFeatures() {
			return new R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep(_showLinkTextInput,
																	   _requiredLinkTextInput,
																	   _showLinkDescriptionInput,
																	   false);
		}
	}
//	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
//	public static class R01UIUrlBuilderWebLinkFormFeaturesOpeningFeaturesMandatoryNewWindowStep {
//		private final boolean _showLinkTextInput;
//		private final boolean _requiredLinkTextInput;
//		private final boolean _showLinkDescriptionInput;
//		private final boolean _showLinkOpeningFeatures;
//		
//		public R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep mandatoryOpenInNewTab(boolean mandatoryOpenInNewTab) {
//			return new R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep(_showLinkTextInput,
//																	   _requiredLinkTextInput,
//																	   _showLinkDescriptionInput,
//																	   _showLinkOpeningFeatures);
//		}
//		public R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep mandatoryOpenInNewTab() {
//			return new R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep(_showLinkTextInput,
//																	   _requiredLinkTextInput,
//																	   _showLinkDescriptionInput,
//																	   _showLinkOpeningFeatures);
//		}
//		public R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep optionalOpenInNewTab() {
//			return new R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep(_showLinkTextInput,
//																	   _requiredLinkTextInput,
//																	   _showLinkDescriptionInput,
//																	   _showLinkOpeningFeatures);
//		}
//	}
	
	@Deprecated
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class R01UIUrlBuilderWebLinkFormFeaturesBuildShowStep {
		private final boolean _showLinkTextInput;
		private final boolean _requiredLinkTextInput;
		private final boolean _showLinkDescriptionInput;
		private final boolean _showLinkOpeningFeatures;
		/**
		 * BEWARE!
		 * It's new feature. To maintain compatibility it's created internally with "false" value.
		 * It will be included in new builder pattern.
		 */
		private final boolean _mandatoryOpenInNewTab = false; // by default
		
		@Deprecated
		public VaadinWebLinkFormFeatures build() {
			return new VaadinWebLinkFormFeatures(_showLinkTextInput,
												 _requiredLinkTextInput,
												 _showLinkDescriptionInput,
												 _showLinkOpeningFeatures,
												 _mandatoryOpenInNewTab);
		}
	}
}
