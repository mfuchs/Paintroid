/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration;

import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import com.robotium.solo.Solo;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@Ignore
public class MainActivityIntegrationTest extends BaseIntegrationTestClass {

	public MainActivityIntegrationTest() throws Exception {
		super();
	}

	@Test
	@Ignore("Broken")
	public void testMenuTermsOfUseAndService() {

		String buttonTermsOfUseAndService = getActivity().getString(R.string.menu_terms_of_use_and_service);
		openNavigationDrawer();
		mSolo.clickOnText(buttonTermsOfUseAndService);

		String termsOfUseAndServiceTextExpected = getActivity().getString(R.string.terms_of_use_and_service_content);

		assertTrue("Terms of Use and Service dialog text not correct, maybe Dialog not started as expected",
				mSolo.waitForText(termsOfUseAndServiceTextExpected, 1, TIMEOUT, true, false));

		mSolo.goBack();
	}

	@Test
	public void testMenuAbout() {

		String buttonAbout = getActivity().getString(R.string.menu_about);
		openNavigationDrawer();
		mSolo.clickOnText(buttonAbout);

		String aboutTextExpected = getActivity().getString(R.string.about_content);
		String licenseText = getActivity().getString(R.string.license_type_paintroid);
		aboutTextExpected = String.format(aboutTextExpected, licenseText);

		assertTrue("About dialog text not correct, maybe Dialog not started as expected",
				mSolo.waitForText(aboutTextExpected, 1, TIMEOUT, true, false));
		mSolo.goBack();
	}

	@Test
	public void testHelpDialogForBrush() {
		toolHelpTest(ToolType.BRUSH, R.string.help_content_brush);
	}

	@Test
	@Ignore
	public void testHelpDialogForCursor() {
		toolHelpTest(ToolType.CURSOR, R.string.help_content_cursor);
	}

	@Test
	public void testHelpDialogForPipette() {
		toolHelpTest(ToolType.PIPETTE, R.string.help_content_eyedropper);
	}

	@Test
	public void testHelpDialogForStamp() {
		toolHelpTest(ToolType.STAMP, R.string.help_content_stamp);
	}

	@Test
	public void testHelpDialogForBucket() {
		toolHelpTest(ToolType.FILL, R.string.help_content_fill);
	}

	@Test
	public void testHelpDialogForShape() {
		toolHelpTest(ToolType.SHAPE, R.string.help_content_shape);
	}

	@Test
	public void testHelpDialogForTransform() {
		toolHelpTest(ToolType.TRANSFORM, R.string.help_content_transform);
	}

	@Test
	public void testHelpDialogForEraser() {
		toolHelpTest(ToolType.ERASER, R.string.help_content_eraser);
	}

	@Test
	@Ignore
	public void testHelpDialogForImportImage() {
		toolHelpTest(ToolType.IMPORTPNG, R.string.help_content_import_png);
	}


	@Test
	public void testHelpDialogForText() {
		toolHelpTest(ToolType.TEXT, R.string.help_content_text);
	}

	private void toolHelpTest(ToolType toolToClick, int idExpectedHelptext) {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurface.class));
		clickLongOnTool(toolToClick);

		String helpTextExpected = mSolo.getString(idExpectedHelptext);
		String buttonDoneTextExpected = mSolo.getString(android.R.string.ok);
		String toolNameInHelperDialog = mSolo.getString(toolToClick.getNameResource());

		assertTrue("Help text not found", mSolo.searchText(helpTextExpected, true));
		assertTrue("Done button not found", mSolo.searchButton(buttonDoneTextExpected, true));
		assertTrue("Wrong or missing tool name in dialog", mSolo.searchText(toolNameInHelperDialog, true));

		mSolo.clickOnButton(buttonDoneTextExpected);
		mSolo.waitForDialogToClose();

		assertFalse("Help text still present", mSolo.searchText(helpTextExpected, true));
	}
}
