package org.eclipse.birt.report.designer.internal.ui.views.attributes.provider;

import java.util.Iterator;

import org.eclipse.birt.report.designer.core.model.SessionHandleAdapter;
import org.eclipse.birt.report.designer.internal.ui.dialogs.FormatAdapter;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.birt.report.designer.ui.util.ExceptionUtil;
import org.eclipse.birt.report.designer.util.DEUtil;
import org.eclipse.birt.report.model.api.CommandStack;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.FormatValueHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.StyleHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.structures.FormatValue;
import org.eclipse.birt.report.model.elements.interfaces.IStyleModel;

import com.ibm.icu.util.ULocale;

/**
 * FormatNumberDescriptorProvider
 */
public class FormatNumberDescriptorProvider extends FormatDescriptorProvider {

	private Object input;

	public String getDisplayName() {
		return null;
	}

	public void save(Object value) throws SemanticException {
		String[] result = (String[]) value;
		if (result.length == 3) {
			CommandStack stack = SessionHandleAdapter.getInstance()
					.getCommandStack();
			stack.startTrans(Messages
					.getString("FormatNumberAttributePage.Trans.SetNumberFormat")); //$NON-NLS-1$

			for (Iterator iter = DEUtil.getInputElements(input).iterator(); iter
					.hasNext();) {
				DesignElementHandle element = (DesignElementHandle) iter.next();
				try {
					if (result[0] == null && result[1] == null) {
						element.setProperty(IStyleModel.NUMBER_FORMAT_PROP,
								null);
					} else {
						element.getPrivateStyle().setNumberFormatCategory(
								result[0]);
						element.getPrivateStyle().setNumberFormat(result[1]);
					}

					if (element.getPrivateStyle() != null) {
						StyleHandle style = element.getPrivateStyle();
						Object formatValue = style
								.getProperty(IStyleModel.NUMBER_FORMAT_PROP);
						if (formatValue instanceof FormatValue) {
							PropertyHandle propHandle = style
									.getPropertyHandle(IStyleModel.NUMBER_FORMAT_PROP);
							FormatValue formatValueToSet = (FormatValue) formatValue;
							FormatValueHandle formatHandle = (FormatValueHandle) formatValueToSet
									.getHandle(propHandle);
							if (result[2] != null)
								formatHandle.setLocale(FormatAdapter
										.getLocaleByDisplayName(result[2]));
						}
					}
				} catch (SemanticException e) {
					stack.rollbackAll();
					ExceptionUtil.handle(e);
				}
			}
			stack.commit();
		}
	}

	public void setInput(Object input) {
		this.input = input;
	}

	public Object load() {
		if (DEUtil.getInputElements(input).isEmpty()) {
			return null;
		}
		String baseCategory = ((DesignElementHandle) DEUtil
				.getInputFirstElement(input)).getPrivateStyle()
				.getNumberFormatCategory();
		String basePattern = ((DesignElementHandle) DEUtil
				.getInputFirstElement(input)).getPrivateStyle()
				.getNumberFormat();

		String baseLocale = FormatAdapter.NONE;
		DesignElementHandle element = ((DesignElementHandle) DEUtil
				.getInputFirstElement(input));
		if (element.getPrivateStyle() != null) {
			StyleHandle style = element.getPrivateStyle();
			Object formatValue = style
					.getProperty(IStyleModel.NUMBER_FORMAT_PROP);
			if (formatValue instanceof FormatValue) {
				PropertyHandle propHandle = style
						.getPropertyHandle(IStyleModel.NUMBER_FORMAT_PROP);
				FormatValue formatValueToSet = (FormatValue) formatValue;
				FormatValueHandle formatHandle = (FormatValueHandle) formatValueToSet
						.getHandle(propHandle);
				ULocale uLocale = formatHandle.getLocale();
				if (uLocale != null)
					baseLocale = uLocale.getDisplayName();
			}
		}

		for (Iterator iter = DEUtil.getInputElements(input).iterator(); iter
				.hasNext();) {
			DesignElementHandle handle = (DesignElementHandle) iter.next();
			String category = handle.getPrivateStyle()
					.getNumberFormatCategory();
			String pattern = handle.getPrivateStyle().getNumberFormat();
			String locale = FormatAdapter.NONE;

			if (handle.getPrivateStyle() != null) {
				StyleHandle style = handle.getPrivateStyle();
				Object formatValue = style
						.getProperty(IStyleModel.NUMBER_FORMAT_PROP);
				if (formatValue instanceof FormatValue) {
					PropertyHandle propHandle = style
							.getPropertyHandle(IStyleModel.NUMBER_FORMAT_PROP);
					FormatValue formatValueToSet = (FormatValue) formatValue;
					FormatValueHandle formatHandle = (FormatValueHandle) formatValueToSet
							.getHandle(propHandle);
					ULocale uLocale = formatHandle.getLocale();
					if (uLocale != null)
						locale = uLocale.getDisplayName();
				}
			}

			if (((baseCategory == null && category == null) || (baseCategory != null && baseCategory
					.equals(category)))
					&& ((basePattern == null && pattern == null) || (basePattern != null && basePattern
							.equals(pattern)))
					&& ((baseLocale == null && locale == null) || (baseLocale != null && baseLocale
							.equals(locale)))) {
				continue;
			}
			return null;
		}
		return new String[] { baseCategory, basePattern, baseLocale };
	}

	public boolean canReset() {
		return true;
	}

	public void reset() throws SemanticException {
		CommandStack stack = SessionHandleAdapter.getInstance()
				.getCommandStack();
		stack.startTrans(Messages
				.getString("FormatNumberAttributePage.Trans.SetNumberFormat")); //$NON-NLS-1$

		for (Iterator iter = DEUtil.getInputElements(input).iterator(); iter
				.hasNext();) {
			DesignElementHandle element = (DesignElementHandle) iter.next();
			element.setProperty(IStyleModel.NUMBER_FORMAT_PROP, null);
		}
		stack.commit();
	}
}
