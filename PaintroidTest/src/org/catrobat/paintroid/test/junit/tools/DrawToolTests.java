/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.tools;

import static org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPaintEquals;
import static org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPathEquals;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.dialog.BrushPickerDialog;
import org.catrobat.paintroid.dialog.BrushPickerDialog.OnBrushChangedListener;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import org.catrobat.paintroid.test.junit.stubs.PathStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;
import org.junit.Test;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PointF;

public class DrawToolTests extends BaseToolTest {

	public DrawToolTests() {
		super();
	}

	@Override
	public void setUp() throws Exception {
		mToolToTest = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
		super.setUp();
	}

	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();

		assertEquals(ToolType.BRUSH, toolType);
	}

	public void testShouldReturnPaint() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		mToolToTest.setDrawPaint(this.mPaint);
		Paint drawPaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, mToolToTest, "mBitmapPaint");
		assertEquals(this.mPaint.getColor(), drawPaint.getColor());
		assertEquals(this.mPaint.getStrokeWidth(), drawPaint.getStrokeWidth());
		assertEquals(this.mPaint.getStrokeCap(), drawPaint.getStrokeCap());
		assertEquals(this.mPaint.getShader(), drawPaint.getShader());
	}

	// down event
	public void testShouldMovePathOnDownEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, mToolToTest, "pathToDraw", pathStub);

		boolean returnValue = mToolToTest.handleDown(event);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		List<Object> arguments = pathStub.getCall("moveTo", 0);
		assertEquals(event.x, arguments.get(0));
		assertEquals(event.y, arguments.get(1));
	}

	public void testShouldNotAddCommandOnDownEvent() {
		PointF event = new PointF(0, 0);

		boolean returnValue = mToolToTest.handleDown(event);

		assertTrue(returnValue);
		assertEquals(0, mCommandManagerStub.getCallCount("commitCommand"));
	}

	public void testShouldNotStartPathIfNoCoordinateOnDownEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, mToolToTest, "pathToDraw", pathStub);

		boolean returnValue = mToolToTest.handleDown(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("reset"));
		assertEquals(0, pathStub.getCallCount("moveTo"));
	}

	// move event
	public void testShouldMovePathOnMoveEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(5, 6);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, mToolToTest, "pathToDraw", pathStub);

		mToolToTest.handleDown(event1);
		boolean returnValue = mToolToTest.handleMove(event2);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		List<Object> arguments = pathStub.getCall("quadTo", 0);
		final float cx = (event1.x + event2.x) / 2;
		final float cy = (event1.y + event2.y) / 2;
		assertEquals(event1.x, arguments.get(0));
		assertEquals(event1.y, arguments.get(1));
		assertEquals(cx, arguments.get(2));
		assertEquals(cy, arguments.get(3));
	}

	public void testShouldNotAddCommandOnMoveEvent() {
		PointF event = new PointF(0, 0);

		mToolToTest.handleDown(event);
		boolean returnValue = mToolToTest.handleMove(event);

		assertTrue(returnValue);
		assertEquals(0, mCommandManagerStub.getCallCount("commitCommand"));
	}

	public void testShouldNotMovePathIfNoCoordinateOnMoveEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, mToolToTest, "pathToDraw", pathStub);

		mToolToTest.handleDown(event);
		boolean returnValue = mToolToTest.handleMove(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("quadTo"));
	}

	// up event
	public void testShouldMovePathOnUpEvent() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE, PaintroidApplication.MOVE_TOLLERANCE);
		PointF event3 = new PointF(PaintroidApplication.MOVE_TOLLERANCE * 2, -PaintroidApplication.MOVE_TOLLERANCE);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, mToolToTest, "pathToDraw", pathStub);

		mToolToTest.handleDown(event1);
		mToolToTest.handleMove(event2);
		boolean returnValue = mToolToTest.handleUp(event3);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		assertEquals(1, pathStub.getCallCount("lineTo"));
		List<Object> arguments = pathStub.getCall("lineTo", 0);
		assertEquals(event3.x, arguments.get(0));
		assertEquals(event3.y, arguments.get(1));
	}

	public void testShouldNotMovePathIfNoCoordinateOnUpEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, mToolToTest, "pathToDraw", pathStub);

		mToolToTest.handleDown(event);
		mToolToTest.handleMove(event);
		boolean returnValue = mToolToTest.handleUp(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("lineTo"));
	}

	public void testShouldAddCommandOnUpEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PointF event1 = new PointF(PaintroidApplication.MOVE_TOLLERANCE + 0.1f, 0);
		PointF event2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE + 2, PaintroidApplication.MOVE_TOLLERANCE + 2);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, mToolToTest, "pathToDraw", pathStub);

		mToolToTest.handleDown(event);
		mToolToTest.handleMove(event1);
		boolean returnValue = mToolToTest.handleUp(event2);

		assertTrue(returnValue);
		assertEquals(1, mCommandManagerStub.getCallCount("commitCommand"));
		Command command = (Command) mCommandManagerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PathCommand);
		Path path = (Path) PrivateAccess.getMemberValue(PathCommand.class, command, "mPath");
		assertPathEquals(pathStub, path);
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "mPaint");
		assertPaintEquals(this.mPaint, paint);
	}

	public void testShouldNotAddCommandIfNoCoordinateOnUpEvent() {
		PointF event = new PointF(0, 0);

		mToolToTest.handleDown(event);
		mToolToTest.handleMove(event);
		boolean returnValue = mToolToTest.handleUp(null);

		assertFalse(returnValue);
		assertEquals(0, mCommandManagerStub.getCallCount("commitCommand"));
	}

	// tab event
	public void testShouldAddCommandOnTabEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF tab = new PointF(0, 0);

		boolean returnValue1 = mToolToTest.handleDown(tab);
		boolean returnValue2 = mToolToTest.handleUp(tab);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		assertEquals(1, mCommandManagerStub.getCallCount("commitCommand"));
		Command command = (Command) mCommandManagerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		PointF point = (PointF) PrivateAccess.getMemberValue(PointCommand.class, command, "mPoint");
		assertTrue(tab.equals(point.x, point.y));
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "mPaint");
		assertPaintEquals(this.mPaint, paint);
	}

	public void testShouldAddCommandOnTabWithinTolleranceEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF tab1 = new PointF(0, 0);
		PointF tab2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE - 0.1f, 0);
		PointF tab3 = new PointF(PaintroidApplication.MOVE_TOLLERANCE - 0.1f,
				PaintroidApplication.MOVE_TOLLERANCE - 0.1f);

		boolean returnValue1 = mToolToTest.handleDown(tab1);
		boolean returnValue2 = mToolToTest.handleMove(tab2);
		boolean returnValue3 = mToolToTest.handleUp(tab3);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		assertTrue(returnValue3);
		assertEquals(1, mCommandManagerStub.getCallCount("commitCommand"));
		Command command = (Command) mCommandManagerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		PointF point = (PointF) PrivateAccess.getMemberValue(PointCommand.class, command, "mPoint");
		assertTrue(tab1.equals(point.x, point.y));
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "mPaint");
		assertPaintEquals(this.mPaint, paint);
	}

	public void testShouldAddPathCommandOnMultipleMovesWithinTolleranceEvent() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		PointF tab1 = new PointF(0, 0);
		PointF tab2 = new PointF(0, PaintroidApplication.MOVE_TOLLERANCE - 0.1f);
		PointF tab3 = new PointF(0, 0);
		PointF tab4 = new PointF(0, -PaintroidApplication.MOVE_TOLLERANCE + 0.1f);
		PointF tab5 = new PointF(0, 0);

		mToolToTest.handleDown(tab1);
		mToolToTest.handleMove(tab2);
		mToolToTest.handleMove(tab3);
		mToolToTest.handleMove(tab4);
		mToolToTest.handleUp(tab5);

		assertEquals(1, mCommandManagerStub.getCallCount("commitCommand"));
		Command command = (Command) mCommandManagerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PathCommand);
	}

	public void testShouldRewindPathOnAppliedToBitmap() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, mToolToTest, "pathToDraw", pathStub);

		mToolToTest.resetInternalState();

		assertEquals(1, pathStub.getCallCount("rewind"));
	}

	public void testShouldReturnBlackForForTopParameterButton() {
		int color = mToolToTest.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals(Color.BLACK, color);
	}

	public void testShouldReturnCorrectResourceForForTopParameterButtonIfColorIsTransparent() {
		mToolToTest.changePaintColor(Color.TRANSPARENT);
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals(R.drawable.checkeredbg_repeat, resource);
	}

	public void testShouldReturnNoResourceForForTopParameterButtonIfColorIsNotTransparent() {
		mToolToTest.changePaintColor(Color.RED);
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals(R.drawable.icon_menu_no_icon, resource);
	}

	public void testShouldStartColorPickerForTopParameterButtonClick() {

		mToolToTest.attributeButtonClick(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals(1, mColorPickerStub.getCallCount("setInitialColor"));
		assertEquals(this.mPaint.getColor(), mColorPickerStub.getCall("setInitialColor", 0).get(0));
		assertEquals(1, mColorPickerStub.getCallCount("show"));
	}

	public void testShouldChangePaintFromColorPicker() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		mToolToTest = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
		mToolToTest.setDrawPaint(this.mPaint);
		ColorPickerDialog colorPicker = (ColorPickerDialog) PrivateAccess.getMemberValue(BaseTool.class,
				this.mToolToTest, "mColorPickerDialog");
		ArrayList<OnColorPickedListener> colorPickerListener = (ArrayList<OnColorPickedListener>) PrivateAccess
				.getMemberValue(ColorPickerDialog.class, colorPicker, "mOnColorPickedListener");

		for (OnColorPickedListener onColorPickedListener : colorPickerListener) {
			onColorPickedListener.colorChanged(Color.RED);
			assertEquals(Color.RED, mToolToTest.getDrawPaint().getColor());
		}

	}

	public void testShouldChangePaintFromBrushPicker() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		mToolToTest = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
		mToolToTest.setDrawPaint(this.mPaint);
		BrushPickerDialog brushPicker = (BrushPickerDialog) PrivateAccess.getMemberValue(BaseTool.class,
				this.mToolToTest, "mBrushPickerDialog");
		ArrayList<OnBrushChangedListener> brushPickerListener = (ArrayList<OnBrushChangedListener>) PrivateAccess
				.getMemberValue(BrushPickerDialog.class, brushPicker, "mBrushChangedListener");

		for (OnBrushChangedListener onBrushChangedListener : brushPickerListener) {
			onBrushChangedListener.setCap(Cap.ROUND);
			onBrushChangedListener.setStroke(15);
			assertEquals(Cap.ROUND, mToolToTest.getDrawPaint().getStrokeCap());
			assertEquals(15f, mToolToTest.getDrawPaint().getStrokeWidth());
		}
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Draw tool icon should be displayed", R.drawable.icon_menu_brush, resource);
	}
}