package org.catrobat.paintroid.test.integration.tools;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ImportPngToolTest extends BaseIntegrationTestClass {

	private static String FAILING_FILE_NAME = "thisisnofile";

	public ImportPngToolTest() throws Exception {
		super();
	}

	@Test
	public void testIconsInitial() {
		// TODO: mock gallery/contentprovider
		// mSolo.sleep(1000);
		// selectTool(ToolType.IMPORTPNG);
		// StampTool stampTool = (ImportTool) PaintroidApplication.currentTool;
		// assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
		// stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
	}
}
